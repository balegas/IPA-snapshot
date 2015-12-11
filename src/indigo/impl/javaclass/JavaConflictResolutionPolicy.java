package indigo.impl.javaclass;

import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Value;

import java.util.Map;

import com.google.common.collect.Maps;

public class JavaConflictResolutionPolicy implements ConflictResolutionPolicy {

	private final Map<String, Value> conflictResolution;
	private final static BooleanValue defaultBooleanValue = BooleanValue.TrueValue();

	/**
	 * Solves all opposing conflicts with default value True.
	 */
	public JavaConflictResolutionPolicy() {
		this.conflictResolution = Maps.newHashMap();
	}

	public JavaConflictResolutionPolicy(Map<String, Value> conflictResolution) {
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
