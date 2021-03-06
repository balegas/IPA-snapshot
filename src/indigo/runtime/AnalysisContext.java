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
package indigo.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import indigo.conflicts.test.OperationTest;
import indigo.generic.ConditionPredicateAssignment;
import indigo.generic.GenericOperation;
import indigo.generic.GenericPredicateFactory;
import indigo.interfaces.interactive.ConflictResolutionPolicy;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.logic.Value;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.Operation;
import indigo.interfaces.operations.Parameter;
import utils.Pair;

public class AnalysisContext {

	private final ConflictResolutionPolicy resolutionPolicy;
	private final Map<String, Collection<PredicateAssignment>> opEffects;
	private final Map<String, Collection<PredicateAssignment>> transformedOps;
	private final Map<String, Collection<PredicateAssignment>> transformedOpsPre;
	private final Map<String, Collection<String>> predicateToOpsIncludingPre;
	private final Map<String, Operation> operations;
	private final Map<String, ConditionPredicateAssignment> predicateSizeConstraints;
	private final static Logger log = Logger.getLogger(AnalysisContext.class.getName());

	private final AnalysisContext parentContext;
	private final GenericPredicateFactory factory;

	private AnalysisContext(Collection<Operation> newOperations, ConflictResolutionPolicy policy,
			AnalysisContext parentContext, boolean propagateTransformations, GenericPredicateFactory factory,
			Map<String, ConditionPredicateAssignment> predicateSizeConstraints) {

		this.resolutionPolicy = policy;
		this.parentContext = parentContext;
		this.transformedOps = Maps.newTreeMap();
		this.transformedOpsPre = Maps.newTreeMap();
		this.factory = factory;
		this.predicateSizeConstraints = predicateSizeConstraints;

		this.operations = parentContext.operations.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		this.opEffects = parentContext.opEffects.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		if (propagateTransformations) {
			for (Entry<String, Collection<PredicateAssignment>> parentTransforms : parentContext.transformedOps
					.entrySet()) {
				opEffects.put(parentTransforms.getKey(), parentTransforms.getValue());
			}
		}

		for (Operation op : newOperations) {
			opEffects.put(op.opName(), op.getEffects());
			operations.put(op.opName(), op);
		}

		this.predicateToOpsIncludingPre = computePredicateToOpsIndex();
	}

	private AnalysisContext(Collection<Operation> operations, ConflictResolutionPolicy policy,
			GenericPredicateFactory factory) {
		this.resolutionPolicy = policy;
		this.parentContext = null;
		this.predicateSizeConstraints = Maps.newHashMap();
		this.transformedOps = Maps.newTreeMap();
		this.transformedOpsPre = Maps.newTreeMap();
		this.factory = factory;

		this.operations = operations.stream().collect(Collectors.toMap(Operation::opName, Function.identity()));

		this.opEffects = operations.stream().collect(Collectors.toMap(Operation::opName, Operation::getEffects));

		this.predicateToOpsIncludingPre = computePredicateToOpsIndex();
	}

	private Map<String, Collection<String>> computePredicateToOpsIndex() {
		Map<String, Collection<String>> predicateToOps = Maps.newHashMap();
		Map<String, Collection<PredicateAssignment>> opE = new HashMap<>(opEffects);
		for (Entry<String, Collection<PredicateAssignment>> entry : opE.entrySet()) {
			Collection<PredicateAssignment> effectsAndPre = new HashSet<>();
			effectsAndPre.addAll(entry.getValue());
			effectsAndPre.addAll(getOperationPreConditions(entry.getKey(), false, false));
			entry.setValue(effectsAndPre);
		}

		for (Entry<String, Collection<PredicateAssignment>> op : opE.entrySet()) {
			for (PredicateAssignment predicate : op.getValue()) {
				Collection<String> opNames = predicateToOps.get(predicate.getPredicateName());
				if (opNames == null) {
					opNames = Sets.newHashSet();
				}
				opNames.add(op.getKey());
				predicateToOps.put(predicate.getPredicateName(), opNames);
			}
		}
		return predicateToOps;
	}

	public AnalysisContext childContext(boolean propagateTransformations) {
		return new AnalysisContext(ImmutableSet.of(), this.resolutionPolicy, this, propagateTransformations,
				this.factory, this.predicateSizeConstraints);
	}

	public AnalysisContext childContext(Collection<Operation> newOperations, boolean propagateTransformations) {
		return new AnalysisContext(newOperations, this.resolutionPolicy, this, propagateTransformations, this.factory,
				this.predicateSizeConstraints);
	}

	protected List<Operation> solveOpposing(OperationTest operations) {
		if (parentContext == null) {
			System.out.println("Cannot test operations in root context");
			System.exit(0);
		}
		// contextOps.addAll(operations.asSet());
		List<Operation> modifiedOps = fixOpposing(operations.asSet());
		return modifiedOps;
	}

