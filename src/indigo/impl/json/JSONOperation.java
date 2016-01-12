package indigo.impl.json;

import indigo.interfaces.Operation;
import indigo.interfaces.PredicateAssignment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableSet;

public class JSONOperation implements Operation {

	private final String opName;
	private final Set<PredicateAssignment> opEffects;

	@SuppressWarnings("unchecked")
	public JSONOperation(JSONObject obj) {
		Set<PredicateAssignment> opEffects = new HashSet<>();
		this.opName = (String) obj.get("op_name");
		JSONClauseContext context = new JSONClauseContext(JSONClause.getArgs(obj));
		JSONArray effects = (JSONArray) obj.get("effects");

		effects.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				JSONPredicateAssignment pa = new JSONPredicateAssignment(opName, obj, context);
				opEffects.add(pa);
			}
		});
		this.opEffects = ImmutableSet.copyOf(opEffects);
	}

	// TODO: Must distinguish operations with same name but different number of
	// operators.
	@Override
	public int hashCode() {
		return opName.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		// if (other instanceof JSONOperation) {
		// return this.opName.equals(((JSONOperation) other).opName)
		// /* && this.opEffects.equals(((JSONOperation) other).opEffects) */;
		// }
		if (other instanceof Operation) {
			return opName.equals(((Operation) other).opName());
		}
		return false;
	}

	@Override
	public String opName() {
		return opName;
	}

	@Override
	public Collection<PredicateAssignment> getEffects() {
		return opEffects;
	}

	@Override
	public String toString() {
		return opName /* + ": " + opEffects */;
	}

}
