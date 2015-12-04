package indigo.impl.json;

import indigo.interfaces.Invariant;

import org.json.simple.JSONObject;

public class JSONInvariantClause extends JSONClause implements Invariant {

	private final JSONClause invariantClause;

	public JSONInvariantClause(JSONObject obj) {
		this.invariantClause = objectToClause(obj, JSONClauseContext.emptyContext());
	}

	private JSONInvariantClause(JSONInvariantClause clause) {
		this.invariantClause = clause.invariantClause.copyOf();
	}

	JSONInvariantClause(JSONClause clause) {
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
	public JSONInvariantClause copyOf() {
		return new JSONInvariantClause(this);
	}

	@Override
	public String toString() {
		return invariantClause.toString();
	}

	@Override
	public void instantiateVariables(int i) {
		System.out.println("INV - copyWithSubstituteVariables - NOT IMPLEMENTED");
		System.exit(-1);
	}

	@Override
	public Invariant mergeClause(Invariant other) {
		if (other instanceof JSONInvariantClause) {
			JSONInvariantClause otherIC = (JSONInvariantClause) other;
			if (!(this.invariantClause instanceof JSONQuantifiedClause) && (otherIC).invariantClause instanceof JSONQuantifiedClause) {
				return new JSONInvariantClause(otherIC.invariantClause.mergeClause(invariantClause));
			}
			return new JSONInvariantClause(invariantClause.mergeClause(((JSONInvariantClause) other).invariantClause));
		} else {
			System.out.println("MERGE NOT EXPECTED");
			System.exit(-1);
		}
		return null;
	}

	@Override
	public boolean affectedBy(String predicateName) {
		return !invariantClause.toLogicExpression().matches(predicateName).isEmpty();
	}

}
