/**
-------------------------------------------------------------------

Copyright (c) 2014 SyncFree Consortium.  All Rights Reserved.

This file is provided to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License.  You may obtain
a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

-------------------------------------------------------------------
**/
package indigo.conflicts.test;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Sets;

import indigo.conflitcs.enums.CONFLICT_TYPE;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.runtime.AnalysisContext;

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
	public void setConflictSolved() {
		conflicts.add(CONFLICT_TYPE.CONFLICT_SOLVED);
	}

	@Override
	public void setIgnored() {
		conflicts.add(CONFLICT_TYPE.IGNORED);
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
	public boolean isValidWPC() {
		return !conflicts.contains(CONFLICT_TYPE.INVALID_WPC);
	}

	@Override
	public boolean isConflictSolved() {
		return conflicts.contains(CONFLICT_TYPE.CONFLICT_SOLVED);
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

	@Override
	public abstract List<String> asList();

}
