package indigo.interfaces.operations;

import java.util.Set;

import indigo.interfaces.logic.PredicateAssignment;

public interface OperationTransformer {

	public Set<PredicateAssignment> transformEffects(Set<PredicateAssignment> set);

}
