package indigo.impl.json;

import indigo.invariants.LogicExpression;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public abstract class JSONClause {

	protected static final Set<String> NUMERIC_OPERATORS_SET = Sets.newHashSet("+", "-", "*", "/", "<", "<=", ">", ">=", "==");

	protected static final Set<String> NUMERIC_COMPARATORS_SET = Sets.newHashSet("<", "<=", ">", ">=", "==");

	protected static final Set<String> QUANTIFIERS_SET = Sets.newHashSet("forall", "exists");

	protected static final Set<String> BINARY_LOGIC_OPERATORS_SET = Sets.newHashSet("and", "or", "=>", "<=>");

	private LogicExpression expression;

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

	private boolean isComparator(String operator) {
		return NUMERIC_COMPARATORS_SET.contains(operator);
	}

	public JSONClause mergeClause(JSONClause next) {
		// TODO: What happens with variables that are quantified in one of the
		// clauses, but not on the other? -- I this should not happen.
		return new JSONBinaryClause("and", this, next);
	}

	// TODO: Hashcode changes when the predicate is instantiated with variables.
	// This does not affect the correctness of the algorithm, but we shoud make
	// copies of the objects, instead of modifying them.
	@Override
	public int hashCode() {
		// if (expression == null) {
		// expression = new LogicExpression(this.toString());
		// }
		// return expression.hashCode();
		return this.toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		// if (expression == null) {
		// expression = new LogicExpression(this.toString());
		// }
		// return expression.equals(((JSONClause) other).expression);
		return this.toString().equals(other.toString());
	}

	// @Override
	public LogicExpression toLogicExpression() {
		if (expression == null) {
			expression = new LogicExpression(this.toString());
		}
		return expression.copyOf();
	}

	public abstract JSONClause copyOf();

	protected JSONClause objectToClause(JSONObject obj, JSONClauseContext context) {
		JSONClause clause;
		// Test for predicate expression
		if (obj.containsKey("predicate")) {
			clause = new JSONPredicateClause((JSONObject) obj.get("predicate"), context);
		} else if (obj.get("type").equals("const") || obj.get("type").equals("variable")) {
			clause = new JSONConstant(obj);
		} else {
			String operator = (String) obj.get("type");
			if (isQuantifier(operator)) {
				Collection<JSONVariable> vars = getVars(obj);
				clause = new JSONQuantifiedClause(operator, vars, (JSONObject) obj.get("formula"), new JSONClauseContext(vars));
			} else if (isBinaryLogicOperator(operator)) {
				clause = new JSONBinaryClause(operator, (JSONObject) obj.get("left"), (JSONObject) obj.get("right"), context);
			} else if (isComparator(operator)) {
				clause = new JSONBinaryClause(operator, (JSONObject) obj.get("left"), (JSONObject) obj.get("right"), context);
			}

			else if (isBinaryNumericOperator(operator)) {
				clause = new JSONBinaryClause(operator, (JSONObject) obj.get("formula"), (JSONObject) obj.get("value"), context);
			} else if (isAssignment(operator)) {
				clause = new JSONBinaryClause(operator, (JSONObject) obj.get("formula"), (JSONObject) obj.get("value"), context);
			}
			// is unary
			else {
				clause = new JSONUnaryClause(operator, (JSONObject) obj.get("formula"), context);
			}
		}
		return clause;
	}

	protected static Collection<JSONVariable> getVars(JSONObject obj) {
		List<JSONVariable> vars = new LinkedList<>();
		JSONArray varsNode = (JSONArray) obj.get("vars");
		varsNode.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				JSONVariable var = new JSONVariable(obj);
				vars.add(var);
			}
		});
		return ImmutableList.copyOf(vars);
	}

	protected static Collection<JSONVariable> getArgs(JSONObject obj) {
		List<JSONVariable> vars = new LinkedList<>();
		JSONArray varsNode = (JSONArray) obj.get("args");
		varsNode.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				JSONVariable var = new JSONVariable((JSONObject) obj.get("value"));
				vars.add(var);
			}
		});
		return ImmutableList.copyOf(vars);
	}

	protected static Collection<JSONVariable> copyVars(Collection<JSONVariable> args) {
		List<JSONVariable> newArgs = new LinkedList<>();
		for (JSONVariable arg : args) {
			newArgs.add(arg.copyOf());
		}
		return ImmutableList.copyOf(newArgs);
	}

	public abstract void instantiateVariables(int i);

}
