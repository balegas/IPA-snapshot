package indigo.generic;

import org.json.simple.JSONObject;

import indigo.interfaces.logic.Value;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.Parameter;

public class GenericVariable implements Parameter, Value {

	private final PREDICATE_TYPE type;
	private final String name;

	public GenericVariable(JSONObject obj) {
		this.name = (String) obj.get("var_name");
		this.type = PREDICATE_TYPE.valueOf((String) obj.get("type"));
	}

	public GenericVariable(String varName, PREDICATE_TYPE varType) {
		this.name = varName;
		this.type = varType;
	}

	@Override
	public String toString() {
		return type + " : " + name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return (name + type).hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof GenericVariable) {
			return this.name.equals(((GenericVariable) other).name) && this.type.equals(((GenericVariable) other).type);
		} else {
			return false;
		}
	}

	@Override
	public GenericVariable copyOf() {
		return new GenericVariable(name, type);
	}

	@Override
	public String getValue() {
		return getName();
	}

	@Override
	public PREDICATE_TYPE getType() {
		return type;
	}

}
