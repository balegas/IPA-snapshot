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

	CounterPredicate(Method m, Increments inc) {
		super(true, m, inc.value());
	}

	CounterPredicate(Method m, Decrements dec) {
		super(false, m, dec.value());
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
		String effect = String.format("(%s %s 1)", function, value ? "+" : "-");
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
		return value;
	}

}
