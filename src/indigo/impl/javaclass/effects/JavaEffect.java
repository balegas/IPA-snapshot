package indigo.impl.javaclass.effects;

import indigo.Parser;
import indigo.Parser.Expression;
import indigo.interfaces.Effect;
import indigo.invariants.LogicExpression;

import java.lang.reflect.Method;

abstract public class JavaEffect implements Effect {

	final String name;
	final String args;
	public final Method method;
	public final String operation;

	JavaEffect(Method method, String args) {
		this.args = args;
		this.method = method;
		this.operation = method.getName();
		this.name = nameFromArgs(args);
	}

	protected String nameFromArgs(String args) {
		int i = args.indexOf('(');
		return i < 0 ? args : args.substring(0, i);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public boolean isNumeric() {
		return false;
	}

	public Expression assertion() {
		return Parser.parse("true");
	}

	@Override
	public boolean applyEffect(LogicExpression invariant, int iteration) {
		return false;
	}

	@Override
	public boolean hasEffects(LogicExpression invariant) {
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return other != null && name.equals(((JavaEffect) other).name);
	}
}
