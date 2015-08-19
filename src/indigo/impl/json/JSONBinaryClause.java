package indigo.impl.json;

import indigo.interfaces.Clause;

import org.json.simple.JSONObject;

public class JSONBinaryClause extends JSONClause {

	private final String operator;
	private final Clause left;
	private final Clause right;

	public JSONBinaryClause(String operator, JSONObject left, JSONObject right, JSONClauseContext context) {
		this.operator = operator;
		this.left = objectToClause(left, context);
		this.right = objectToClause(right, context);
	}

	protected JSONBinaryClause(String operator, Clause left, Clause right) {
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		return "(" + left.toString() + " " + operator + " " + right.toString() + ")";
	}

	@Override
	public Clause copyOf() {
		return new JSONBinaryClause(operator, left.copyOf(), right.copyOf());
	}

	@Override
	public boolean isNumeric() {
		return NUMERIC_OPERATORS_SET.contains(operator);
	}

}
