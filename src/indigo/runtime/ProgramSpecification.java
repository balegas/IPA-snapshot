package indigo.runtime;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import indigo.interfaces.interactive.ConflictResolutionPolicy;
import indigo.interfaces.logic.Invariant;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.operations.Operation;

public interface ProgramSpecification {

	public Set<Operation> getOperations();

	public Set<String> getOperationsNames();

	public Set<Invariant> getInvariantClauses();

	public String getAppName();

	public Invariant newEmptyInv();

	public ConflictResolutionPolicy getDefaultConflictResolutionPolicy();

	void updateOperations(Collection<Operation> newOperations);

	public Invariant invariantFor(Collection<String> asSet, AnalysisContext context);

	public Map<String, Set<PredicateAssignment>> getDependenciesForPredicate();

}
