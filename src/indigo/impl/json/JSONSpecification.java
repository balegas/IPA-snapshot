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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import indigo.generic.GenericConstant;
import indigo.generic.GenericOperation;
import indigo.interfaces.interactive.ConflictResolutionPolicy;
import indigo.interfaces.logic.Invariant;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.Operation;
import indigo.specification.AbstractSpecification;

public class JSONSpecification extends AbstractSpecification {

	private final JSONObject spec;

	public JSONSpecification(JSONObject spec) {
		super("MUST ADD APP NAME TO SPEC");
		this.spec = spec;
		init();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Set<Invariant> readInvariants() {

		Set<Invariant> invariants = new HashSet<>();
		JSONArray invariantNode = (JSONArray) spec.get("INV");
		invariantNode.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				Invariant clause = new JSONInvariantClause(obj);
				invariants.add(clause);
			}
		});
		return ImmutableSet.copyOf(invariants);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Set<Operation> readOperations() {
		Set<Operation> operations = new HashSet<>();
		JSONArray operationsNode = (JSONArray) spec.get("OPS");
		operationsNode.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				Operation operation = new GenericOperation(obj);
				operations.add(operation);
			}
		});
		return Sets.newHashSet(operations);
	}

	@Override
	public Invariant newEmptyInv() {
		return new JSONInvariantClause(new GenericConstant(PREDICATE_TYPE.bool, "true"));
	}

	@Override
	public ConflictResolutionPolicy getDefaultConflictResolutionPolicy() {
		return new JSONConflictResolutionPolicy();
	}

	@Override
	public Map<String, Set<PredicateAssignment>> getDependenciesForPredicate() {
		// TODO Auto-generated method stub
		System.out.println("not implemented");
		System.exit(0);
		return null;
	}

}
