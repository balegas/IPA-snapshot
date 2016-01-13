package indigo.generic;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class SingleOperationTest extends AbstractOperationTest {

	private final String opName;

	public SingleOperationTest(String opName) {
		this.opName = opName;
	}

	// @Override
	// public int hashCode() {
	// return opName.hashCode();
	// }
	//
	// @Override
	// public boolean equals(Object other) {
	// if (other instanceof SingleOperationTest) {
	// SingleOperationTest otherOC = (SingleOperationTest) other;
	// if (this.opName.equals(otherOC.opName)) {
	// return true;
	// }
	// }
	// return false;
	// }

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
	public Set<String> asSet() {
		return ImmutableSet.of(opName);
	}

}
