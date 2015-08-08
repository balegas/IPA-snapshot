package indigo.abtract;

import java.util.Collection;

public interface Operation {

	public String opName();

	// public Collection<Clause> effects();

	public Collection<PredicateAssignment> getPredicateAssignments();

}
