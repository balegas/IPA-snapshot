package indigo.invariants;

import indigo.Bindings;
import indigo.Parser;
import indigo.Parser.Expression;

import java.util.HashSet;
import java.util.Set;

public class InvariantExpression {

	private Expression parsedExpr;
	Set<Expression> assertions = new HashSet<>();

	public InvariantExpression(String expr) {
		this.parsedExpr = Parser.parse(expr);
	}

	public InvariantExpression(Expression expr) {
		this.parsedExpr = expr;
	}

	public void replace(String target, String expr) {
		this.parsedExpr.replace(target, expr);
		this.parsedExpr = Parser.parse(this.parsedExpr.simplify().toString());
	}

	public Bindings matches(String value) {
		return this.parsedExpr.matches(Parser.parse(value));
	}

	public Set<Expression> assertions() {
		return assertions;
	}

	public void replaceWith(String expr) {
		this.parsedExpr = Parser.parse(expr);
	}

	public void assertion(String expr) {
		assertions.add(Parser.parse(expr).push("Bool"));
	}

	public Expression expression() {
		return parsedExpr;
	}

	@Override
	public String toString() {
		return parsedExpr.toString();
	}

	public InvariantExpression copyOf() {
		// System.err.println(parsedExpr.toString());
		return new InvariantExpression(parsedExpr.toString());
	}

	@Override
	public int hashCode() {
		return parsedExpr.toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		InvariantExpression that = (InvariantExpression) other;
		return parsedExpr.toString().equals(that.parsedExpr.toString());
	}

	public void mergeClause(InvariantExpression next) {
		InvariantExpression newInv = new InvariantExpression(parsedExpr.merge(next.parsedExpr));
		this.assertions = newInv.assertions;
		this.parsedExpr = newInv.parsedExpr;
	}

}
