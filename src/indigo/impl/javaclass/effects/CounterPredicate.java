/**
-------------------------------------------------------------------

Copyright (c) 2014 SyncFree Consortium.  All Rights Reserved.

This file is provided to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License.  You may obtain
a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

-------------------------------------------------------------------
**/
package indigo.impl.javaclass.effects;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import indigo.annotations.Decrements;
import indigo.annotations.Increments;
import indigo.invariants.LogicExpression;
import indigo.runtime.Bindings;
import indigo.runtime.Parser;

public class CounterPredicate extends Predicate {

	// List<BoolExpr> effects = new ArrayList<>();
	final boolean positive;

	CounterPredicate(Method m, Increments inc) {
		super(true, m, inc.value() + " = 1");
		positive = true;
	}

	CounterPredicate(Method m, Decrements dec) {
		super(false, m, dec.value() + " = -1");
		positive = false;
	}

	static public List<CounterPredicate> listFor(Method m) {
		List<CounterPredicate> res = new ArrayList<>();

		for (Increments i : m.getAnnotationsByType(Increments.class))
			res.add(new CounterPredicate(m, i));

		for (Decrements i : m.getAnnotationsByType(Decrements.class))
			res.add(new CounterPredicate(m, i));

		return res;
	}

	String effect(int iteration) {
		Parameter[] pm = method.getParameters();
		Pattern p = Pattern.compile("\\$\\d+");
		Matcher mm = p.matcher(annotation);

		String res = annotation;
		while (mm.find()) {
			String num = annotation.substring(mm.start(), mm.end());
			int param = Integer.valueOf(num.substring(1));

			res = res.replace(num,
					String.format(" %s : %s%s ", pm[param].getType().getSimpleName(), pm[param].getName(), iteration));
		}
		return res;
	}

	// @Override
	// public boolean hasEffects(LogicExpression invariant) {
	// return invariant.matches(predicateName).size() > 0;
	// }

	@Override
	public boolean applyEffect(LogicExpression le, int iteration) {
		String function = effect(iteration);
		String effect = String.format("(%s %s 1)", function, "+");
		Bindings matches = le.matches(function);
		if (matches != null) {
			matches.entrySet().stream().findAny().ifPresent(e -> {
				le.replace(e.getKey().toString(), effect);
				Bindings vars = Parser.match(e.getKey(), e.getValue());
				vars.forEach((k, v) -> {
					le.replace(k.toString(), v.toString());
				});
			});
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return predicateName;
	}

	public boolean isPositive() {
		return positive;
	}

}
