/**
-------------------------------------------------------------------

Copyright (c) 2014 SyncFree Consortium.  All Rights Reserved.

This file is provided to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License.  You may obtain
a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

-------------------------------------------------------------------
**/
package indigo.impl.json;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import indigo.generic.GenericConstant;
import indigo.generic.GenericVariable;
import indigo.interfaces.operations.Parameter;
import indigo.invariants.LogicExpression;

public abstract class JSONClause {

	protected static final Set<String> NUMERIC_OPERATORS_SET = Sets.newHashSet("+", "-", "*", "/", "<", "<=", ">", ">=",
			"==");

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
			clause = new GenericConstant(obj);
		} else {
			String operator = (String) obj.get("type");
			if (isQuantifier(operator)) {
				Collection<Parameter> vars = getVars(obj);
				clause = new JSONQuantifiedClause(operator, vars, (JSONObject) obj.get("formula"),
						new JSONClauseContext(vars));
			} else if (isBinaryLogicOperator(operator)) {
				clause = new JSONBinaryClause(operator, (JSONObject) obj.get("left"), (JSONObject) obj.get("right"),
						context);
			} else if (isComparator(operator)) {
				clause = new JSONBinaryClause(operator, (JSONObject) obj.get("left"), (JSONObject) obj.get("right"),
						context);
			}

			else if (isBinaryNumericOperator(operator)) {
				clause = new JSONBinaryClause(operator, (JSONObject) obj.get("formula"), (JSONObject) obj.get("value"),
						context);
			} else if (isAssignment(operator)) {
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

	protected static Collection<Parameter> getVars(JSONObject obj) {
		List<GenericVariable> vars = new LinkedList<>();
		JSONArray varsNode = (JSONArray) obj.get("vars");
		varsNode.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				GenericVariable var = new GenericVariable(obj);
				vars.add(var);
			}
		});
		return ImmutableList.copyOf(vars);
	}

	public static List<Parameter> getArgs(JSONObject obj) {
		List<GenericVariable> vars = new LinkedList<>();
		JSONArray varsNode = (JSONArray) obj.get("args");
		varsNode.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				GenericVariable var = new GenericVariable((JSONObject) obj.get("value"));
				vars.add(var);
			}
		});
		return ImmutableList.copyOf(vars);
	}

	protected static Collection<Parameter> copyVars(Collection<Parameter> args) {
		List<Parameter> newArgs = new LinkedList<>();
		for (Parameter arg : args) {
			newArgs.add(arg.copyOf());
		}
		return ImmutableList.copyOf(newArgs);
	}

	public abstract void instantiateVariables(int i);

}
