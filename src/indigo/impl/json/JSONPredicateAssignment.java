package indigo.impl.json;

import indigo.Parser.Expression;
import indigo.interfaces.Clause;
import indigo.interfaces.PredicateAssignment;
import indigo.invariants.LogicExpression;

import org.json.simple.JSONObject;

public class JSONPredicateAssignment extends JSONClause implements PredicateAssignment {

	private final Clause effectClause;
	private final String opName;

	public JSONPredicateAssignment(String opName, JSONObject obj, JSONClauseContext context) {
		this.opName = opName;
		this.effectClause = objectToClause(obj, context);
	}

	private JSONPredicateAssignment(String opName, Clause clause) {
		this.opName = opName;
		this.effectClause = clause;
	}

	@Override
	public int hashCode() {
		return effectClause.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		// TODO: Does not check opName, not sure this is intentional.
		if (o instanceof JSONPredicateAssignment) {
			return this.effectClause.equals(((JSONPredicateAssignment) o).effectClause);
		} else {
			return false;
		}
	}

	@Override
	public void applyEffect(LogicExpression wpc, int i) {
		System.out.println("NOT IMPLEMENTED --- MUST SUBSTITUTE VARIABLES, CHECK JAVA IMPLEMENTATION");
		System.exit(0);

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
		System.out.println("NOT IMPLEMENTED --- CHECK OBJECTS");
		return false;
	}

	@Override
	public String opName() {
		return opName;
	}

	@Override
	public Clause copyOf() {
		return new JSONPredicateAssignment(opName, effectClause.copyOf());
	}

	@Override
	public String toString() {
		// return opName + ": " + effectClause.toString();
		return effectClause.toString();
	}

}
