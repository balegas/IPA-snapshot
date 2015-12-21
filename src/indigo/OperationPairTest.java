package indigo;

import java.util.Collection;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSet;

public class OperationPairTest implements OperationTest {

	private final String firstOperation;
	private final String secondOperation;

	Collection<CONFLICT_TYPE> conflicts;

	public OperationPairTest(String firstOperation, String secondOperation) {
		conflicts = new TreeSet<>();
		this.firstOperation = firstOperation;
		this.secondOperation = secondOperation;
	}

	@Override
	public int hashCode() {
		return (firstOperation + secondOperation).hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof OperationPairTest) {
			OperationPairTest otherOC = (OperationPairTest) other;
			if (this.firstOperation.equals(otherOC.firstOperation)) {
				if (!this.secondOperation.equals(otherOC.secondOperation)) {
					return true;
				}
			}
		}
		return false;
	}

	public void setOpposing() {
		assert (secondOperation != null);
		conflicts.add(CONFLICT_TYPE.OPPOSING_POST);
	}

	public void setConflicting() {
		assert (secondOperation != null);
		conflicts.add(CONFLICT_TYPE.CONFLICT);
	}

	public void setModified() {
		conflicts.add(CONFLICT_TYPE.MODIFIED);
	}

	@Override
	public boolean isOpposing() {
		assert (secondOperation != null);
		return conflicts.contains(CONFLICT_TYPE.OPPOSING_POST);
	}

	@Override
	public boolean isConflicting() {
		assert (secondOperation != null);
		return conflicts.contains(CONFLICT_TYPE.CONFLICT);
	}

	@Override
	public boolean isNonIdempotent() {
		assert (secondOperation != null);
		return conflicts.contains(CONFLICT_TYPE.NON_IDEMPOTENT);
	}

	public boolean isModified() {
		return conflicts.contains(CONFLICT_TYPE.MODIFIED);
	}

	@Override
	public Collection<String> asSet() {
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

}
