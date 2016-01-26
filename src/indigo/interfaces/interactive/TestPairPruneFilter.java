package indigo.interfaces.interactive;

import java.util.List;

import indigo.interfaces.operations.Operation;

public interface TestPairPruneFilter {

	public List<List<Operation>> prunePending(Operation operation, List<List<Operation>> allTestPairs);

}
