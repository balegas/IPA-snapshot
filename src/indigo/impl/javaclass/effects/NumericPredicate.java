package indigo.effects;

import indigo.annotations.Numeric;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NumericPredicate extends Predicate {

	protected NumericPredicate(boolean value, Method method, String args) {
		super(value, method, args);
	}

	public boolean isNumeric() {
		return true;
	}

	static public List<NumericPredicate> listFor(Method m) {
		List<NumericPredicate> res = new ArrayList<>();

		for (Numeric i : m.getAnnotationsByType(Numeric.class))
			res.add(new NumericPredicate(true, m, i.toString()));

		return res;
	}
}
