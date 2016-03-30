package indigo.impl.javaclass;

import java.util.List;

import indigo.impl.javaclass.effects.JavaEffect;
import indigo.interfaces.logic.Invariant;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.logic.Value;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.Parameter;
import indigo.invariants.LogicExpression;
import indigo.runtime.Parser.Expression;

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
		if (o instanceof PredicateAssignment) {
			return this.effect.equals(o);
		} else
			return false;
	}

	public static JavaPredicateAssignment fromEffect(JavaEffect e) {
		return new JavaPredicateAssignment(e);
	}

	@Override
	public String toString() {
		return effect.toString();
	}

	@Override
	public String getOperationName() {
		return effect.getOperationName();
	}

	// TODO: ATTENTION -- This is not a real copy, however the algorithm seems
	// to be working fine. Must check that either the applyEffects generates a
	// new copy of the object somewhere in between, or it just works because the
	// order of checking conflicts is hiding the error
	@Override
	public PredicateAssignment copyOf() {
		return new JavaPredicateAssignment(effect);
	}

	@Override
	public PREDICATE_TYPE getType() {
		return effect.getValue().getType();
	}

	@Override
	public String getPredicateName() {
		return effect.getPredicateName();
	}

	@Override
	public Expression expression() {
		return new LogicExpression(effect.applyIterationToEffect(1)).expression();
	}

	@Override
	public void applyEffect(LogicExpression e, int iteration) {
		effect.applyEffect(e, iteration);
	}

	@Override
	public boolean isType(PREDICATE_TYPE type) {
		return effect.getValue().getType().equals(type);
	}

	@Override
	public boolean affects(Invariant invariant) {
		return invariant.affectedBy(effect.getPredicateName());
	}

	@Override
	public Value getAssignedValue() {
		return effect.getValue();
	}

	@Override
	public List<Parameter> getParams() {
		return effect.getParameters();
	}

	@Override
	public void updateParamTypes(List<Expression> params) {
		System.out.println("NOT IMPLEMENTED " + this.getClass());
		System.exit(0);
	}

	@Override
	public Value negateValue() {
		if (effect.getValue().getType().equals(PREDICATE_TYPE.bool)) {
			if (effect.getValue().getValue().equals("true")) {
				return new BooleanValue(false);
			} else {
				return new BooleanValue(true);
			}
		}
		return effect.getValue();
	}

}
