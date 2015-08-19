package indigo.impl.json;

import indigo.interfaces.Clause;

import org.json.simple.JSONObject;

public class JSONConstant extends JSONClause {

	private final String type;
	private final String value;

	public JSONConstant(JSONObject obj) {
		JSONObject value = (JSONObject) obj.get("value");
		this.type = (String) value.get("type");
		this.value = (String) value.get("value");
	}

	public JSONConstant(String type, String value) {
		this.type = type;
		this.value = value;
	}

	@Override
	// TODO: Check the type string for numeric values;
	public boolean isNumeric() {
		return type.equals("int");
	}

	@Override
	public Clause copyOf() {
		return new JSONConstant(type, value);
	}

	@Override
	public String toString() {
		// return value + " : " + type;
		return value;
	}
}
