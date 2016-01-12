package indigo;

import java.util.Collection;
import java.util.Set;

import indigo.interfaces.PredicateAssignment;

public interface OperationTest extends Comparable<OperationTest> {

	boolean isConflicting();

	boolean isSelfConflicting();

	boolean isNonIdempotent();

	boolean isOpposing();

	Collection<String> asSet();

	boolean isOK();

	boolean isValid();

	void addCounterExample(Collection<PredicateAssignment> model, AnalysisContext context);

	Set<PredicateAssignment> getCounterExample();

	Collection<CONFLICT_TYPE> getConflicts();

	void setInvalidWPC();

	void setConflicting();

	boolean isModified();

}
