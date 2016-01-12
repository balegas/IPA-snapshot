package indigo;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Invariant;
import indigo.interfaces.Operation;
import indigo.interfaces.PREDICATE_TYPE;
import indigo.interfaces.PredicateAssignment;
import indigo.interfaces.Value;

public class AnalysisContext {

	private final ConflictResolutionPolicy resolutionPolicy;
	private final Map<String, Collection<PredicateAssignment>> opEffects;
	private final Map<String, Collection<PredicateAssignment>> transformedOps;
	private final Map<String, Collection<String>> predicateToOps;
	private final Set<String> contextOps;
	private final static Logger log = Logger.getLogger(AnalysisContext.class.getName());

	private final AnalysisContext parentContext;
	private final PredicateFactory factory;

	private AnalysisContext(Collection<Operation> newOperations, ConflictResolutionPolicy policy,
			AnalysisContext parentContext, boolean propagateTransformations, PredicateFactory factory) {

		this.resolutionPolicy = policy;
		this.parentContext = parentContext;
		this.contextOps = Sets.newHashSet();
		this.transformedOps = Maps.newTreeMap();
		this.opEffects = Maps.newHashMap(parentContext.opEffects);
		this.factory = factory;

		for (Operation op : newOperations) {
			opEffects.put(op.opName(), op.getEffects());
		}

		if (propagateTransformations) {
			for (Entry<String, Collection<PredicateAssignment>> parentTransforms : parentContext.transformedOps
					.entrySet()) {
				opEffects.put(parentTransforms.getKey(), parentTransforms.getValue());
			}
		}

		this.predicateToOps = computePredicateToOpsIndex();
	}

	private AnalysisContext(Collection<Operation> operations, ConflictResolutionPolicy policy,
			PredicateFactory factory) {
		this.resolutionPolicy = policy;
		this.parentContext = null;
		this.contextOps = Sets.newHashSet();
		this.transformedOps = Maps.newTreeMap();
		this.factory = factory;

		Map<String, Collection<PredicateAssignment>> map = Maps.newHashMap();
		for (Operation op : operations) {
			map.put(op.opName(), op.getEffects());
		}

		this.opEffects = ImmutableMap.copyOf(map);
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
		contextOps.addAll(operations.asSet());
		List<Operation> modifiedOps = fixOpposing();
		return modifiedOps;
	}

	public Collection<PredicateAssignment> getOperationEffects(String opName, boolean allowTransformed) {
		/*
		 * if (!allowTransformed && parentContext != null) { return
		 * parentContext.getOperationEffects(opName, allowTransformed); } else
		 */if (!allowTransformed) {
			return opEffects.get(opName);
		} else {
			Collection<PredicateAssignment> effects = transformedOps.getOrDefault(opName, opEffects.get(opName));
			return effects;
		}
	}

	public void addInvariant(Invariant inv) {

	}

	public Map<String, Collection<PredicateAssignment>> getAllOperationEffectsAsMap(Collection<String> opNames,
			boolean allowTransformed) {
		Map<String, Collection<PredicateAssignment>> output = Maps.newHashMap();
		for (String opName : opNames) {
			Collection<PredicateAssignment> op = getOperationEffects(opName, allowTransformed);
			if (op != null)
				output.put(opName, op);
		}
		return output;
	}

	public Set<Pair<String, Collection<PredicateAssignment>>> getAllOperationEffects(Collection<String> opNames,
			boolean allowTransformed) {
		Set<Pair<String, Collection<PredicateAssignment>>> output = Sets.newHashSet();
		for (String opName : opNames) {
			Collection<PredicateAssignment> op = getOperationEffects(opName, allowTransformed);
			if (op != null)
				output.add(new Pair<String, Collection<PredicateAssignment>>(opName, op));
		}
		return output;
	}

	// Alternatively we could just drop the bad assignments.
	private List<Operation> fixOpposing() {
		log.finest("Ignoring numerical predicates during conflict resolution.");
		transformedOps.clear();

		Map<String, PredicateAssignment> singleAssignmentCheck = new HashMap<>();
		Map<String, Collection<String>> operationsToModify = new HashMap<>();
		HashMap<String, Set<String>> opSuffix = Maps.newHashMap();
		Map<String, Collection<PredicateAssignment>> opToTestEffects = getAllOperationEffectsAsMap(contextOps, false);

		opToTestEffects.forEach((name, predicates) -> {
			for (PredicateAssignment predicate : predicates) {
				if (predicate.isType(PREDICATE_TYPE.bool)) {
					PredicateAssignment current = singleAssignmentCheck.putIfAbsent(predicate.getPredicateName(),
							predicate);
					if (current != null && !current.getAssignedValue().equals(predicate.getAssignedValue())) {
						Value convergenceRule = resolutionPolicy.getResolutionFor(predicate.getPredicateName(),
								resolutionPolicy.defaultBooleanValue());
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
			log.info("Operation " + op.getKey() + " ORIG: " + getOperationEffects(op.getKey(), false) + " TRANSFORMED: "
					+ op.getValue());
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
			ops.add(new GenericOperation(prefix[0] + suffix.toString(), predicates));
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

}

class GenericOperation implements Operation {

	private final String opName;
	private final Collection<PredicateAssignment> predicates;

	public GenericOperation(String opName, Collection<PredicateAssignment> predicates) {
		this.opName = opName;
		this.predicates = predicates;
	}

	@Override
	public String opName() {
		return opName;
	}

	@Override
	public Collection<PredicateAssignment> getEffects() {
		return predicates;
	}

	@Override
	public String toString() {
		return opName + " " + predicates;
	}

	@Override
	public int hashCode() {
		return opName.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return opName.equals(((Operation) other).opName());
	}
}
