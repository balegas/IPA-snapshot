package indigo.interfaces;

import indigo.Parser.Expression;
import indigo.invariants.LogicExpression;

public interface PredicateAssignment {

	// public Predicate getLeftHandSide();

	public boolean applyEffectOnLogicExpression(LogicExpression wpc, int i);

	public PredicateType getType();

	// public Operation getOperation();

	public Expression getAssertion();

	public boolean hasEffectIn(Clause clause);

	public String opName();

	// public InvariantExpression toInvExpression();

	public PredicateAssignment copyOf();

}
