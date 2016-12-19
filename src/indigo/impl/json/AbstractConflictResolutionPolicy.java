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
package indigo.impl.json;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import indigo.generic.GenericPredicateAssignment;
import indigo.interfaces.interactive.ConflictResolutionPolicy;
import indigo.interfaces.logic.Value;

public abstract class AbstractConflictResolutionPolicy implements ConflictResolutionPolicy {

	protected final Map<String, Value> conflictResolution;
	protected Value defaultBooleanValue = GenericPredicateAssignment.newBoolean(true);

	public AbstractConflictResolutionPolicy() {
		this.conflictResolution = Maps.newHashMap();
	}

	public AbstractConflictResolutionPolicy(Map<String, Value> conflictResolution) {
		this.conflictResolution = conflictResolution;
	}

	/**
	 * Solves all opposing conflicts with default value True.
	 */
	public AbstractConflictResolutionPolicy(Value defaultValue) {
		this.conflictResolution = Maps.newHashMap();
		this.defaultBooleanValue = defaultValue;
	}

	AbstractConflictResolutionPolicy(Map<String, Value> conflictResolution, Value defaultValue) {
		this.defaultBooleanValue = defaultValue;
		this.conflictResolution = conflictResolution;
	}

	@Override
	public Value resolutionFor(String predicateName) {
		Value res = conflictResolution.get(predicateName);
		if (res != null)
			return res;
		return defaultBooleanValue.copyOf();

	}

	@Override
	public boolean hasResolutionFor(String operationName) {
		return conflictResolution.containsKey(operationName);
	}

	@Override
	public List<String> dumpResolutions() {
		List<String> output = Lists.newLinkedList();
		for (Entry<String, Value> entry : conflictResolution.entrySet()) {
			output.add(entry.getKey() + ": " + entry.getValue());
		}
		return output;
	}
}
