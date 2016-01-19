package indigo.generic;

import java.util.Collection;
import java.util.List;
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

	Set<String> asSet();

	List<String> asList();

	Collection<CONFLICT_TYPE> getConflicts();

}
