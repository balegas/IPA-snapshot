package indigo.impl.javaclass;

import indigo.Parser.Expression;
import indigo.interfaces.Clause;
import indigo.invariants.LogicExpression;

public class JavaInvariantClause implements Clause {

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

	@Override
	public Clause mergeClause(Clause next) throws Exception {
		if (next instanceof JavaInvariantClause) {
			Expression mergedExp = Expression.merge(invariant.expression(),
					(((JavaInvariantClause) next).invariant.expression()));
			return new JavaInvariantClause(new LogicExpression(mergedExp));
		} else {
			throw new Exception("Wrong type exception");
		}
	}

	// @Override
	public LogicExpression toLogicExpression() {
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

	@Override
	public boolean isNumeric() {
		System.out.println("NOT EXPECTED");
		System.exit(-1);
		return false;
	}
}
