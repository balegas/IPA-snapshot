package indigo;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Sets;

import indigo.interfaces.PredicateAssignment;

public abstract class AbstractOperationTest implements OperationTest {

	protected final Collection<CONFLICT_TYPE> conflicts;

	protected AnalysisContext context;
	protected Set<PredicateAssignment> counterExample;

	protected AbstractOperationTest() {
		conflicts = new TreeSet<>();
	}

	@Override
	public int hashCode() {
		return this.asSet().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AbstractOperationTest) {
			return this.asSet().equals(((AbstractOperationTest) other).asSet());
		} else {
			return false;
		}
	}

	public void setOpposing() {
		conflicts.add(CONFLICT_TYPE.OPPOSING_POST);
	}

	@Override
	public void setConflicting() {
		conflicts.add(CONFLICT_TYPE.CONFLICT);
	}

	public void setModified() {
		conflicts.add(CONFLICT_TYPE.MODIFIED);
	}

	public void setSelfConflicting() {
		conflicts.add(CONFLICT_TYPE.SELF_CONFLICT);
	}

	public void setNonIdempotent() {
		conflicts.add(CONFLICT_TYPE.NON_IDEMPOTENT);
	}

	@Override
	public void setInvalidWPC() {
		conflicts.add(CONFLICT_TYPE.INVALID_WPC);
	}

	@Override
	public boolean isOpposing() {
		return conflicts.contains(CONFLICT_TYPE.OPPOSING_POST);
	}

	@Override
	public boolean isConflicting() {
		return conflicts.contains(CONFLICT_TYPE.CONFLICT);
	}

	@Override
	public boolean isNonIdempotent() {
		return conflicts.contains(CONFLICT_TYPE.NON_IDEMPOTENT);
	}

	@Override
	public boolean isModified() {
		return conflicts.contains(CONFLICT_TYPE.MODIFIED);
	}

	@Override
	public boolean isOK() {
		return conflicts.isEmpty();
	}

	@Override
	public boolean isSelfConflicting() {
		return conflicts.contains(CONFLICT_TYPE.SELF_CONFLICT);
	}

	@Override
	public boolean isValid() {
		return !conflicts.contains(CONFLICT_TYPE.INVALID_WPC);
	}

	@Override
	public void addCounterExample(Collection<PredicateAssignment> model, AnalysisContext context) {
		this.context = context;
		this.counterExample = Sets.newHashSet();
		for (PredicateAssignment assertion : model) {
			if (context.hasPredicateAssignment(assertion.getPredicateName())) {
				counterExample.add(assertion);
			}
		}
		System.out.println("Counter model for conflict " + asSet() + " :" + counterExample);
	}

	@Override
	public Set<PredicateAssignment> getCounterExample() {
		return counterExample;
	}

	@Override
	public Collection<CONFLICT_TYPE> getConflicts() {
		return conflicts;
	}

	@Override
	public abstract Set<String> asSet();

}
