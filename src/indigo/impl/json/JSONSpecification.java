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
import com.google.common.collect.Sets;

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
		return Sets.newHashSet(operations);
	}

	@Override
	public Invariant newEmptyInv() {
		return new JSONInvariantClause(new JSONConstant(PREDICATE_TYPE.bool, "true"));
	}

	@Override
	public ConflictResolutionPolicy getDefaultConflictResolutionPolicy() {
		return new JSONConflictResolutionPolicy();
	}
}
