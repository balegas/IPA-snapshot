package indigo.impl.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClauseContext {

	private final Map<String, String> nameToType;

	public ClauseContext() {
		nameToType = new HashMap<>();
	}

	public ClauseContext(Collection<JSONVariable> vars) {
		nameToType = new HashMap<>();
		for (JSONVariable var : vars) {
			nameToType.put(var.getName(), var.getType());
		}
	}

	String getVarType(String varName) {
		return nameToType.getOrDefault(varName, "_");
	}

	public static ClauseContext emptyContext() {
		return new ClauseContext();
	}
}
