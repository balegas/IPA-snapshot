package indigo.impl.json;

import org.json.simple.JSONObject;

public class JSONUnaryClause extends JSONClause {

	private final String operator;
	private final JSONClause unaryClause;

	public JSONUnaryClause(String operator, JSONObject jsonObject, JSONClauseContext context) {
		super();
		this.operator = operator;
		this.unaryClause = objectToClause(jsonObject, context);
	}

	private JSONUnaryClause(String operator, JSONClause clause) {
		super();
		this.operator = operator;
		this.unaryClause = clause.copyOf();
	}

	@Override
	public JSONClause copyOf() {
		return new JSONUnaryClause(operator, unaryClause);
	}

	@Override
	public String toString() {
		return operator + " ( " + unaryClause.toString() + " ) ";
	}

	@Override
	public boolean isNumeric() {
		return unaryClause.isNumeric();
	}

	@Override
	public void instantiateVariables(int i) {
		unaryClause.instantiateVariables(i);
	}

}
