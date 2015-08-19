package indigo.interfaces;

import indigo.invariants.LogicExpression;

public interface Clause {

	// public boolean contains(Predicate e);

	public Clause mergeClause(Clause next) throws Exception;

	// public InvariantExpression toInvExpression();

	public Clause copyOf();

	public boolean isNumeric();

	public LogicExpression toLogicExpression();

}