	protected void solveOpposingByModifying(OperationTest operations) {
		if (parentContext == null) {
			System.out.println("Cannot test operations in root context");
			System.exit(0);
		}
		// contextOps.addAll(operations.asSet());
		fixOpposingByModifying(operations.asSet());
	}

	public Collection<PredicateAssignment> getOperationEffects(String opName, boolean onlyBoolean,
			boolean allowTransformed) {
		Collection<PredicateAssignment> effects;
		if (!allowTransformed) {
			effects = opEffects.get(opName);
		} else {
			effects = transformedOps.getOrDefault(opName, opEffects.get(opName));
		}
		return effects.stream().filter(e -> e.isType(PREDICATE_TYPE.bool) || !onlyBoolean).collect(Collectors.toSet());
	}

	public Collection<PredicateAssignment> getOperationEffectsAndPre(String opName, boolean onlyBoolean,
			boolean allowTransformed) {
		Collection<PredicateAssignment> effects = new HashSet<>();
		if (!allowTransformed) {
			effects.addAll(opEffects.get(opName));
		} else {
			effects.addAll(transformedOps.getOrDefault(opName, opEffects.get(opName)));
		}
		effects.addAll(operations.get(opName).getPreConditions());
		return effects.stream().filter(e -> e.isType(PREDICATE_TYPE.bool) || !onlyBoolean).collect(Collectors.toSet());
	}

	public Collection<PredicateAssignment> getOperationPreConditions(String opName, boolean onlyBoolean,
			boolean allowTransformed) {
		Collection<PredicateAssignment> effects;
		if (!allowTransformed) {
			effects = operations.get(opName).getPreConditions();
		} else {
			effects = transformedOpsPre.getOrDefault(opName, operations.get(opName).getPreConditions());
		}
		if (effects == null) {
			System.out.println("here");
		}
		return effects.stream().filter(e -> e.isType(PREDICATE_TYPE.bool) || !onlyBoolean).collect(Collectors.toSet());

	}

	public Map<String, Collection<PredicateAssignment>> getAllOperationEffectsAsMap(Collection<String> opNames,
			boolean onlyBoolean, boolean allowTransformed) {
		Map<String, Collection<PredicateAssignment>> output = Maps.newHashMap();
		for (String opName : opNames) {
			Collection<PredicateAssignment> op = getOperationEffects(opName, onlyBoolean, allowTransformed);
			if (op != null)
				output.put(opName, op);
		}
		return output;
	}

	private Map<String, Collection<PredicateAssignment>> getAllOperationEffectsAndPreAsMap(Collection<String> opNames,
			boolean onlyBoolean, boolean allowTransformed) {
		Map<String, Collection<PredicateAssignment>> output = Maps.newHashMap();
		for (String opName : opNames) {
			Collection<PredicateAssignment> op = getOperationEffects(opName, onlyBoolean, allowTransformed);
			op.addAll(getOperationPreConditions(opName, onlyBoolean, allowTransformed));
			if (op != null)
				output.put(opName, op);
		}
		return output;
	}

	public Set<Pair<String, Collection<PredicateAssignment>>> getAllOperationEffects(Collection<String> opNames,
			boolean onlyBoolean, boolean allowTransformed) {
		Set<Pair<String, Collection<PredicateAssignment>>> output = Sets.newHashSet();
		for (String opName : opNames) {
			Collection<PredicateAssignment> op = getOperationEffects(opName, onlyBoolean, allowTransformed);
			if (op != null)
				output.add(new Pair<String, Collection<PredicateAssignment>>(opName, op));
		}
		return output;
	}

