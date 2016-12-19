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

import indigo.conflitcs.enums.CONFLICT_TYPE;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.runtime.AnalysisContext;

public interface OperationTest extends Comparable<OperationTest> {

	boolean isConflicting();

	boolean isSelfConflicting();

	boolean isNonIdempotent();

	boolean isOpposing();

	boolean isConflictSolved();

	boolean isOK();

	boolean isValidWPC();

	boolean isModified();

	void setInvalidWPC();

	void setConflicting();

	void setConflictSolved();

	void setIgnored();

	void addCounterExample(Collection<PredicateAssignment> model, AnalysisContext context);

	Set<PredicateAssignment> getCounterExample();

	Set<String> asSet();

	List<String> asList();

	Collection<CONFLICT_TYPE> getConflicts();

}
