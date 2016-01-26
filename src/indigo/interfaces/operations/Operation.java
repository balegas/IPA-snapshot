package indigo.interfaces.operations;

import java.util.Collection;
import java.util.List;

import indigo.interfaces.logic.PredicateAssignment;

public interface Operation {

	public String opName();

	public Collection<PredicateAssignment> getEffects();

	public List<Parameter> getParameters();

	public boolean isSubset(Operation otherOp);

}
