package indigo.generic;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import indigo.TestPairFilter;
import indigo.TestPairPruneFilter;
import indigo.interfaces.Operation;

public class FilterSubsetAndSameName implements TestPairPruneFilter, TestPairFilter {

	@Override
	public List<List<Operation>> prunePending(Operation operation, List<List<Operation>> allTestPairs) {
		return allTestPairs.stream()
				.filter(pair -> !(operation.opName().equals(pair.get(0).opName()) && (operation.isSubset(pair.get(0)))))
				.collect(Collectors.toList());
	}

	@Override
	public boolean toTest(Operation operation, Collection<Operation> successfulOps) {
		if (successfulOps.isEmpty())
			return true;
		else {
			return successfulOps.stream()
					.anyMatch(otherOp -> operation.opName().equals(otherOp.opName()) && otherOp.isSubset(operation));
		}
	}

}
