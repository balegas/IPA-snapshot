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

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class OperationPairTest extends AbstractOperationTest {

	private final String firstOperation;
	private final String secondOperation;

	public OperationPairTest(String firstOperation, String secondOperation) {
		this.firstOperation = firstOperation;
		this.secondOperation = secondOperation;
	}

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

	@Override
	public List<String> asList() {
		return ImmutableList.of(firstOperation, secondOperation);
	}

}
