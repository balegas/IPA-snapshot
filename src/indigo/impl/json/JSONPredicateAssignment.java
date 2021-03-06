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

import java.util.List;

import org.json.simple.JSONObject;

import indigo.generic.GenericConstant;
import indigo.interfaces.logic.Invariant;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.logic.Value;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.Parameter;
import indigo.invariants.LogicExpression;
import indigo.runtime.Bindings;
import indigo.runtime.Parser;
import indigo.runtime.Parser.Expression;

public class JSONPredicateAssignment extends JSONClause implements PredicateAssignment {

	private final JSONClause effectClause;
	private final String opName;
	private final String predicateName;
	private final GenericConstant value;
	private final String operator;
	private final List<Parameter> params;

	// private JSONPredicateClause predicate;

	public JSONPredicateAssignment(String opName, JSONObject obj, JSONClauseContext context) {
		JSONObject predicate = (JSONObject) ((JSONObject) obj.get("formula")).get("predicate");
		JSONObject value = ((JSONObject) obj.get("value"));
		this.opName = opName;
		this.effectClause = objectToClause(obj, context);
		this.value = new GenericConstant(value);
		this.operator = (String) obj.get("type");
		this.predicateName = (String) predicate.get("name");
		this.params = JSONClause.getArgs(obj);
	}

	private JSONPredicateAssignment(String opName, String predicateName, List<Parameter> params, String operator, JSONClause clause, GenericConstant effect) {
		this.opName = opName;
		this.predicateName = predicateName;
		this.params = params;
		this.operator = operator;
		this.value = effect.copyOf();
		this.effectClause = clause.copyOf();

	}

	@Override
	public int hashCode() {
		return (predicateName /* + predicateArity */).hashCode();
		/* return (predicateName + predicateArity + value).hashCode(); */
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof PredicateAssignment) {
			PredicateAssignment otherPA = (PredicateAssignment) other;
			return this.getPredicateName().equals(
					otherPA.getPredicateName()) /*
												 * && this . predicateArity ==
												 * otherPA . predicateArity
												 */;
			/*
			 * return this.predicateName.equals(otherPA.predicateName) &&
			 * this.predicateArity == otherPA.predicateArity &&
			 * this.value.equals(((JSONPredicateAssignment) other).value);
			 */
		} else {
			return false;
		}
	}

	@Override
	public void applyEffect(LogicExpression wpc, int i) {
		effectClause.instantiateVariables(i);
		JSONClause left = null;
		if (this.effectClause instanceof JSONBinaryClause) {
			left = ((JSONBinaryClause) this.effectClause).getLeftClause();
		} else {
			System.out.println("NOT  IMPLEMENTED");
			System.exit(0);
		}
		// String predicateAsString = effectClause.toString().split("=")[0];
		String predicateAsString = left.toString();
		Bindings matches = wpc.matches(predicateAsString);
		if (!matches.isEmpty()) {
			matches.entrySet().stream().findAny().ifPresent(e -> {
				if (operator.equals("-") || operator.equals("+")) {
					wpc.replace(e.getKey().toString(), "" + this.toString());
				} else {
					wpc.replace(e.getKey().toString(), "" + value.getValueAsString());
				}
				Bindings vars = Parser.match(e.getKey(), e.getValue());
				vars.forEach((k, v) -> {
					wpc.replace(k.toString(), v.toString());
				});
			});
			wpc.assertion(String.format("%s", effectClause.toString()));
		}
	}

	@Override
	public Expression expression() {
		return effectClause.toLogicExpression().expression();
	}

	@Override
	public boolean affects(Invariant invariant) {
		// TODO: does not check arity of the matching predicate
		return !invariant.toLogicExpression().matches(predicateName).isEmpty();
	}

	@Override
	public String getOperationName() {
		return opName;
	}

	@Override
	public JSONPredicateAssignment copyOf() {
		return new JSONPredicateAssignment(opName, predicateName, params, operator, effectClause, value);
	}

	@Override
	public String toString() {
		// return opName + ": " + effectClause.toString();
		return effectClause.toString();
	}

	@Override
	public void instantiateVariables(int i) {
		effectClause.instantiateVariables(i);
	}

	@Override
	public PREDICATE_TYPE getType() {
		return value.getType();
	}

	@Override
	public String getPredicateName() {
		return predicateName;
	}

	@Override
	public boolean isType(PREDICATE_TYPE type) {
		return value.getType().equals(type);
	}

	@Override
	public Value getAssignedValue() {
		return value;
	}

	@Override
	public List<Parameter> getParams() {
		return params;
	}

	@Override
	public void updateParamTypes(List<Expression> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public Value negateValue() {
		// TODO Auto-generated method stub
		System.out.println("not implemented");
		System.exit(0);
		return null;
	}

}
