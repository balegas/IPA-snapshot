package indigo;

import indigo.interfaces.Invariant;
import indigo.interfaces.Operation;
import indigo.interfaces.PredicateAssignment;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class AnalysisContext {

	private final Map<String, Collection<PredicateAssignment>> opEffects;
	private final ConflictResolutionPolicy resolutionPolicy;
	private final static Logger log = Logger.getLogger(AnalysisContext.class.getName());

	public AnalysisContext(ConflictResolutionPolicy policy) {
		this.resolutionPolicy = policy;
		this.opEffects = new HashMap<>();
	}

	public void addOperationEffects(Operation op) {
		this.opEffects.put(op.opName(), op.getEffects());
	}

	public void addInvariant(Invariant inv) {

	}

	public Operation getOperation(String opName) {
		// TODO: return new operation.
		return null;
	}

	public void applyConflictResolutionPolicy() {
		Map<String, String> predicateValue = new HashMap<>();

		// Solve opposing post-conditions.
		opEffects.forEach((opName, predicates) -> {
			Set<PredicateAssignment> newPredicates = new HashSet<>();
			for (PredicateAssignment p : predicates) {
				log.warning("Ignoring numerical predicates during conflict resolution.");
				String value = predicateValue.putIfAbsent(p.getPredicateName(), p.getAssignedValueAsString());
				if (value != null) {
					String resolution = resolutionPolicy.getResolutionForPredicate(p.getPredicateName(), "true");
					log.warning("Applying conflict resolution " + resolution + " for " + p.getPredicateName());
					p = new ModifiedPredicateAssignment(p, resolution);
				}
				newPredicates.add(p);
			}
		});

	}
}
