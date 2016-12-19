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
package indigo.runtime;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import indigo.interfaces.interactive.ConflictResolutionPolicy;
import indigo.interfaces.logic.Invariant;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.operations.Operation;

public interface ProgramSpecification {

	public Set<Operation> getOperations();

	public Set<String> getOperationsNames();

	public Set<Invariant> getInvariantClauses();

	public String getAppName();

	public Invariant newEmptyInv();

	public ConflictResolutionPolicy getDefaultConflictResolutionPolicy();

	void updateOperations(Collection<Operation> newOperations);

	public Invariant invariantFor(Collection<String> asSet, AnalysisContext context);

	public Map<String, Set<PredicateAssignment>> getDependenciesForPredicate();

}
