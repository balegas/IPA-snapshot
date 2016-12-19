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
package indigo.generic;

import java.util.List;

import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.logic.Value;
import indigo.interfaces.operations.Parameter;

public class GenericPredicateFactory {

	// private final ProgramSpecification spec;
	private static GenericPredicateFactory instance;

	private GenericPredicateFactory(/* ProgramSpecification spec */) {
		// this.spec = spec;
	}

	public PredicateAssignment newPredicateAssignmentFrom(PredicateAssignment effect, Value newValue) {
		String operationName = effect.getOperationName();
		String predicateName = effect.getPredicateName();
		Value value = newValue.copyOf();
		List<Parameter> arguments = GenericPredicateAssignment
				.parseParametersFromExpressionString(effect.expression().toString());
		return new GenericPredicateAssignment(operationName, predicateName, value, arguments);

	}

	public static GenericPredicateFactory getFactory(/*
												 * ProgramSpecification
												 * programSpec
												 */) {
		if (instance == null) {
			instance = new GenericPredicateFactory(/* programSpec */);
		}
		return instance;
	}

}