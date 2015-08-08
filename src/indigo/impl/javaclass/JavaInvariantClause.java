package indigo.impl.javaclass;

import indigo.Parser.Expression;
import indigo.abtract.Clause;
import indigo.invariants.InvariantExpression;

public class JavaInvariantClause implements Clause {

	InvariantExpression invariant;

	public JavaInvariantClause(String invString) {
		this.invariant = new InvariantExpression(invString);
	}

	public JavaInvariantClause() {
		this.invariant = new InvariantExpression("");
	}

	JavaInvariantClause(InvariantExpression invExp) {
		this.invariant = invExp;
	}

	@Override
	public Clause mergeClause(Clause next) throws Exception {
		if (next instanceof JavaInvariantClause) {
			Expression mergedExp = Expression.merge(invariant.expression(), (((JavaInvariantClause) next).invariant.expression()));
			return new JavaInvariantClause(new InvariantExpression(mergedExp));
		} else {
			throw new Exception("Wrong type exception");
		}
	}

	@Override
	public InvariantExpression toInvExpression() {
		return invariant.copyOf();
	}

	@Override
	public JavaInvariantClause copyOf() {
		return new JavaInvariantClause(invariant.copyOf());
	}

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
