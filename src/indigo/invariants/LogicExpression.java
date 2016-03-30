package indigo.invariants;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import indigo.interfaces.logic.PredicateAssignment;
import indigo.runtime.Bindings;
import indigo.runtime.DependencyChecker;
import indigo.runtime.GetContrainedSets;
import indigo.runtime.Parser;
import indigo.runtime.Parser.Expression;

public class LogicExpression {

	private Expression parsedExpr;
	Set<Expression> assertions = new HashSet<>();

	public LogicExpression(String expr) {
		this.parsedExpr = Parser.parse(expr);
	}

	public LogicExpression(Expression expr) {
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

	public LogicExpression copyOf() {
		// System.err.println(parsedExpr.toString());
		return new LogicExpression(parsedExpr.toString());
	}

	@Override
	public int hashCode() {
		return parsedExpr.toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		LogicExpression that = (LogicExpression) other;
		return parsedExpr.toString().equals(that.parsedExpr.toString());
	}

	public void mergeClause(LogicExpression next) {
		LogicExpression newInv = new LogicExpression(parsedExpr.merge(next.parsedExpr));
		this.assertions = newInv.assertions;
		this.parsedExpr = newInv.parsedExpr;
	}

	public void applyEffect(PredicateAssignment e, int iteration) {
		e.applyEffect(this, iteration);
	}

	public Map<String, Set<PredicateAssignment>> getConstrainedSetsDependencies(Set<String> constrainedSets) {
		Map<String, Set<PredicateAssignment>> dependeciesForPredicate = Maps.newHashMap();
		parsedExpr.evaluate(new DependencyChecker(dependeciesForPredicate, constrainedSets));
		return dependeciesForPredicate;

	}

	public Set<String> getConstrainedSets() {
		Set<String> constrainedSets = Sets.newTreeSet();
		parsedExpr.evaluate(new GetContrainedSets(constrainedSets));
		return constrainedSets;
	}

}
