package indigo.impl.json;

import indigo.interfaces.Clause;
import indigo.invariants.LogicExpression;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public abstract class JSONClause implements Clause {

	private LogicExpression clause;

	protected static final Set<String> NUMERIC_OPERATORS_SET = Sets
			.newHashSet("+", "-", "*", "/", "<", "<=", ">", ">=");

	protected static final Set<String> QUANTIFIERS_SET = Sets.newHashSet("forall", "exists");

	protected static final Set<String> BINARY_LOGIC_OPERATORS_SET = Sets.newHashSet("and", "or", "=>", "<=>");

	private boolean isBinaryNumericOperator(String operator) {
		return NUMERIC_OPERATORS_SET.contains(operator);
	}

	private boolean isBinaryLogicOperator(String operator) {
		return BINARY_LOGIC_OPERATORS_SET.contains(operator);
	}

	private boolean isQuantifier(String operator) {
		return QUANTIFIERS_SET.contains(operator);
	}

	// TODO: We currently do not support numeric equality. Need to extend the
	// parser and use different assignment operator
	private boolean isAssignment(String operator) {
		return operator.equals("=");
	}

	// private boolean isBinary(String operator) {
	// return isBinaryLogicOperator(operator) ||
	// isBinaryNumericOperator(operator) || isAssignment(operator);
	// }

	@Override
	public Clause mergeClause(Clause next) {
		return new JSONBinaryClause("AND", this, next);
	}

	@Override
	public int hashCode() {
		if (clause == null) {
			clause = new LogicExpression(this.toString());
		}
		return clause.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (clause == null) {
			clause = new LogicExpression(this.toString());
		}
		return clause.equals(((JSONClause) other).clause);
	}

	@Override
	public LogicExpression toLogicExpression() {
		if (clause == null) {
			clause = new LogicExpression(this.toString());
		}
		return clause;
	}

	@Override
	public abstract Clause copyOf();

	protected Clause objectToClause(JSONObject obj, JSONClauseContext context) {
		JSONClause clause;
		// Test for predicate expression
		if (obj.containsKey("predicate")) {
			clause = new JSONPredicateClause((JSONObject) obj.get("predicate"), context);
		} else if (obj.get("type").equals("const")) {
			clause = new JSONConstant(obj);
		} else {
			String operator = (String) obj.get("type");
			if (isQuantifier(operator)) {
				Collection<JSONVariable> vars = getVars(obj);
				clause = new JSONQuantifiedClause(operator, vars, (JSONObject) obj.get("formula"),
						new JSONClauseContext(vars));
			} else if (isBinaryLogicOperator(operator)) {
				clause = new JSONBinaryClause(operator, (JSONObject) obj.get("left"), (JSONObject) obj.get("right"),
						context);
			} else if (isAssignment(operator) || isBinaryNumericOperator(operator)) {
				clause = new JSONBinaryClause(operator, (JSONObject) obj.get("formula"), (JSONObject) obj.get("value"),
						context);
			}
			// is unary
			else {
				clause = new JSONUnaryClause(operator, (JSONObject) obj.get("formula"), context);
			}
		}
		return clause;
	}

	protected static Collection<JSONVariable> getVars(JSONObject obj) {
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

	protected static Collection<JSONVariable> getArgs(JSONObject obj) {
		Set<JSONVariable> vars = new HashSet<>();
		JSONArray varsNode = (JSONArray) obj.get("args");
		varsNode.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				JSONVariable var = new JSONVariable((JSONObject) obj.get("value"));
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
