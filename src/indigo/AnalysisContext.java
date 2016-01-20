package indigo;

import java.util.Collection;
import java.util.HashMap;
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

import indigo.generic.GenericOperation;
import indigo.generic.OperationTest;
import indigo.generic.Pair;
import indigo.generic.PredicateFactory;
import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Operation;
import indigo.interfaces.PREDICATE_TYPE;
import indigo.interfaces.Parameter;
import indigo.interfaces.PredicateAssignment;
import indigo.interfaces.Value;

public class AnalysisContext {

	private final ConflictResolutionPolicy resolutionPolicy;
	private final Map<String, Collection<PredicateAssignment>> opEffects;
	private final Map<String, Collection<PredicateAssignment>> transformedOps;
	private final Map<String, Collection<String>> predicateToOps;
	private final Map<String, Operation> operations;
	// private final Set<String> contextOps;
	private final static Logger log = Logger.getLogger(AnalysisContext.class.getName());

	private final AnalysisContext parentContext;
	private final PredicateFactory factory;

	private AnalysisContext(Collection<Operation> newOperations, ConflictResolutionPolicy policy,
			AnalysisContext parentContext, boolean propagateTransformations, PredicateFactory factory) {

		this.resolutionPolicy = policy;
		this.parentContext = parentContext;
		// this.contextOps = Sets.newHashSet();
		this.transformedOps = Maps.newTreeMap();
		this.factory = factory;

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

		this.predicateToOps = computePredicateToOpsIndex();
	}

	private AnalysisContext(Collection<Operation> operations, ConflictResolutionPolicy policy,
			PredicateFactory factory) {
		this.resolutionPolicy = policy;
		this.parentContext = null;
		this.transformedOps = Maps.newTreeMap();
		this.factory = factory;

		this.operations = operations.stream().collect(Collectors.toMap(Operation::opName, Function.identity()));

		this.opEffects = operations.stream().collect(Collectors.toMap(Operation::opName, Operation::getEffects));

		this.predicateToOps = computePredicateToOpsIndex();
	}

	private Map<String, Collection<String>> computePredicateToOpsIndex() {
		Map<String, Collection<String>> predicateToOps = Maps.newHashMap();
		for (Entry<String, Collection<PredicateAssignment>> op : opEffects.entrySet()) {
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
				this.factory);
	}

	public AnalysisContext childContext(Collection<Operation> newOperations, boolean propagateTransformations) {
		return new AnalysisContext(newOperations, this.resolutionPolicy, this, propagateTransformations, this.factory);
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
		return effects.stream().filter(e -> e.isType(PREDICATE_TYPE.bool) || !onlyBoolean).collect(Collectors.toList());
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
		// HashMap<String, Set<String>> opSuffix = Maps.newHashMap();
		Map<String, Collection<PredicateAssignment>> opToTestEffects = getAllOperationEffectsAsMap(contextOps, false,
				true);

		opToTestEffects.forEach((name, predicates) -> {
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

		// Modify all occurrences of that value.
		for (Entry<String, Collection<String>> opPreds : operationsToModify.entrySet()) {
			Collection<PredicateAssignment> effectsList = Sets.newHashSet();
			for (PredicateAssignment effect : getOperationEffects(opPreds.getKey(), false, true))
				// for (PredicateAssignment effect :
				// opEffects.get(opPreds.getKey()))
				if (opPreds.getValue().contains(effect.getPredicateName())) {
					effectsList.add(singleAssignmentCheck.get(effect.getPredicateName()));
				} else {
					effectsList.add(effect);
				}
			transformedOps.put(opPreds.getKey(), effectsList);
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
		Map<String, Collection<PredicateAssignment>> opToTestEffects = getAllOperationEffectsAsMap(contextOps, false,
				false);

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
			ops.add(new GenericOperation(prefix[0] + suffix.toString(), predicates, opParams));
		});
		return ops;
	}

	private Collection<String> BoolOpsWithPredicateAndDiffValue(PredicateAssignment p) {
		Collection<String> opsWithPredicateAndDiffValue = Sets.newHashSet();
		predicateToOps.get(p.getPredicateName()).stream().forEach(op -> {
			opEffects.get(op).forEach(predicate -> {
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
			PredicateFactory factory) {
		return new AnalysisContext(allOps, policy, factory);
	}

	public boolean hasPredicateAssignment(String predicateName) {
		return predicateToOps.containsKey(predicateName);
	}

	public List<String> getConflictResolutionPolicy() {
		return resolutionPolicy.dumpResolutions();
	}

	public Operation getOperation(String opName) {
		return operations.get(opName);
	}

}
