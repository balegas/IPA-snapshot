package indigo.generic;

import java.util.Collection;
import java.util.Set;

import indigo.AnalysisContext;
import indigo.interfaces.PredicateAssignment;

public interface OperationTest extends Comparable<OperationTest> {

	boolean isConflicting();

	boolean isSelfConflicting();

	boolean isNonIdempotent();

	boolean isOpposing();

	boolean isConflictSolved();

	boolean isOK();

	boolean isValidWPC();

	boolean isModified();

	void setInvalidWPC();

	void setConflicting();

	void setConflictSolved();

	void setIgnored();

	void addCounterExample(Collection<PredicateAssignment> model, AnalysisContext context);

	Set<PredicateAssignment> getCounterExample();

	Collection<String> asSet();

	Collection<CONFLICT_TYPE> getConflicts();

}
