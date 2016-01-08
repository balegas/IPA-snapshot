package indigo.impl.json;

import indigo.Bindings;
import indigo.Parser;
import indigo.Parser.Expression;
import indigo.interfaces.Invariant;
import indigo.interfaces.PREDICATE_TYPE;
import indigo.interfaces.PredicateAssignment;
import indigo.interfaces.Value;
import indigo.invariants.LogicExpression;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONPredicateAssignment extends JSONClause implements PredicateAssignment {

	private final JSONClause effectClause;
	private final String opName;
	private final String predicateName;
	private final int predicateArity;
	private final JSONConstant value;
	private final String operator;

	// private JSONPredicateClause predicate;

	public JSONPredicateAssignment(String opName, JSONObject obj, JSONClauseContext context) {
		JSONObject predicate = (JSONObject) ((JSONObject) obj.get("formula")).get("predicate");
		JSONObject value = ((JSONObject) obj.get("value"));
		this.opName = opName;
		this.effectClause = objectToClause(obj, context);
		this.value = new JSONConstant(value);
		this.operator = (String) obj.get("type");
		this.predicateName = (String) predicate.get("name");
		this.predicateArity = ((JSONArray) predicate.get("args")).size();
	}

	private JSONPredicateAssignment(String opName, String predicateName, int predicateArity, String operator,
			JSONClause clause, JSONConstant effect) {
		this.opName = opName;
		this.predicateName = predicateName;
		this.predicateArity = predicateArity;
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
			return this.getPredicateName().equals(otherPA.getPredicateName()) /*
			 * &&
			 * this
			 * .
			 * predicateArity
			 * ==
			 * otherPA
			 * .
			 * predicateArity
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
	public Expression getExpression() {
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
		return new JSONPredicateAssignment(opName, predicateName, predicateArity, operator, effectClause, value);
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

}
