package indigo.impl.json;

import indigo.abtract.Clause;
import indigo.invariants.InvariantExpression;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableSet;

public abstract class JSONClause implements Clause {

	protected InvariantExpression clause;

	private boolean isBinary(String operator) {
		return operator.equals("AND") || operator.equals("OR") || operator.equals("=>") || operator.equals("<=>");
	}

	private boolean isQuantifier(String operator) {
		return operator.equals("forall") || operator.equals("exists");
	}

	@Override
	public Clause mergeClause(Clause next) {
		return new BinaryClause("AND", this, next);
	}

	@Override
	public int hashCode() {
		if (clause == null) {
			clause = new InvariantExpression(this.toString());
		}
		return clause.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return clause.equals(other);
	}

	@Override
	public InvariantExpression toInvExpression() {
		if (clause == null) {
			clause = new InvariantExpression(this.toString());
		}
		return clause;
	}

	@Override
	public abstract Clause copyOf();

	protected Clause objectToClause(JSONObject obj, ClauseContext context) {
		JSONClause clause;
		// Test for predicate expression
		if (obj.containsKey("predicate")) {
			clause = new PredicateClause((JSONObject) obj.get("predicate"), context);
		} else {
			String operator = (String) obj.get("type");
			if (isQuantifier(operator)) {
				Collection<JSONVariable> vars = getVars(obj);
				clause = new QuantifiedClause(operator, vars, (JSONObject) obj.get("formula"), new ClauseContext(vars));
			} else if (isBinary(operator)) {
				clause = new BinaryClause(operator, (JSONObject) obj.get("left"), (JSONObject) obj.get("right"),
						context);
			}
			// is unary
			else {
				clause = new UnaryClause(operator, (JSONObject) obj.get("formula"), context);
			}
		}
		return clause;
	}

	private static Collection<JSONVariable> getVars(JSONObject obj) {
		Set<JSONVariable> vars = new HashSet<>();
		JSONArray varsNode = (JSONArray) obj.get("vars");
		varsNode.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				JSONVariable var = new JSONVariable(obj);
				vars.add(var);
			}
		});
		return ImmutableSet.copyOf(vars);
	}

	protected static Collection<JSONVariable> copyVars(Collection<JSONVariable> args) {
		Set<JSONVariable> newArgs = new HashSet<>();
		for (JSONVariable arg : args) {
			newArgs.add(arg.copyOf());
		}
		return ImmutableSet.copyOf(newArgs);
	}
}
