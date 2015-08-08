package indigo.abtract;

import indigo.invariants.InvariantExpression;

public interface Clause {

	// public boolean contains(Predicate e);

	public Clause mergeClause(Clause next) throws Exception;

	public InvariantExpression toInvExpression();

	public Clause copyOf();

}
