package indigo;

import indigo.interfaces.Clause;
import indigo.interfaces.Operation;
import indigo.interfaces.PredicateAssignment;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ProgramSpecification {

	public Set<Operation> getOperations();

	public Collection<PredicateAssignment> getAllOperationEffects();

	public Set<Clause> getInvariantClauses();

	public String getAppName();

	// public Clause newEmptyInvClause();

	public Map<PredicateAssignment, Set<Clause>> collectInvariantsForPredicate();

	public Clause newTrueClause();

}
