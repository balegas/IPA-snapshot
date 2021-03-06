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
package indigo.interactive.generator;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import indigo.generic.GenericConstant;
import indigo.generic.GenericPredicateFactory;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.logic.Value;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.OperationTransformer;

public class NegateEffects implements OperationTransformer {

	private static GenericPredicateFactory factory = GenericPredicateFactory.getFactory();

	private Set<PredicateAssignment> negatedEffects(Collection<PredicateAssignment> set) {
		Set<PredicateAssignment> negatedEffects = Sets.newHashSet();
		for (PredicateAssignment effect : set) {
			Value value = effect.getAssignedValue();
			if (value.toString().equals("true")) {
				value = new GenericConstant(PREDICATE_TYPE.bool, "false");
			} else if (value.toString().equals("false")) {
				value = new GenericConstant(PREDICATE_TYPE.bool, "true");
			} else {
				System.out.println("NOT EXPECTED TYPE");
				System.exit(0);
			}
			PredicateAssignment modEffect = factory.newPredicateAssignmentFrom(effect, value);
			negatedEffects.add(modEffect);
		}
		return negatedEffects;
	}

	@Override
	public Collection<PredicateAssignment> transformEffects(Collection<PredicateAssignment> set) {
		return negatedEffects(set);
	}

}
