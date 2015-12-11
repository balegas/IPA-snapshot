package indigo;

import java.util.Collection;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSet;

public class SingleOperationTest implements OperationTest {

	// private final Operation firstOperation;
	private final String opName;

	Collection<CONFLICT_TYPE> conflicts;

	public SingleOperationTest(String opName) {
		conflicts = new TreeSet<>();
		// this.firstOperation = singleOperation;
		this.opName = opName;
	}

	@Override
	public int hashCode() {
		return opName.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SingleOperationTest) {
			SingleOperationTest otherOC = (SingleOperationTest) other;
			if (this.opName.equals(otherOC.opName)) {
				return true;
			}
		}
		return false;
	}

	public void setSelfConflicting() {
		conflicts.add(CONFLICT_TYPE.SELF_CONFLICT);
	}

	public void setNonIdempotent() {
		conflicts.add(CONFLICT_TYPE.NON_IDEMPOTENT);
	}

	@Override
	public boolean isSelfConflicting() {
		return conflicts.contains(CONFLICT_TYPE.SELF_CONFLICT);
	}

	@Override
	public boolean isNonIdempotent() {
		return conflicts.contains(CONFLICT_TYPE.NON_IDEMPOTENT);
	}

	@Override
	public String toString() {
		return "[" + this.opName + "] : " + ((conflicts.size() == 0) ? "OK" : conflicts);
	}

	public String getOpName() {
		return this.opName;
	}

	@Override
	public int compareTo(OperationTest o) {
		if (o instanceof SingleOperationTest) {
			return ((SingleOperationTest) o).opName.compareTo(opName);
		}
		return -1;
	}

	@Override
	public boolean isConflicting() {
		return false;
	}

	@Override
	public boolean isOpposing() {
		return false;
	}

	@Override
	public Collection<String> asSet() {
		return ImmutableSet.of(opName);
	}

}
