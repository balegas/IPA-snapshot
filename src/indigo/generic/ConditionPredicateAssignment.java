package indigo.generic;

import java.util.List;

import indigo.interfaces.logic.Invariant;
import indigo.interfaces.operations.Parameter;
import indigo.invariants.LogicExpression;

public class ConditionPredicateAssignment {

	private final Invariant constraint;
	private final List<Parameter> params;
	private final String predicateName;

	public ConditionPredicateAssignment(String predicateName, List<Parameter> params, Invariant constraint) {
		this.predicateName = predicateName;
		this.params = params;
		this.constraint = constraint;
	}

	public LogicExpression toLogicExpression() {
		return constraint.toLogicExpression();
	}

	public String getPredicateName() {
		return predicateName;
	}

}
