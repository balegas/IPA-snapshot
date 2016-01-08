package indigo.impl.json;

import indigo.interfaces.PREDICATE_TYPE;
import indigo.interfaces.Value;

import org.json.simple.JSONObject;

public class JSONConstant extends JSONClause implements Value {

	private final PREDICATE_TYPE type;
	private final String value;

	public JSONConstant(JSONObject obj) {
		JSONObject value = (JSONObject) obj.get("value");
		// TODO: must change spec reserved value "int"
		if (value.get("type").equals("int")) {
			this.type = PREDICATE_TYPE.valueOf("numeric");
		} else {
			this.type = PREDICATE_TYPE.valueOf((String) value.get("type"));
		}
		this.value = "" + value.get("value");
	}

	public JSONConstant(PREDICATE_TYPE type, String valueAsString) {
		this.type = type;
		this.value = valueAsString;
	}

	/*
	 * @Override public int hashCode() { return (type.name() +
	 * value).hashCode(); }
	 * 
	 * @Override public boolean equals(Object otherConstant) { JSONConstant
	 * other = (JSONConstant) otherConstant; return this.type.equals(other.type)
	 * && this.value.equals(other.value); }
	 */

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

	@Override
	public PREDICATE_TYPE getType() {
		return type;
	}

	@Override
	public Object getValue() {
		return value;
	}

	/*
	 * @Override public Value negatedValue() { if
	 * (type.equals(PREDICATE_TYPE.bool)) { if (value.equals("true")) { return
	 * new JSONConstant(type, "false"); } else { return new JSONConstant(type,
	 * "true"); } } System.out.println("NOT IMPLEMENTED - negated Value");
	 * System.exit(0); return null; }
	 */
}
