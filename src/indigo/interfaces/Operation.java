package indigo.interfaces;

import java.util.Collection;
import java.util.List;

public interface Operation {

	public String opName();

	public Collection<PredicateAssignment> getEffects();

	public List<Parameter> getParameters();

	public boolean isSubset(Operation otherOp);

}
