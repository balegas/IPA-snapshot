package indigo.impl.json;

import org.json.simple.JSONObject;

public class JSONVariable {

	private final String type;
	private final String name;

	// private JSONObject obj;

	public JSONVariable(JSONObject obj) {
		// this.obj = obj;
		this.name = (String) obj.get("var_name");
		this.type = (String) obj.get("type");
	}

	public JSONVariable(String varName, String varType) {
		this.name = varName;
		this.type = varType;
	}

	@Override
	public String toString() {
		return type + " : " + name;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return (name + type).hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof JSONVariable) {
			return this.name.equals(((JSONVariable) other).name) && this.type.equals(((JSONVariable) other).type);
		} else {
			return false;
		}
	}

	public JSONVariable copyOf() {
		return new JSONVariable(name, type);
	}

}
