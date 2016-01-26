package indigo.interfaces.interactive;

import java.util.List;

import indigo.interfaces.logic.Value;

public interface ConflictResolutionPolicy {

	public Value resolutionFor(String opName);

	// public Value getResolutionFor(String opName, Value defaultRes);

	// public Value defaultBooleanValue();

	public boolean hasResolutionFor(String operationName);

	public List<String> dumpResolutions();

}
