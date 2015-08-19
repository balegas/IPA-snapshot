package indigo.impl.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JSONClauseContext {

	private final Map<String, String> nameToType;

	public JSONClauseContext() {
		nameToType = new HashMap<>();
	}

	public JSONClauseContext(Collection<JSONVariable> vars) {
		nameToType = new HashMap<>();
		for (JSONVariable var : vars) {
			nameToType.put(var.getName(), var.getType());
		}
	}

	String getVarType(String varName) {
		return nameToType.getOrDefault(varName, "_");
	}

	public static JSONClauseContext emptyContext() {
		return new JSONClauseContext();
	}
}
