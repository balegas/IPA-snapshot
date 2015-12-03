package indigo;

import indigo.interfaces.Operation;

import java.util.Collection;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSet;

public class OperationConflicts implements Comparable<OperationConflicts> {

	Operation firstOperation;
	Operation secondOperation;
	ImmutableSet<Operation> set;

	Collection<CONFLICT_TYPE> conflicts;

	boolean self;

	public OperationConflicts(Operation singleOperation) {
		self = true;
		conflicts = new TreeSet<>();
		this.firstOperation = singleOperation;
		set = ImmutableSet.of(singleOperation);
	}

	public OperationConflicts(Operation firstOperation, Operation secondOperation) {
		conflicts = new TreeSet<>();
		this.firstOperation = firstOperation;
		if (firstOperation.equals(secondOperation)) {
			self = true;
			set = ImmutableSet.of(firstOperation);
		} else {
			this.secondOperation = secondOperation;
			set = ImmutableSet.of(firstOperation, secondOperation);
		}
	}

	@Override
	public int hashCode() {
		if (self) {
			return firstOperation.opName().hashCode();
		} else {
			return (firstOperation.opName() + secondOperation.opName()).hashCode();
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof OperationConflicts) {
			OperationConflicts otherOC = (OperationConflicts) other;
			if (this.self != otherOC.self) {
				return false;
			} else if (!this.firstOperation.equals(otherOC.firstOperation)) {
				return false;
			} else if (!this.self && !this.secondOperation.equals(otherOC.secondOperation)) {
				return false;
			}
			return true;
		}
		return false;
	}

	public void setSelfConflicting() {
		assert (secondOperation == null);
		conflicts.add(CONFLICT_TYPE.SELF_CONFLICT);
	}

	public void setOpposing() {
		assert (secondOperation != null);
		conflicts.add(CONFLICT_TYPE.OPPOSING_POST);
	}

	public void setConflicting() {
		assert (secondOperation != null);
		conflicts.add(CONFLICT_TYPE.CONFLICT);
	}

	public void setNonIdempotent() {
		assert (secondOperation != null);
		conflicts.add(CONFLICT_TYPE.NON_IDEMPOTENT);
	}

	public boolean isSelfConflicting() {
		assert (secondOperation == null);
		return conflicts.contains(CONFLICT_TYPE.SELF_CONFLICT);
	}

	public boolean isOpposing() {
		assert (secondOperation != null);
		return conflicts.contains(CONFLICT_TYPE.OPPOSING_POST);
	}

	public boolean isConflicting() {
		assert (secondOperation != null);
		return conflicts.contains(CONFLICT_TYPE.CONFLICT);
	}

	public boolean isNonIdempotent() {
		assert (secondOperation != null);
		return conflicts.contains(CONFLICT_TYPE.NON_IDEMPOTENT);
	}

	public boolean isSingleOp() {
		return self;
	}

	public Collection<Operation> asSet() {
		return set;
	}

	// Operations are ordered lexicographically.
	@Override
	public int compareTo(OperationConflicts o) {
		return this.getPairUID().compareTo(o.getPairUID());
	}

	private String getPairUID() {
		if (self) {
			return firstOperation.opName();
		} else {
			return firstOperation.opName() + secondOperation.opName();
		}
	}

	@Override
	public String toString() {
		return "[" + firstOperation + ((self) ? "" : (", " + secondOperation)) + "] : "
				+ ((conflicts.size() == 0) ? "OK" : conflicts);
	}
}
