package indigo.impl.json;

import indigo.interfaces.PREDICATE_TYPE;
import indigo.interfaces.Value;

import java.util.Map;

public class JSONConflictResolutionPolicy extends AbstractConflictResolutionPolicy {

	private final static JSONConstant defaultJsonValue = new JSONConstant(PREDICATE_TYPE.bool, "true");

	/**
	 * Solves all opposing conflicts with default value True.
	 */
	public JSONConflictResolutionPolicy() {
		super(defaultJsonValue);
	}

	JSONConflictResolutionPolicy(Map<String, Value> conflictResolution) {
		super(conflictResolution, defaultJsonValue);
	}

}
