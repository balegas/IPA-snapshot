package indigo.interfaces.operations;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import indigo.interfaces.logic.PredicateAssignment;

public interface Operation {

	public String opName();

	public Collection<PredicateAssignment> getEffects();

	public List<Parameter> getParameters();

	public boolean isSubset(Operation otherOp);

	public Set<PredicateAssignment> getPreConditions();

	public boolean containsPredicate(String predicateName);

}
