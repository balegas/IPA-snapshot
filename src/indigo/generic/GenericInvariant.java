package indigo.generic;

import indigo.interfaces.Clause;
import indigo.interfaces.Invariant;
import indigo.invariants.LogicExpression;

public class GenericInvariant implements Invariant {

	Clause<Invariant> invariant;

	public GenericInvariant(Clause<Invariant> invariant) {
		this.invariant = invariant;
	}

	@Override
	public Invariant mergeClause(Invariant next) {
		return new GenericInvariant(invariant.mergeClause(next));
	}

	@Override
	public LogicExpression toLogicExpression() {
		return invariant.toLogicExpression();
	}

	@Override
	public boolean affectedBy(String predicateName) {
		return !invariant.toLogicExpression().matches(predicateName).isEmpty();
	}
}
