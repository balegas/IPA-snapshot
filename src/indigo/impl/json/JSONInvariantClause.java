package indigo.impl.json;

import indigo.interfaces.Clause;

import org.json.simple.JSONObject;

public class JSONInvariantClause extends JSONClause {

	private final Clause clause;

	public JSONInvariantClause(JSONObject obj) {
		this.clause = objectToClause(obj, JSONClauseContext.emptyContext());
	}

	private JSONInvariantClause(Clause clause) {
		this.clause = clause;
	}

	@Override
	public int hashCode() {
		return clause.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this.clause.equals(((JSONInvariantClause) other).clause);
	}

	@Override
	public Clause copyOf() {
		return new JSONInvariantClause(clause.copyOf());
	}

	@Override
	public String toString() {
		return clause.toString();
	}

	@Override
	public boolean isNumeric() {
		return clause.isNumeric();
	}

}
