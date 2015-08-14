package indigo.impl.json;

import indigo.abtract.Clause;

import org.json.simple.JSONObject;

public class UnaryClause extends JSONClause {

	private final String operator;
	private final Clause unaryClause;

	public UnaryClause(String operator, JSONObject jsonObject, ClauseContext context) {
		super();
		this.operator = operator;
		this.unaryClause = objectToClause(jsonObject, context);
	}

	private UnaryClause(String operator, Clause clause) {
		super();
		this.operator = operator;
		this.unaryClause = clause;
	}

	@Override
	public Clause copyOf() {
		return new UnaryClause(operator, unaryClause.copyOf());
	}

	@Override
	public String toString() {
		return operator + " ( " + unaryClause.toString() + " ) ";
	}

}
