package indigo.generic;

import org.json.simple.JSONObject;

import indigo.interfaces.operations.Parameter;

public class GenericVariable implements Parameter {

	private String type;
	private final String name;

	public GenericVariable(JSONObject obj) {
		this.name = (String) obj.get("var_name");
		this.type = (String) obj.get("type");
	}

	public GenericVariable(String varName, String varType) {
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
	public String getType() {
		return type;
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
	public void setType(String type) {
		this.type = type;

	}

}
