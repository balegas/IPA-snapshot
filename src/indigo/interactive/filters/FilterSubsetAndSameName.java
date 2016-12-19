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
package indigo.interactive.filters;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import indigo.interfaces.interactive.TestPairFilter;
import indigo.interfaces.interactive.TestPairPruneFilter;
import indigo.interfaces.operations.Operation;

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
			return successfulOps.stream().anyMatch(otherOp -> {
				return !(operation.opName().equals(otherOp.opName()) && otherOp.isSubset(operation));
			});
		}
	}

}
