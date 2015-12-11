package indigo.impl.json;

import indigo.AbstractSpecification;
import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Invariant;
import indigo.interfaces.Operation;
import indigo.interfaces.PREDICATE_TYPE;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableSet;

//TODO: Make abstract class for Java and JSON impls.
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

		// JSONObject invariantNode = (JSONObject) spec.get("INV");
		// return ImmutableSet.of(new JSONInvariantClause(invariantNode));

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
				Operation operation = new JSONOperation(obj);
				operations.add(operation);
			}
		});
		return ImmutableSet.copyOf(operations);
	}

	// @Override
	// protected Map<PredicateAssignment, Set<Invariant>>
	// computeInvariantsForPredicate() {
	// Collection<PredicateAssignment> flattenEffects = Lists.newLinkedList();
	// getAllOperationEffects().values().forEach(flattenEffects::addAll);
	//
	// Map<PredicateAssignment, Set<Invariant>> affectedInvariantPerClauses =
	// new HashMap<>();
	// analysisLog.fine("Invariants affected by operations in the workload:");
	// flattenEffects.forEach(pa -> {
	// Set<Invariant> s = Sets.newHashSet();
	// for (Invariant i : invariants) {
	// if (pa.affects(i)) {
	// s.add(i.copyOf());
	// analysisLog.fine("Predicate " + pa + " present in invariant clauses " + s
	// + " for operation "
	// + pa.getOperationName());
	// }
	// }
	// ImmutableSet<Invariant> immutable = ImmutableSet.copyOf(s);
	// affectedInvariantPerClauses.put(pa, immutable);
	// });
	//
	// if (affectedInvariantPerClauses.isEmpty()) {
	// analysisLog.warning("No invariants are affected by operations in the workload.");
	// }
	// return ImmutableMap.copyOf(affectedInvariantPerClauses);
	// }

	@Override
	public Invariant newEmptyInv() {
		return new JSONInvariantClause(new JSONConstant(PREDICATE_TYPE.bool, "true"));
	}

	@Override
	public ConflictResolutionPolicy getDefaultConflictResolutionPolicy() {
		return new JSONConflictResolutionPolicy();
	}
}