	protected void fixOpposingByModifying(Collection<String> contextOps) {
		log.finest("Ignoring numerical predicates during conflict resolution.");

		Map<String, PredicateAssignment> singleAssignmentCheck = new HashMap<>();
		Map<String, Collection<String>> operationsToModify = new HashMap<>();

		Map<String, Collection<PredicateAssignment>> opToTestEffectsAndPre = getAllOperationEffectsAsMap(
				// Map<String, Collection<PredicateAssignment>>
				// opToTestEffectsAndPre = getAllOperationEffectsAndPreAsMap(
				contextOps, false, true);

		opToTestEffectsAndPre.forEach((name, predicates) -> {
			for (PredicateAssignment predicate : predicates) {
				if (predicate.isType(PREDICATE_TYPE.bool)) {
					// Check predicate value already assigned.
					PredicateAssignment current = singleAssignmentCheck.putIfAbsent(predicate.getPredicateName(),
							predicate);
					if (current != null && !current.getAssignedValue().equals(predicate.getAssignedValue())) {
						// Assigned value and different from the first.
						Value convergenceRule = resolutionPolicy.resolutionFor(predicate.getPredicateName());
						PredicateAssignment resolution = factory.newPredicateAssignmentFrom(predicate, convergenceRule);
						log.info("Applying conflict resolution: all predicates \"" + predicate.getPredicateName()
								+ "\" become \"" + resolution + "\"");
						// Set resolution rule for the predicate.
						singleAssignmentCheck.put(predicate.getPredicateName(), resolution);
						// Mark all operations that have a different value for
						// that predicate.
						Collection<String> opsWithDiffPredicateValue = BoolOpsWithPredicateAndDiffValue(resolution);
						opsWithDiffPredicateValue.forEach(op -> {
							Collection<String> setOfPred = operationsToModify.get(op);
							if (setOfPred == null) {
								setOfPred = Sets.newHashSet();
							}
							setOfPred.add(resolution.getPredicateName());
							operationsToModify.put(op, setOfPred);
						});
					}
				}
			}
		});

		Map<String, Set<PredicateAssignment>> opsToPreInEffects = new HashMap<>();
		// Add resolutions to the effect set, when they match a pre-condition.
		for (String op : opToTestEffectsAndPre.keySet()) {
			Collection<PredicateAssignment> preConditions = getOperationPreConditions(op, true, true);
			for (PredicateAssignment pre : preConditions) {
				if (singleAssignmentCheck.containsKey(pre.getPredicateName())) {
					Set<PredicateAssignment> transformedEffects = opsToPreInEffects.get(op);
					if (transformedEffects == null) {
						transformedEffects = Sets.newHashSet();
						opsToPreInEffects.put(op, transformedEffects);
					}
					// PAPOC 4 with true true and this line commented should be
					// INV_WPC.
					// The reason it doesn't its because it is solving conflicts
					// according to pre-conditions aswell during fix opposing
					transformedEffects.add(singleAssignmentCheck.get(pre.getPredicateName()));
				}

			}
		}

		// Modify all occurrences of that value.
		for (Entry<String, Collection<String>> opPreds : operationsToModify.entrySet()) {
			Collection<PredicateAssignment> effectsList = Sets.newHashSet();
			// Collection<PredicateAssignment> preconditionsSet =
			// Sets.newHashSet();
			for (PredicateAssignment effect : getOperationEffects(opPreds.getKey(), false, true)) {
				if (opPreds.getValue().contains(effect.getPredicateName())) {
					effectsList.add(singleAssignmentCheck.get(effect.getPredicateName()));
				} else {
					effectsList.add(effect);
				}
			}
			// for (PredicateAssignment effect :
			// getOperationPreConditions(opPreds.getKey(), false, true)) {
			// if (opPreds.getValue().contains(effect.getPredicateName())) {
			// preconditionsSet.add(singleAssignmentCheck.get(effect.getPredicateName()));
			// //
			// effectsList.add(singleAssignmentCheck.get(effect.getPredicateName()));
			// } else {
			// preconditionsSet.add(effect);
			// }
			// }
			Collection<PredicateAssignment> transformedEffectsForOp = transformedOps.get(opPreds.getKey());
			if (transformedEffectsForOp == null) {
				transformedEffectsForOp = Sets.newHashSet();
				transformedOps.put(opPreds.getKey(), transformedEffectsForOp);
			}
			transformedEffectsForOp.addAll(effectsList);

			for (Entry<String, Set<PredicateAssignment>> op : opsToPreInEffects.entrySet()) {
				Collection<PredicateAssignment> transformedEffects = transformedOps.get(opPreds.getKey());
				if (transformedEffects == null) {
					transformedEffects = Sets.newHashSet();
					transformedOps.put(opPreds.getKey(), transformedEffects);
				}
				transformedEffects.addAll(op.getValue());
			}
			// transformedOpsPre.put(opPreds.getKey(), preconditionsSet);
		}
		if (!operationsToModify.isEmpty())
			fixOpposingByModifying(contextOps);
	}

