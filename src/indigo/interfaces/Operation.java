package indigo.interfaces;

import java.util.Collection;

public interface Operation {

	public String opName();

	public Collection<PredicateAssignment> getEffects();

}
