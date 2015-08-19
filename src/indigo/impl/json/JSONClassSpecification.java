package indigo.impl.json;

import indigo.interfaces.Clause;
import indigo.interfaces.Operation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableSet;

public class JSONClassSpecification {

	private final JSONObject spec;
	// private final Set<Operation> operations;
	// TODO: Should reduce to a single invariant?
	private final Set<Clause> invariants;
	private final Set<Operation> operations;

	// private final Map<PredicateAssignment, Set<Clause>>
	// affectedInvariantPerClauses;

	public JSONClassSpecification(JSONObject spec) {
		this.spec = spec;
		this.invariants = readInvariants();
		this.operations = readOperations();
		// this.affectedInvariantPerClauses = computeInvariantsForPredicate();
		System.out.println(this.invariants);
		System.out.println(this.operations);
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
}
