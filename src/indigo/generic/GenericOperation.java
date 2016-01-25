package indigo.generic;

import java.util.ArrayList;
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

import indigo.impl.javaclass.JavaPredicateAssignment;
import indigo.impl.javaclass.effects.JavaEffect;
import indigo.impl.json.JSONClause;
import indigo.impl.json.JSONClauseContext;
import indigo.impl.json.JSONPredicateAssignment;
import indigo.interfaces.Operation;
import indigo.interfaces.Parameter;
import indigo.interfaces.PredicateAssignment;

public class GenericOperation implements Operation {

	private final String opName;
	private final Collection<PredicateAssignment> opEffects;
	private final List<Parameter> params;

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
	}

	public GenericOperation(String opName, ArrayList<JavaEffect> effectList, List<Parameter> params) {
		this.opName = opName;
		this.opEffects = effectList.stream().map(e -> new JavaPredicateAssignment(e)).collect(Collectors.toList());
		this.params = params;
	}

	public GenericOperation(String opName, Collection<PredicateAssignment> predicates, List<Parameter> params) {
		this.opName = opName;
		// this.opEffects = predicates;
		// TODO: This 1 is an hack... but not being used right now.
		this.opEffects = predicates.stream()
				.map(effect -> newEffectFromParamContext(effect, params/* , 1 */)).collect(Collectors.toList());
		this.params = params;
	}

	// TODO: Does not support multiple arguments with same type.
	private PredicateAssignment newEffectFromParamContext(PredicateAssignment effect,
			List<Parameter> params/*
									 * , int iteration
									 */) {
		LinkedList<Parameter> paramsCopy = new LinkedList<>(params);

		List<Parameter> newParams = effect.getParams().stream().map(p -> {
			String predicateValue = "_" + p.getType().toLowerCase();
			String predicateType = p.getType();
			int idx = 0;
			while (idx < paramsCopy.size()) {
				Parameter existingP = paramsCopy.get(idx);
				if (existingP.getType().equals(p.getType())) {
					predicateValue = existingP.getName() /* + iteration */;
					paramsCopy.remove(idx);
					break;
				}
				idx++;
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
		return opEffects;
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

	protected static boolean strictContains(Set<PredicateAssignment> predicateSet,
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
}