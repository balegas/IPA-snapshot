package indigo.impl.json;

import indigo.abtract.Clause;

import org.json.simple.JSONObject;

public class BinaryClause extends JSONClause {

	private final String operator;
	private final Clause left;
	private final Clause right;

	public BinaryClause(String operator, JSONObject left, JSONObject right, ClauseContext context) {
		this.operator = operator;
		this.left = objectToClause(left, context);
		this.right = objectToClause(right, context);
	}

	protected BinaryClause(String operator, Clause left, Clause right) {
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		return left.toString() + " " + operator + " " + right.toString();
	}

	@Override
	public Clause copyOf() {
		return new BinaryClause(operator, left.copyOf(), right.copyOf());
	}

}
