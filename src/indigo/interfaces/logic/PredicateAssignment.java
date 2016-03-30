package indigo.interfaces.logic;

import java.util.List;

import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.Parameter;
import indigo.invariants.LogicExpression;
import indigo.runtime.Parser.Expression;

public interface PredicateAssignment {

	public Expression expression();

	public PREDICATE_TYPE getType();

	public boolean isType(PREDICATE_TYPE type);

	public PredicateAssignment copyOf();

	public String getOperationName();

	public String getPredicateName();

	public Value getAssignedValue();

	public List<Parameter> getParams();

	public void applyEffect(LogicExpression e, int iteration);

	boolean affects(Invariant otherClause);

	public void updateParamTypes(List<Expression> params);

	public Value negateValue();

}
