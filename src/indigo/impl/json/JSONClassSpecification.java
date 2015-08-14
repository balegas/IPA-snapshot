package indigo.impl.json;

import indigo.abtract.Clause;

import java.util.Set;

import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableSet;

public class JSONClassSpecification {

	private final JSONObject spec;
	// private final Set<Operation> operations;
	// TODO: Should reduce to a single invariant?
	private final Set<Clause> invariants;

	// private final Map<PredicateAssignment, Set<Clause>>
	// affectedInvariantPerClauses;

	public JSONClassSpecification(JSONObject spec) {
		this.spec = spec;
		this.invariants = readInvariants();
		// this.operations = readOperations();
		// this.affectedInvariantPerClauses = computeInvariantsForPredicate();
		System.out.println(this.invariants);
	}

	@SuppressWarnings("unchecked")
	private Set<Clause> readInvariants() {
		JSONObject invariantNode = (JSONObject) spec.get("INV");
		return ImmutableSet.of(new JSONInvariantClause(invariantNode));
		// Set<Clause> invariants = new HashSet<>();
		// JSONArray invariantNode = (JSONArray) spec.get("INV");
		// invariantNode.forEach(new Consumer<JSONObject>() {
		//
		// @Override
		// public void accept(JSONObject obj) {
		// Clause clause = new JSONInvariantClause(obj);
		// invariants.add(clause);
		// }
		// });
		// return ImmutableSet.copyOf(invariants);
	}
}
