package indigo.impl.json;

import indigo.AbstractSpecification;
import indigo.IndigoAnalyzer;
import indigo.interfaces.Clause;
import indigo.interfaces.Operation;
import indigo.interfaces.PredicateAssignment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class JSONSpecification implements AbstractSpecification {

	private final JSONObject spec;
	// private final Set<Operation> operations;
	// TODO: Should reduce to a single invariant?
	private final Set<Clause> invariants;
	private final Set<Operation> operations;
	private final Map<PredicateAssignment, Set<Clause>> affectedInvariantPerClauses;

	private final static Logger analysisLog = Logger.getLogger(IndigoAnalyzer.class.getName());

	public JSONSpecification(JSONObject spec) {
		this.spec = spec;
		this.invariants = readInvariants();
		this.operations = readOperations();
		this.affectedInvariantPerClauses = computeInvariantsForPredicate();
	}

	@SuppressWarnings("unchecked")
	private Set<Clause> readInvariants() {

		// JSONObject invariantNode = (JSONObject) spec.get("INV");
		// return ImmutableSet.of(new JSONInvariantClause(invariantNode));

		Set<Clause> invariants = new HashSet<>();
		JSONArray invariantNode = (JSONArray) spec.get("INV");
		invariantNode.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				Clause clause = new JSONInvariantClause(obj);
				invariants.add(clause);
			}
		});
		return ImmutableSet.copyOf(invariants);
	}

	private Set<Operation> readOperations() {
		Set<Operation> operations = new HashSet<>();
		JSONArray operationsNode = (JSONArray) spec.get("OPS");
		operationsNode.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				Operation operation = new JSONOperation(obj);
				operations.add(operation);
			}
		});
		return ImmutableSet.copyOf(operations);
	}

	private Map<PredicateAssignment, Set<Clause>> computeInvariantsForPredicate() {
		Collection<PredicateAssignment> predicateAssignment = getAllOperationEffects();
		Map<PredicateAssignment, Set<Clause>> affectedInvariantPerClauses = new HashMap<>();
		analysisLog.fine("Invariants affected by operations in the workload:");
		predicateAssignment.forEach(pa -> {
			Set<Clause> s = Sets.newHashSet();
			for (Clause i : invariants) {
				if (pa.hasEffectIn(i)) {
					s.add(i.copyOf());
					analysisLog.fine("Predicate " + pa + " present in invariant clauses " + s + " for operation "
							+ pa.opName());
				}
			}
			ImmutableSet<Clause> immutable = ImmutableSet.copyOf(s);
			affectedInvariantPerClauses.put(pa, immutable);
		});

		if (affectedInvariantPerClauses.isEmpty()) {
			analysisLog.warning("No invariants are affected by operations in the workload.");
		}
		return ImmutableMap.copyOf(affectedInvariantPerClauses);
	}

	@Override
	public Collection<PredicateAssignment> getAllOperationEffects() {
		List<PredicateAssignment> predicates = new ArrayList<>();
		for (Operation op : operations) {
			Collection<PredicateAssignment> pred = op.getEffects();
			predicates.addAll(pred);
		}
		return predicates;
	}

	@Override
	public Set<Operation> getOperations() {
		return ImmutableSet.copyOf(operations);
	}

	@Override
	public Set<Clause> getInvariantClauses() {
		return ImmutableSet.copyOf(invariants);
	}

	@Override
	public String getAppName() {
		return "MUST ADD APP NAME TO SPEC";
	}

	@Override
	public Map<PredicateAssignment, Set<Clause>> collectInvariantsForPredicate() {
		return ImmutableMap.copyOf(affectedInvariantPerClauses);
	}

	@Override
	public Clause newTrueClause() {
		return new JSONConstant("bool", "true");
	}

}
