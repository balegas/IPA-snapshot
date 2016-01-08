package indigo.interfaces;

import indigo.invariants.LogicExpression;

public interface Clause<T extends Clause<T>> {

	public T mergeClause(T next);

	// public T copyOf();

	public LogicExpression toLogicExpression();

}
