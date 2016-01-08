package indigo.impl.javaclass;

import indigo.Bindings;
import indigo.Parser.Expression;
import indigo.interfaces.Invariant;
import indigo.invariants.LogicExpression;

public class JavaInvariantClause implements Invariant {

	LogicExpression invariant;

	public JavaInvariantClause(String invString) {
		this.invariant = new LogicExpression(invString);
	}

	public JavaInvariantClause() {
		this.invariant = new LogicExpression("");
	}

	JavaInvariantClause(LogicExpression invExp) {
		this.invariant = invExp;
	}

	// @Override
	public boolean affectedBy(String predicateName) {
		Bindings result = invariant.matches(predicateName);
		return result != null && !result.isEmpty();
	}

	@Override
	public Invariant mergeClause(Invariant next) {
		if (next instanceof JavaInvariantClause) {
			Expression mergedExp = Expression.merge(invariant.expression(),
					(((JavaInvariantClause) next).invariant.expression()));
			return new JavaInvariantClause(new LogicExpression(mergedExp));
		}
		assert (false);
		System.exit(0);
		return null;
	}

	@Override
	public LogicExpression toLogicExpression() {
		return invariant.copyOf();
	}

	// @Override
	// public JavaInvariantClause copyOf() {
	// return new JavaInvariantClause(invariant.copyOf());
	// }

	@Override
	public String toString() {
		return invariant.toString();
	}

	@Override
	public int hashCode() {
		return this.invariant.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof JavaInvariantClause) {
			return this.invariant.equals(((JavaInvariantClause) other).invariant);
		} else {
			return false;
		}
	}

}
