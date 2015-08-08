package indigo;

import indigo.abtract.Clause;
import indigo.abtract.Operation;
import indigo.abtract.PredicateAssignment;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface AbstractSpecification {

	public Set<Operation> getOperations();

	public Collection<PredicateAssignment> getAllOperationEffects();

	public Set<Clause> getInvariantClauses();

	public String getAppName();

	public Clause newEmptyInvClause();

	public Map<PredicateAssignment, Set<Clause>> collectInvariantsForPredicate();

	public Clause newTrueClause();

}
