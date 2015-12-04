package indigo.impl.javaclass.effects;

import indigo.Bindings;
import indigo.annotations.Assigns;
import indigo.invariants.LogicExpression;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.z3.BoolExpr;

public class AssignPredicate extends Predicate {

	List<BoolExpr> effects = new ArrayList<>();
	String arg;

	AssignPredicate(Method m, Assigns a) {
		super(true, m, a.value().split(":=")[0]);
		arg = a.value().split(":=")[1];
	}

	static public List<AssignPredicate> listFor(Method m) {
		List<AssignPredicate> res = new ArrayList<>();

		for (Assigns i : m.getAnnotationsByType(Assigns.class))
			res.add(new AssignPredicate(m, i));

		return res;
	}

	@Override
	public String toString() {
		return predicateName + "--->" + value;
	}

	@Override
	public boolean applyEffect(LogicExpression le, int iteration) {
		Bindings matches = le.matches(predicateName);
		if (matches != null) {
			le.assertion(String.format("%s = %s", predicateName, arg));
			return true;
		}
		return false;
	}

}
