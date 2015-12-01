package indigo.impl.json;

import org.json.simple.JSONObject;

public class JSONBinaryClause extends JSONClause {

	private final String operator;
	private final JSONClause left;
	private final JSONClause right;

	public JSONBinaryClause(String operator, JSONObject left, JSONObject right, JSONClauseContext context) {
		this.operator = operator;
		this.left = objectToClause(left, context);
		this.right = objectToClause(right, context);
	}

	protected JSONBinaryClause(String operator, JSONClause left, JSONClause right) {
		this.operator = operator;
		this.left = left.copyOf();
		this.right = right.copyOf();
	}

	@Override
	public String toString() {
		return "(" + left.toString() + " " + operator + " " + right.toString() + ")";
	}

	@Override
	public JSONClause copyOf() {
		return new JSONBinaryClause(operator, left, right);
	}

	@Override
	public void instantiateVariables(int i) {
		left.instantiateVariables(i);
		right.instantiateVariables(i);
	}

	public JSONClause getLeftClause() {
		return left.copyOf();
	}
}
