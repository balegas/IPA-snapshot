package indigo;

import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Invariant;
import indigo.interfaces.PREDICATE_TYPE;
import indigo.interfaces.PredicateAssignment;
import indigo.interfaces.Value;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class AnalysisContext {

	private final ConflictResolutionPolicy resolutionPolicy;
	private final Map<String, Collection<PredicateAssignment>> opEffects;
	private final Map<String, Collection<PredicateAssignment>> transformedOps;
	private final static Logger log = Logger.getLogger(AnalysisContext.class.getName());

	private final AnalysisContext parentContext;

	private AnalysisContext(ConflictResolutionPolicy policy, AnalysisContext parentContext) {
		this.resolutionPolicy = policy;
		this.opEffects = Maps.newHashMap();
		this.transformedOps = Maps.newTreeMap();
		this.parentContext = parentContext;
	}

	private AnalysisContext(Map<String, Collection<PredicateAssignment>> allOps, ConflictResolutionPolicy policy) {
		this.resolutionPolicy = policy;
		this.opEffects = ImmutableMap.copyOf(allOps);
		this.transformedOps = Maps.newTreeMap();
		this.parentContext = null;
	}

	public AnalysisContext newContextFrom() {
		return new AnalysisContext(this.resolutionPolicy, this);
	}

	// public AnalysisContext pop() {
	// return parentContext;
	// }

	public Collection<PredicateAssignment> getOperationEffects(String opName) {
		Collection<PredicateAssignment> effects = transformedOps.getOrDefault(opName, opEffects.get(opName));
		if (effects == null && parentContext != null) {
			return parentContext.getOperationEffects(opName);
		}
		// might be null;
		return effects;
	}

	public void addInvariant(Invariant inv) {

	}

	public Set<Pair<String, Collection<PredicateAssignment>>> getAllOperationEffects(Collection<String> opNames) {
		Set<Pair<String, Collection<PredicateAssignment>>> output = Sets.newHashSet();
		for (String opName : opNames) {
			Collection<PredicateAssignment> op = getOperationEffects(opName);
			if (op != null)
				output.add(new Pair<String, Collection<PredicateAssignment>>(opName, op));
		}
		return output;
	}

	// Alternatively we could just drop the bad assignments.
	public void fixOpposing() {
		log.fine("Ignoring numerical predicates during conflict resolution.");
		transformedOps.clear();

		Map<String, PredicateAssignment> predicateValue = new HashMap<>();
		// Solve opposing post-conditions.
		opEffects.forEach((opName, predicates) -> {
			for (PredicateAssignment p : predicates) {
				if (p.isType(PREDICATE_TYPE.bool)) {
					PredicateAssignment value = predicateValue.putIfAbsent(p.getPredicateName(), p);
					if (value != null) {
						Value resolution = resolutionPolicy.getResolutionFor(p.getPredicateName(),
								resolutionPolicy.defaultBooleanValue());
						log.warning("Applying conflict resolution " + resolution + " for " + p.getPredicateName()
								+ " using defualt: " + resolutionPolicy.hasResolutionFor(p.getOperationName()));
						PredicateAssignment newValue = p.copyWithNewValue(resolution);
						predicateValue.put(p.getPredicateName(), newValue);
						log.fine("New predicate assignment " + p + " for operation " + opName);
					}
				}
			}
		});
		for (Entry<String, Collection<PredicateAssignment>> op : opEffects.entrySet()) {
			for (PredicateAssignment predicate : op.getValue()) {
				Collection<PredicateAssignment> effectsList = transformedOps.get(op.getKey());
				if (effectsList == null) {
					effectsList = Sets.newHashSet();
				}
				if (predicate.isType(PREDICATE_TYPE.bool)) {
					effectsList.add(predicateValue.get(predicate.getPredicateName()));
					transformedOps.put(op.getKey(), effectsList);
				} else {
					effectsList.add(predicate);
				}

			}
		}
		log.finest("Resolution result:");
		for (Entry<String, Collection<PredicateAssignment>> op : transformedOps.entrySet()) {
			log.info("Operation " + op.getKey() + " ORIG: " + opEffects.get(op.getKey()) + " TRANSF: " + op.getValue());
		}

	}

	public static AnalysisContext getNewContext(Map<String, Collection<PredicateAssignment>> allOps,
			ConflictResolutionPolicy policy) {
		return new AnalysisContext(allOps, policy);
	}

}
