package indigo;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Invariant;
import indigo.interfaces.Operation;
import indigo.interfaces.PredicateAssignment;

public interface ProgramSpecification {

	public Set<Operation> getOperations();

	public Set<String> getOperationsNames();

	// public Map<String, Collection<PredicateAssignment>>
	// getAllOperationEffects();

	public Set<Invariant> getInvariantClauses();

	public String getAppName();

	public Map<PredicateAssignment, Set<Invariant>> invariantsAffectedPerPredicateAssignemnt();

	public Invariant newEmptyInv();

	public ConflictResolutionPolicy getDefaultConflictResolutionPolicy();

	void updateOperations(Collection<Operation> newOperations);

}
