package indigo;

import java.util.List;

import indigo.interfaces.Operation;

public interface TestPairPruneFilter {

	public List<List<Operation>> prunePending(Operation operation, List<List<Operation>> allTestPairs);

}
