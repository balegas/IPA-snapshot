package indigo.effects;

import indigo.Parser;
import indigo.Parser.Expression;
import indigo.invariants.InvariantExpression;

import java.lang.reflect.Method;

abstract public class Effect {

	final String name;
	final String args;
	public final Method method;
	public final String operation;

	Effect(Method method, String args) {
		this.args = args;
		this.method = method;
		this.operation = method.getName();
		this.name = nameFromArgs(args);
	}

	protected String nameFromArgs(String args) {
		int i = args.indexOf('(');
		return i < 0 ? args : args.substring(0, i);
	}

	public String name() {
		return name;
	}

	public boolean isNumeric() {
		return false;
	}

	public Expression assertion() {
		return Parser.parse("true");
	}

	public boolean applyEffect(InvariantExpression invariant, int iteration) {
		return false;
	}

	public boolean hasEffects(InvariantExpression invariant) {
		return false;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public boolean equals(Object other) {
		return other != null && name.equals(((Effect) other).name);
	}
}
