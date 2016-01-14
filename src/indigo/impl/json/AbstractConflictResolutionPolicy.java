package indigo.impl.json;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import indigo.generic.GenericPredicateAssignment;
import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Value;

public abstract class AbstractConflictResolutionPolicy implements ConflictResolutionPolicy {

	protected final Map<String, Value> conflictResolution;
	protected Value defaultBooleanValue = GenericPredicateAssignment.newBoolean(true);

	public AbstractConflictResolutionPolicy() {
		this.conflictResolution = Maps.newHashMap();
	}

	public AbstractConflictResolutionPolicy(Map<String, Value> conflictResolution) {
		this.conflictResolution = conflictResolution;
	}

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
		return defaultBooleanValue.copyOf();

	}

	@Override
	public boolean hasResolutionFor(String operationName) {
		return conflictResolution.containsKey(operationName);
	}

	@Override
	public List<String> dumpResolutions() {
		List<String> output = Lists.newLinkedList();
		for (Entry<String, Value> entry : conflictResolution.entrySet()) {
			output.add(entry.getKey() + ": " + entry.getValue());
		}
		return output;
	}
}
