package indigo.generic;

import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Value;

import java.util.Map;

import com.google.common.collect.Maps;

public class GenericConflictResolutionPolicy implements ConflictResolutionPolicy {

	private final Map<String, Value> conflictResolution;
	private final static Value defaultBooleanValue = GenericPredicateAssignment.newBoolean(true);

	/**
	 * Solves all opposing conflicts with default value True.
	 */
	public GenericConflictResolutionPolicy() {
		this.conflictResolution = Maps.newHashMap();
	}

	public GenericConflictResolutionPolicy(Map<String, Value> conflictResolution) {
		this.conflictResolution = conflictResolution;
	}

	@Override
	public Value resolutionFor(String predicateName) {
		Value res = conflictResolution.get(predicateName);
		if (res != null)
			return res;
		return null;

	}

	@Override
	public Value getResolutionFor(String opName, Value defaultRes) {
		return conflictResolution.getOrDefault(opName, defaultRes);
	}

	@Override
	public Value defaultBooleanValue() {
		return defaultBooleanValue;
	}

	@Override
	public boolean hasResolutionFor(String operationName) {
		return conflictResolution.containsKey(operationName);
	}
}
