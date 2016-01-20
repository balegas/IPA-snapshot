package indigo.impl.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import indigo.interfaces.Parameter;

public class JSONClauseContext {

	private final Map<String, String> nameToType;

	public JSONClauseContext() {
		nameToType = new HashMap<>();
	}

	public JSONClauseContext(Collection<Parameter> vars) {
		nameToType = new HashMap<>();
		for (Parameter var : vars) {
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