	// Alternatively we could just drop the bad assignments.
	private List<Operation> fixOpposing(Collection<String> contextOps) {
		log.finest("Ignoring numerical predicates during conflict resolution.");
		transformedOps.clear();

		Map<String, PredicateAssignment> singleAssignmentCheck = new HashMap<>();
		Map<String, Collection<String>> operationsToModify = new HashMap<>();
		HashMap<String, Set<String>> opSuffix = Maps.newHashMap();
		Map<String, Collection<PredicateAssignment>> opToTestEffects = getAllOperationEffectsAndPreAsMap(contextOps,
				false, false);

		opToTestEffects.forEach((name, predicates) -> {
			for (PredicateAssignment predicate : predicates) {
				if (predicate.isType(PREDICATE_TYPE.bool)) {
					PredicateAssignment current = singleAssignmentCheck.putIfAbsent(predicate.getPredicateName(),
							predicate);
					if (current != null && !current.getAssignedValue().equals(predicate.getAssignedValue())) {
						Value convergenceRule = resolutionPolicy.resolutionFor(predicate.getPredicateName());
						PredicateAssignment resolution = factory.newPredicateAssignmentFrom(predicate, convergenceRule);
						log.warning("Applying conflict resolution: all predicates \"" + predicate.getPredicateName()
								+ "\" become \"" + resolution + "\"");
						singleAssignmentCheck.put(predicate.getPredicateName(), resolution);
						Collection<String> opsWithDiffPredicateValue = BoolOpsWithPredicateAndDiffValue(resolution);
						opsWithDiffPredicateValue.forEach(op -> {
							Collection<String> setOfPred = operationsToModify.get(op);
							if (setOfPred == null) {
								setOfPred = Sets.newHashSet();
							}
							setOfPred.add(resolution.getPredicateName());
							operationsToModify.put(op, setOfPred);
						});
					}
				}
			}
		});

		for (Entry<String, Collection<String>> opPreds : operationsToModify.entrySet()) {
			Collection<PredicateAssignment> effectsList = Sets.newHashSet();
			TreeSet<String> orderedSuffix = Sets.newTreeSet();
			for (PredicateAssignment effect : opEffects.get(opPreds.getKey()))
				if (opPreds.getValue().contains(effect.getPredicateName())) {
					effectsList.add(singleAssignmentCheck.get(effect.getPredicateName()));
					orderedSuffix.add(effect.getPredicateName());
				} else {
					effectsList.add(effect);
				}
			transformedOps.put(opPreds.getKey(), effectsList);
			opSuffix.put(opPreds.getKey(), orderedSuffix);

		}

		if (transformedOps.size() != 0) {
			log.finest("Resolution result:");
		}
		for (Entry<String, Collection<PredicateAssignment>> op : transformedOps.entrySet()) {
			log.info("Operation " + op.getKey() + " ORIG: " + getOperationEffects(op.getKey(), false, false)
					+ " TRANSFORMED: " + op.getValue());
		}
		return makeGenericOps(transformedOps, opSuffix);

	}

	private List<Operation> makeGenericOps(Map<String, Collection<PredicateAssignment>> transformedOps,
			HashMap<String, Set<String>> opSuffix) {
		List<Operation> ops = Lists.newLinkedList();
		transformedOps.forEach((opName, predicates) -> {
			StringBuilder suffix = new StringBuilder();
			// Must identify predicate
			Set<String> suffixSet = opSuffix.get(opName);
			String[] prefix = opName.split("-");
			if (suffixSet != null) {
				for (int i = 1; i < prefix.length; i++) {
					suffixSet.add(prefix[i]);
				}
				for (String predName : suffixSet) {
					suffix.append("-" + predName);
				}
			}
			List<Parameter> opParams = operations.get(opName).getParameters();
			ops.add(new GenericOperation(prefix[0] + suffix.toString(), predicates, opParams,
					operations.get(opName).getPreConditions()));
		});
		return ops;
	}

	private Collection<String> BoolOpsWithPredicateAndDiffValue(PredicateAssignment p) {
		Collection<String> opsWithPredicateAndDiffValue = Sets.newHashSet();
		predicateToOpsIncludingPre.get(p.getPredicateName()).stream().forEach(op -> {
			getOperationEffects(op, true, true).forEach(predicate -> {
				// getOperationEffectsAndPre(op, true, true).forEach(predicate
				// -> {
				if (predicate.getType().equals(PREDICATE_TYPE.bool)
						&& predicate.getPredicateName().equals(p.getPredicateName())
						&& (!predicate.getAssignedValue().equals(p.getAssignedValue()))) {
					opsWithPredicateAndDiffValue.add(op);
				}
			});
		});
		return opsWithPredicateAndDiffValue;
	}

	public static AnalysisContext getNewContext(Collection<Operation> allOps, ConflictResolutionPolicy policy,
			GenericPredicateFactory factory) {
		return new AnalysisContext(allOps, policy, factory);
	}

	public boolean hasPredicateAssignment(String predicateName) {
		return predicateToOpsIncludingPre.containsKey(predicateName);
	}

	public List<String> getConflictResolutionPolicy() {
		return resolutionPolicy.dumpResolutions();
	}

	public Operation getOperation(String opName) {
		return operations.get(opName);
	}

	public void registerContraint(ConditionPredicateAssignment constraint) {
		this.predicateSizeConstraints.put(constraint.getPredicateName(), constraint);

	}

	public ConditionPredicateAssignment getConstraintFor(String predicateName) {
		return predicateSizeConstraints.get(predicateName);
	}

}
