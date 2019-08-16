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
package indigo.conflitcs;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import indigo.generic.GenericPredicateAssignment;
import indigo.impl.json.AbstractConflictResolutionPolicy;
import indigo.interfaces.interactive.ConflictResolutionPolicy;
import indigo.interfaces.logic.Value;
import indigo.runtime.Text;

public class AutomaticAddWinsConflictResolutionPolicy extends AbstractConflictResolutionPolicy
		implements ConflictResolutionPolicy {

	private final static boolean defaultValue = true;
	private final static Value defaultBooleanValue = GenericPredicateAssignment.newBoolean(defaultValue);
	private final Iterator<String> input;
	private final PrintStream out;
	private final static Set<String> trueSet = ImmutableSet.of("TRUE", "true", "T", "t");
	private final static Set<String> falseSet = ImmutableSet.of("FALSE", "false", "F", "f");
	private static final String PROVIDE_RESOLUTION_FOR_MSG = "PLEASE PROVIDE PREFERRED PREDICATE VALUE FOR "
			+ Text.opColor("%s") + ".";
	private static final String DEFAULT_RESOLUTION_FOR_MSG = "USING DEFAULT VALUE: " + Text.opColor("%s") + ".";

	enum BOOLEAN_RESOLUTION {
		TRUE, FALSE
	}

	/**
	 * Solves all opposing conflicts with default value True.
	 */
	public AutomaticAddWinsConflictResolutionPolicy(Iterator<String> input, PrintStream out) {
		super();
		this.input = input;
		this.out = out;
	}

	@Override
	public Value resolutionFor(String predicateName) {
		Value res = conflictResolution.get(predicateName);
		if (res != null) {
			return res;
		}
		else {
			return defaultBooleanValue;
		}
	}

}
