package indigo;

import indigo.interfaces.PredicateAssignment;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface OperationTest extends Comparable<OperationTest> {

	boolean isConflicting();

	boolean isSelfConflicting();

	boolean isNonIdempotent();

	boolean isOpposing();

	Collection<String> asSet();

	boolean isOK();

	void addCounterExample(List<PredicateAssignment> model, AnalysisContext context);

	Set<PredicateAssignment> getCounterExample();

}