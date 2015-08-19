package indigo.impl.javaclass;

import indigo.Parser.Expression;
import indigo.impl.javaclass.effects.CounterPredicate;
import indigo.impl.javaclass.effects.JavaEffect;
import indigo.interfaces.Clause;
import indigo.interfaces.PredicateAssignment;
import indigo.invariants.LogicExpression;

public class JavaPredicateAssignment implements PredicateAssignment {

	private final JavaEffect effect;

	public JavaPredicateAssignment(JavaEffect e) {
		this.effect = e;
	}

	@Override
	public int hashCode() {
		return effect.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof JavaPredicateAssignment) {
			return this.effect.equals(((JavaPredicateAssignment) o).effect);
		} else {
			return false;
		}
	}

	public static JavaPredicateAssignment fromEffect(JavaEffect e) {
		return new JavaPredicateAssignment(e);
	}

	@Override
	public void applyEffect(LogicExpression wpc, int i) {
		effect.applyEffect(wpc, i);
	}

	@Override
	public boolean isNumeric() {
		return effect instanceof CounterPredicate;
	}

	@Override
	public Expression getAssertion() {
		return effect.assertion();
	}

	@Override
	public boolean hasEffectIn(Clause clause) {
		return effect.hasEffects(((JavaInvariantClause) clause).toLogicExpression());
	}

	@Override
	public String toString() {
		return effect.toString();
	}

	@Override
	public String opName() {
		return effect.operation;
	}

}
