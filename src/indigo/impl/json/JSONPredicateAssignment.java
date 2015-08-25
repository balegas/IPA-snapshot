package indigo.impl.json;

import indigo.Bindings;
import indigo.Parser;
import indigo.Parser.Expression;
import indigo.interfaces.Clause;
import indigo.interfaces.PredicateAssignment;
import indigo.invariants.LogicExpression;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONPredicateAssignment extends JSONClause implements PredicateAssignment {

	private final JSONClause effectClause;
	private final String opName;
	private final String predicateName;
	private final int predicateArity;
	private final JSONConstant effect;
	private final String operator;

	// private JSONPredicateClause predicate;

	public JSONPredicateAssignment(String opName, JSONObject obj, JSONClauseContext context) {
		JSONObject predicate = (JSONObject) ((JSONObject) obj.get("formula")).get("predicate");
		JSONObject value = ((JSONObject) obj.get("value"));
		this.opName = opName;
		this.effectClause = objectToClause(obj, context);
		this.effect = new JSONConstant(value);
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
		this.effect = effect.copyOf();
		this.effectClause = clause.copyOf();
	}

	@Override
	public int hashCode() {
		return (predicateName + predicateArity).hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof JSONPredicateAssignment) {
			JSONPredicateAssignment otherPA = (JSONPredicateAssignment) other;
			return this.predicateName.equals(otherPA.predicateName) && this.predicateArity == otherPA.predicateArity;
		} else {
			return false;
		}
	}

	@Override
	public boolean applyEffectOnLogicExpression(LogicExpression wpc, int i) {
		effectClause.instantiateVariables(i);
		Clause left = null;
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
					wpc.replace(e.getKey().toString(), "" + effect.getValueAsString());
				}
				Bindings vars = Parser.match(e.getKey(), e.getValue());
				vars.forEach((k, v) -> {
					wpc.replace(k.toString(), v.toString());
				});
			});
			wpc.assertion(String.format("%s", effectClause.toString()));
			return true;
		}
		return false;
	}

	@Override
	public boolean isNumeric() {
		return effectClause.isNumeric();
	}

	@Override
	public Expression getAssertion() {
		return effectClause.toLogicExpression().expression();
	}

	@Override
	public boolean hasEffectIn(Clause otherClause) {
		// TODO: does not check arity of the matching predicate
		return !otherClause.toLogicExpression().matches(predicateName).isEmpty();
	}

	@Override
	public String opName() {
		return opName;
	}

	@Override
	public JSONPredicateAssignment copyOf() {
		return new JSONPredicateAssignment(opName, predicateName, predicateArity, operator, effectClause, effect);
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

}
