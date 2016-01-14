package indigo.generic;

import java.util.Map;

import indigo.impl.json.AbstractConflictResolutionPolicy;
import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Value;

public class GenericConflictResolutionPolicy extends AbstractConflictResolutionPolicy
		implements ConflictResolutionPolicy {

	/**
	 * Solves all opposing conflicts with default value True.
	 */
	public GenericConflictResolutionPolicy() {
		super();
	}

	public GenericConflictResolutionPolicy(Map<String, Value> conflictResolution) {
		super(conflictResolution);
	}

}
