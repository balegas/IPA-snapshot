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
	public boolean applyEffectOnLogicExpression(LogicExpression wpc, int i) {
		return effect.applyEffect(wpc, i);
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

	// TODO: ATTENTION -- This is not a real copy, however the algorithm seems
	// to be working fine. Must check that either the applyEffects generates a
	// new copy of the object somewhere in between, or it just works because the
	// order of checking conflicts is hiding the error
	@Override
	public PredicateAssignment copyOf() {
		return new JavaPredicateAssignment(effect);
	}
}
