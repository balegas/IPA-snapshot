package indigo.interfaces;

import java.util.List;

public interface ConflictResolutionPolicy {

	public Value resolutionFor(String opName);

	// public Value getResolutionFor(String opName, Value defaultRes);

	// public Value defaultBooleanValue();

	public boolean hasResolutionFor(String operationName);

	public List<String> dumpResolutions();

}
