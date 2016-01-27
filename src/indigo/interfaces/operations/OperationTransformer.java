package indigo.interfaces.operations;

import java.util.Collection;

import indigo.interfaces.logic.PredicateAssignment;

public interface OperationTransformer {

	public Collection<PredicateAssignment> transformEffects(Collection<PredicateAssignment> set);

}
