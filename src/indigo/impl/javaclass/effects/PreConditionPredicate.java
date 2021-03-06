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

import indigo.annotations.PreFalse;
import indigo.annotations.PreTrue;
import indigo.invariants.LogicExpression;
import indigo.runtime.Bindings;
import indigo.runtime.Parser;

public class PreConditionPredicate extends Predicate {

	final boolean isSimplePredicate;

	PreConditionPredicate(boolean value, Method m, PreTrue predicate) {
		super(value, m, predicate.value());
		this.isSimplePredicate = predicate.value().matches(".*=\\s*[true|false]");
	}

	PreConditionPredicate(boolean value, Method m, PreFalse predicate) {
		super(value, m, predicate.value());
		this.isSimplePredicate = predicate.value().matches(".*=\\s*[true|false]");
	}

	PreConditionPredicate(boolean value, Method m, String predicateValue) {
		super(value, m, predicateValue);
		this.isSimplePredicate = true;
	}

	static String nameFrom(Method m, String args) {
		int i = args.indexOf('(');
		return args.substring(0, i);
	}

	String predicate(int iteration) {
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

	static public List<AssertionPredicate> listFor(Method m) {
		List<AssertionPredicate> res = new ArrayList<>();

		for (PreTrue i : m.getAnnotationsByType(PreTrue.class))
			res.add(new AssertionPredicate(true, m, i.value() + " = true"));
		for (PreFalse i : m.getAnnotationsByType(PreFalse.class))
			res.add(new AssertionPredicate(false, m, i.value() + " = false"));

		return res;
	}

	@Override
	public boolean applyEffect(LogicExpression le, int iteration) {
		String formula = predicate(iteration);
		String predicate = formula.split("=")[0];

		if (!isSimplePredicate) {
			le.assertion(String.format("%s", formula));
			System.err.println("-------->" + le);
			return true;
		}
		Bindings matches = le.matches(predicate);
		if (!matches.isEmpty()) {
			matches.entrySet().stream().findAny().ifPresent(e -> {
				le.replace(e.getKey().toString(), "" + value);
				Bindings vars = Parser.match(e.getKey(), e.getValue());
				vars.forEach((k, v) -> {
					le.replace(k.toString(), v.toString());
				});
			});
			le.assertion(String.format("%s", formula));
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return annotation /* + "-->" + value */;
	}

}
