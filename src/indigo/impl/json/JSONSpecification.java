package indigo.impl.json;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import indigo.generic.GenericConstant;
import indigo.generic.GenericOperation;
import indigo.interfaces.interactive.ConflictResolutionPolicy;
import indigo.interfaces.logic.Invariant;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.Operation;
import indigo.specification.AbstractSpecification;

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
				Operation operation = new GenericOperation(obj);
				operations.add(operation);
			}
		});
		return Sets.newHashSet(operations);
	}

	@Override
	public Invariant newEmptyInv() {
		return new JSONInvariantClause(new GenericConstant(PREDICATE_TYPE.bool, "true"));
	}

	@Override
	public ConflictResolutionPolicy getDefaultConflictResolutionPolicy() {
		return new JSONConflictResolutionPolicy();
	}

}
