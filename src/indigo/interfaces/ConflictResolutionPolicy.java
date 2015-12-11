package indigo.interfaces;

public interface ConflictResolutionPolicy {

	public Value resolutionFor(String opName);

	public Value getResolutionFor(String opName, Value defaultRes);

	public Value defaultBooleanValue();

	public boolean hasResolutionFor(String operationName);

}
