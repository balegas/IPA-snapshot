package indigo.impl.json;

import indigo.interfaces.Clause;

import org.json.simple.JSONObject;

public class JSONInvariantClause extends JSONClause {

	private final Clause invariantClause;

	public JSONInvariantClause(JSONObject obj) {
		this.invariantClause = objectToClause(obj, JSONClauseContext.emptyContext());
	}

	private JSONInvariantClause(Clause clause) {
		this.invariantClause = clause.copyOf();
	}

	@Override
	public int hashCode() {
		return invariantClause.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this.invariantClause.equals(((JSONInvariantClause) other).invariantClause);
	}

	@Override
	public JSONClause copyOf() {
		return new JSONInvariantClause(invariantClause);
	}

	@Override
	public String toString() {
		return invariantClause.toString();
	}

	@Override
	public boolean isNumeric() {
		return invariantClause.isNumeric();
	}

	@Override
	public void instantiateVariables(int i) {
		System.out.println("INV - copyWithSubstituteVariables - NOT IMPLEMENTED");
		System.exit(-1);
	}

	@Override
	public JSONInvariantClause mergeClause(Clause other) {
		if (other instanceof JSONInvariantClause) {
			JSONInvariantClause otherIC = (JSONInvariantClause) other;
			if (!(this.invariantClause instanceof JSONQuantifiedClause)
					&& (otherIC).invariantClause instanceof JSONQuantifiedClause) {
				return new JSONInvariantClause(otherIC.invariantClause.mergeClause(invariantClause));
			}
			return new JSONInvariantClause(invariantClause.mergeClause(((JSONInvariantClause) other).invariantClause));
		} else {
			System.out.println("MERGE NOT EXPECTED");
			System.exit(-1);
		}
		return null;
	}

}
