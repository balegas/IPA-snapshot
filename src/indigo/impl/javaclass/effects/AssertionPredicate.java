package indigo.effects;

import indigo.Bindings;
import indigo.Parser;
import indigo.Parser.Expression;
import indigo.annotations.Assert;
import indigo.annotations.False;
import indigo.annotations.True;
import indigo.invariants.InvariantExpression;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssertionPredicate extends Predicate {

	final List<Parameter> params;

	final boolean isSimplePredicate;

	AssertionPredicate(boolean value, Method m, Assert predicate) {
		super(value, m, predicate.value());
		this.params = null;
		this.isSimplePredicate = predicate.value().matches(".*=\\s*[true|false]");
	}

	AssertionPredicate(boolean value, Method m, String predicateValue) {
		super(value, m, predicateValue);
		this.params = null;
		this.isSimplePredicate = true;
	}

	static String nameFrom(Method m, String args) {
		int i = args.indexOf('(');
		return args.substring(0, i);
	}

	public Expression assertion() {
		return Parser.parse(predicate(1)); // this 1 is fishy...
	}

	String predicate(int iteration) {
		Parameter[] pm = method.getParameters();
		Pattern p = Pattern.compile("\\$\\d+");
		Matcher mm = p.matcher(args);

		String res = args;
		while (mm.find()) {
			String num = args.substring(mm.start(), mm.end());
			int param = Integer.valueOf(num.substring(1));

			res = res.replace(num, String.format(" %s : %s%s ", pm[param].getType().getSimpleName(), pm[param].getName(), iteration));
		}
		return res;
	}

	static public List<AssertionPredicate> listFor(Method m) {
		List<AssertionPredicate> res = new ArrayList<>();

		for (Assert i : m.getAnnotationsByType(Assert.class))
			res.add(new AssertionPredicate(true, m, i));

		for (True i : m.getAnnotationsByType(True.class))
			res.add(new AssertionPredicate(true, m, i.value() + " = true"));

		for (False i : m.getAnnotationsByType(False.class))
			res.add(new AssertionPredicate(false, m, i.value() + " = false"));

		return res;
	}

	@Override
	public boolean hasEffects(InvariantExpression invariant) {
		return !invariant.matches(name).isEmpty();
	}

	@Override
	public boolean applyEffect(InvariantExpression invariant, int iteration) {
		String formula = predicate(iteration);
		String predicate = formula.split("=")[0];

		if (!isSimplePredicate) {
			invariant.assertion(String.format("%s", formula));
			System.err.println("-------->" + invariant);
			return true;
		}
		Bindings matches = invariant.matches(predicate);
		if (!matches.isEmpty()) {
			matches.entrySet().stream().findAny().ifPresent(e -> {
				invariant.replace(e.getKey().toString(), "" + value);
				Bindings vars = Parser.match(e.getKey(), e.getValue());
				vars.forEach((k, v) -> {
					invariant.replace(k.toString(), v.toString());
				});
			});
			invariant.assertion(String.format("%s", formula));
			return true;
		}
		return false;
	}

	public String toString() {
		return args + "-->" + value;
	}
}