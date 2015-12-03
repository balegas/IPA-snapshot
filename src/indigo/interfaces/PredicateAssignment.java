package indigo.interfaces;

import indigo.Parser.Expression;
import indigo.invariants.LogicExpression;

public interface PredicateAssignment {

	public boolean applyEffectOnLogicExpression(LogicExpression wpc, int i);

	public boolean hasEffectIn(Clause clause);

	public PredicateType getType();

	public Expression getExpression();

	public PredicateAssignment copyOf();

	public String getOperationName();

	public String getPredicateName();

	public String getAssignedValueAsString();

	// public PredicateAssignment copyWithNewValue(String modifiedValue);

}
