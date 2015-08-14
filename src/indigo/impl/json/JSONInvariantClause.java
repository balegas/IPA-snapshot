package indigo.impl.json;

import indigo.abtract.Clause;

import org.json.simple.JSONObject;

public class JSONInvariantClause extends JSONClause {

	private final Clause clause;

	public JSONInvariantClause(JSONObject obj) {
		this.clause = objectToClause(obj, ClauseContext.emptyContext());
	}

	private JSONInvariantClause(Clause clause) {
		this.clause = clause;
	}

	@Override
	public Clause copyOf() {
		return new JSONInvariantClause(clause.copyOf());
	}

	@Override
	public String toString() {
		return clause.toString();
	}

}
