package indigo.conflitcs;

import java.util.Map;

import indigo.impl.json.AbstractConflictResolutionPolicy;
import indigo.interfaces.interactive.ConflictResolutionPolicy;
import indigo.interfaces.logic.Value;

public class GenericConflictResolutionPolicy extends AbstractConflictResolutionPolicy
		implements ConflictResolutionPolicy {

	/**
	 * Solves all opposing conflicts with default value True.
	 */
	private GenericConflictResolutionPolicy() {
		super();
	}

	public GenericConflictResolutionPolicy(Map<String, Value> conflictResolution) {
		super(conflictResolution);
	}

	public static GenericConflictResolutionPolicy getDefault() {
		return new GenericConflictResolutionPolicy();
	}

}
