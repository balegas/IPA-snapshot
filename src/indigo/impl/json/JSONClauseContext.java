package indigo.impl.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.Parameter;

public class JSONClauseContext {

	private final Map<String, PREDICATE_TYPE> nameToType;

	public JSONClauseContext() {
		nameToType = new HashMap<>();
	}

	public JSONClauseContext(Collection<Parameter> vars) {
		nameToType = new HashMap<>();
		for (Parameter var : vars) {
			nameToType.put(var.getName(), var.getType());
		}
	}

	PREDICATE_TYPE getVarType(String varName) {
		return nameToType.getOrDefault(varName, PREDICATE_TYPE._);
	}

	public static JSONClauseContext emptyContext() {
		return new JSONClauseContext();
	}
}
