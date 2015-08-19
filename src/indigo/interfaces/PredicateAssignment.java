package indigo.abtract;

import indigo.Parser.Expression;
import indigo.invariants.InvariantExpression;

public interface PredicateAssignment {

	// public Predicate getLeftHandSide();

	public void applyEffect(InvariantExpression wpc, int i);

	public boolean isNumeric();

	// public Operation getOperation();

	public Expression getAssertion();

	public boolean hasEffectIn(Clause clause);

	public String opName();

	// public InvariantExpression toInvExpression();

	// public String getPredicate();
}
