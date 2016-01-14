package indigo.generic;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class OperationPairTest extends AbstractOperationTest {

	private final String firstOperation;
	private final String secondOperation;

	public OperationPairTest(String firstOperation, String secondOperation) {
		this.firstOperation = firstOperation;
		this.secondOperation = secondOperation;
	}

	// @Override
	// public int hashCode() {
	// return (firstOperation + secondOperation).hashCode();
	// }
	//
	// @Override
	// public boolean equals(Object other) {
	// if (other instanceof OperationPairTest) {
	// OperationPairTest otherOC = (OperationPairTest) other;
	// if (this.firstOperation.equals(otherOC.firstOperation)) {
	// if (!this.secondOperation.equals(otherOC.secondOperation)) {
	// return true;
	// }
	// }
	// }
	// return false;
	// }

	@Override
	public Set<String> asSet() {
		return ImmutableSet.of(firstOperation, secondOperation);
	}

	private String getPairUID() {
		if (firstOperation.compareTo(secondOperation) <= 0) {
			return firstOperation + secondOperation;
		} else {
			return secondOperation + firstOperation;
		}

	}

	@Override
	public String toString() {
		return "[" + firstOperation + " , " + secondOperation + " ] : " + ((conflicts.size() == 0) ? "OK" : conflicts);
	}

	@Override
	public int compareTo(OperationTest o) {
		if (o instanceof OperationPairTest) {
			return this.getPairUID().compareTo(((OperationPairTest) o).getPairUID());
		}
		return 1;
	}

	@Override
	public boolean isSelfConflicting() {
		return false;
	}

	public static OperationPairTest of(String op1, String op2) {
		return new OperationPairTest(op1, op2);
	}

	public String getFirst() {
		return firstOperation;
	}

	public String getSecond() {
		return secondOperation;
	}

}
