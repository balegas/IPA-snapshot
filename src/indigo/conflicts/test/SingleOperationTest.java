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

public class SingleOperationTest extends AbstractOperationTest {

	private final String opName;

	public SingleOperationTest(String opName) {
		this.opName = opName;
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
	public Set<String> asSet() {
		return ImmutableSet.of(opName);
	}

	@Override
	public List<String> asList() {
		return ImmutableList.of(opName);
	}

}
