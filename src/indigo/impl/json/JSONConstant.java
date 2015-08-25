package indigo.impl.json;

import org.json.simple.JSONObject;

public class JSONConstant extends JSONClause {

	private final String type;
	private final String value;

	public JSONConstant(JSONObject obj) {
		JSONObject value = (JSONObject) obj.get("value");
		this.type = (String) value.get("type");
		this.value = "" + value.get("value");
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
	public JSONConstant copyOf() {
		return new JSONConstant(type, value);
	}

	@Override
	public String toString() {
		// return value + " : " + type;
		return value;
	}

	@Override
	public void instantiateVariables(int i) {
	}

	public String getValueAsString() {
		return value;
	}
}
