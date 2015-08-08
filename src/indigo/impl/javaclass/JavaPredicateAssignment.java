package indigo.impl.javaclass;

import indigo.Parser.Expression;
import indigo.abtract.Clause;
import indigo.abtract.PredicateAssignment;
import indigo.effects.CounterPredicate;
import indigo.effects.Effect;
import indigo.invariants.InvariantExpression;

public class JavaPredicateAssignment implements PredicateAssignment {

	private final Effect effect;

	public JavaPredicateAssignment(Effect e) {
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

	public static JavaPredicateAssignment fromEffect(Effect e) {
		return new JavaPredicateAssignment(e);
	}

	@Override
	public void applyEffect(InvariantExpression wpc, int i) {
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
		return effect.hasEffects(clause.toInvExpression());
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
