package indigo.impl.json;

import indigo.interfaces.Clause;

import org.json.simple.JSONObject;

public class JSONUnaryClause extends JSONClause {

	private final String operator;
	private final Clause unaryClause;

	public JSONUnaryClause(String operator, JSONObject jsonObject, JSONClauseContext context) {
		super();
		this.operator = operator;
		this.unaryClause = objectToClause(jsonObject, context);
	}

	private JSONUnaryClause(String operator, Clause clause) {
		super();
		this.operator = operator;
		this.unaryClause = clause;
	}

	@Override
	public Clause copyOf() {
		return new JSONUnaryClause(operator, unaryClause.copyOf());
	}

	@Override
	public String toString() {
		return operator + " ( " + unaryClause.toString() + " ) ";
	}

	@Override
	public boolean isNumeric() {
		return unaryClause.isNumeric();
	}

}
