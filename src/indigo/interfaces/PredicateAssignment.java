package indigo.interfaces;

import java.util.List;

import indigo.Parser.Expression;
import indigo.invariants.LogicExpression;

public interface PredicateAssignment {

	public Expression getExpression();

	public PREDICATE_TYPE getType();

	public boolean isType(PREDICATE_TYPE type);

	public PredicateAssignment copyOf();

	public String getOperationName();

	public String getPredicateName();

	public Value getAssignedValue();

	public List<Parameter> getParams();

	public void applyEffect(LogicExpression e, int iteration);

	boolean affects(Invariant otherClause);

}
