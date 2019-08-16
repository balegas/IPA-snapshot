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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableSet;

import indigo.impl.json.JSONClause;
import indigo.impl.json.JSONClauseContext;
import indigo.impl.json.JSONPredicateAssignment;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.Operation;
import indigo.interfaces.operations.Parameter;

public class GenericOperation implements Operation {

	private final String opName;
	private final Collection<PredicateAssignment> opEffects;
	private final List<Parameter> params;
	private Set<PredicateAssignment> opPreConditions;

	@SuppressWarnings("unchecked")
	public GenericOperation(JSONObject obj) {
		Set<PredicateAssignment> opEffects = new HashSet<>();
		this.opName = (String) obj.get("op_name");
		this.params = JSONClause.getArgs(obj);
		JSONClauseContext context = new JSONClauseContext(params);
		JSONArray effects = (JSONArray) obj.get("effects");

		effects.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				JSONPredicateAssignment pa = new JSONPredicateAssignment(opName, obj, context);
				opEffects.add(pa);
			}
		});
		this.opEffects = ImmutableSet.copyOf(opEffects);
		this.opPreConditions = ImmutableSet.copyOf(new HashSet<>());
	}

	// public GenericOperation(String opName, ArrayList<JavaEffect> effectList,
	// List<Parameter> params) {
	// this.opName = opName;
	// this.opEffects = effectList.stream().map(e -> new
	// JavaPredicateAssignment(e)).collect(Collectors.toSet());
	// this.params = params;
	// this.opPreConditions = ImmutableSet.copyOf(new HashSet<>());
	// }

	public GenericOperation(String opName, Set<PredicateAssignment> effectList, List<Parameter> params,
			Set<PredicateAssignment> preConditions) {
		this(opName, effectList, params);
		// if (opName.equals("beginTournament")) {
		// System.out.println(preConditions.stream().map(pre ->
		// newEffectFromParamContext(pre, params))
		// .collect(Collectors.toSet()));
		// System.out.println("here ");
		// }
		this.opPreConditions = preConditions.stream().map(pre -> newEffectFromParamContext(pre, params))
				.collect(Collectors.toSet());

	}

	private GenericOperation(String opName, Collection<PredicateAssignment> effects, List<Parameter> params) {
		this.opName = opName;
		this.opEffects = effects.stream().map(effect -> newEffectFromParamContext(effect, params))
				.collect(Collectors.toSet());
		this.params = params;
	}

	public GenericOperation(String opName, Collection<PredicateAssignment> predicates, List<Parameter> params,
			Set<PredicateAssignment> preConditions) {
		this.opName = opName;
		// this.opEffects = predicates;
		// TODO: This 1 is an hack... but not being used right now.
		this.opEffects = predicates.stream()
				.map(effect -> newEffectFromParamContext(effect, params/* , 1 */)).collect(Collectors.toSet());
		this.params = params;
		this.opPreConditions = preConditions.stream().map(pre -> newEffectFromParamContext(pre, params))
				.collect(Collectors.toSet());
	}

	// TODO: Does not support multiple arguments with same type.
	private PredicateAssignment newEffectFromParamContext(PredicateAssignment effect,
			List<Parameter> params/*
									 * , int iteration
									 */) {
		LinkedList<Parameter> paramsCopy = new LinkedList<>(params);

		List<Parameter> newParams = effect.getParams().stream().map(p -> {
			String predicateValue = "_" + p.getType().toString().toLowerCase();
			String predicateType = p.getType();
			int idx;
			if (p.getName().contains("\\$")) {
				idx = Integer.parseInt(p.getName().split("\\$")[1]);
				Parameter existingP = paramsCopy.get(idx);
				if (existingP.getType().equals(p.getType())) {
					predicateValue = existingP.getName();
					paramsCopy.remove(idx);
				}
			} else {
				idx = 0;
				while (idx < paramsCopy.size()) {
					Parameter existingP = paramsCopy.get(idx);
					if (existingP.getType().equals(p.getType())) {
						predicateValue = existingP.getName() /* + iteration */;
						paramsCopy.remove(idx);
						break;
					}
					idx++;
				}
			}

			return new GenericVariable(predicateValue, predicateType);
		}).collect(Collectors.toList());

		return new GenericPredicateAssignment(effect.getOperationName(), effect.getPredicateName(),
				effect.getAssignedValue().copyOf(), newParams);
	}

	@Override
	public String opName() {
		return opName;
	}

	@Override
	public Collection<PredicateAssignment> getEffects() {
		return ImmutableSet.copyOf(opEffects);
	}

	@Override
	public String toString() {
		return opName + GenericOperation.argsToString(params) + " : " + opEffects;
	}

	public static String argsToString(List<Parameter> params) {
		StringBuilder output;
		if (!params.isEmpty()) {
			output = new StringBuilder("(");
			Iterator<Parameter> it = params.iterator();
			do {
				Parameter elem = it.next();
				output.append(elem.getType() + " : " + elem.getName());
				if (it.hasNext())
					output.append(", ");
			} while (it.hasNext());
			output.append(")");
		} else {
			output = new StringBuilder("()");
		}
		return output.toString();
	}

	@Override
	public int hashCode() {
		return opName.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return opName.equals(((Operation) other).opName());
	}

	@Override
	public List<Parameter> getParameters() {
		return params;
	}

	public static boolean strictContains(Set<PredicateAssignment> predicateSet,
			Collection<Collection<PredicateAssignment>> predicateSetSet) {
		for (Collection<PredicateAssignment> existingOp : predicateSetSet) {
			boolean all = true;
			for (PredicateAssignment existingPred : existingOp) {
				all &= predicateSet.contains(existingPred);
			}
			if (all && predicateSet.size() == existingOp.size()) {
				if (allEqualValues(existingOp, predicateSet)) {
					return true;
				}
			}
		}
		return false;
	}

	protected static boolean allEqualValues(Collection<PredicateAssignment> op1, Collection<PredicateAssignment> op2) {
		boolean allEqual = true;
		for (PredicateAssignment op2pred : op2) {
			for (PredicateAssignment op1pred : op1) {
				if (op2pred.getPredicateName().equals(op1pred.getPredicateName())) {
					allEqual &= op2pred.getAssignedValue().equals(op1pred.getAssignedValue());
				}
			}
		}
		return allEqual;
	}

	@Override
	public boolean isSubset(Operation otherOp) {
		boolean isSubset = true;
		for (PredicateAssignment pred : opEffects) {
			boolean contains = false;
			for (PredicateAssignment otherPred : otherOp.getEffects()) {
				if (pred.equals(otherPred)) {
					if (pred.getAssignedValue().equals(otherPred.getAssignedValue())) {
						contains = true;
					} else {
						break;
					}
				}
			}
			if (!contains) {
				isSubset = false;
				break;
			}
		}
		return isSubset;
	}

	@Override
	public Set<PredicateAssignment> getPreConditions() {
		return opPreConditions;
	}

	@Override
	public boolean containsPredicate(String predicateName) {
		boolean result = false;
		for (PredicateAssignment effect : opEffects) {
			if (effect.isType(PREDICATE_TYPE.bool)) {
				result = effect.getPredicateName().equals(predicateName);
				if (result) {
					break;
				}
			}
		}
		return result;
	}
}