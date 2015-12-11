package indigo.impl.json;

import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Value;

import java.util.Map;

import com.google.common.collect.Maps;

public abstract class AbstractConflictResolutionPolicy implements ConflictResolutionPolicy {

	private final Map<String, Value> conflictResolution;
	private final Value defaultBooleanValue;

	/**
	 * Solves all opposing conflicts with default value True.
	 */
	public AbstractConflictResolutionPolicy(Value defaultValue) {
		this.conflictResolution = Maps.newHashMap();
		this.defaultBooleanValue = defaultValue;
	}

	AbstractConflictResolutionPolicy(Map<String, Value> conflictResolution, Value defaultValue) {
		this.defaultBooleanValue = defaultValue;
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
