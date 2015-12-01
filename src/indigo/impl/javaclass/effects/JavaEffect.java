package indigo.impl.javaclass.effects;

import indigo.Parser;
import indigo.Parser.Expression;
import indigo.interfaces.Effect;
import indigo.interfaces.PredicateValue;
import indigo.invariants.LogicExpression;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class JavaEffect implements Effect {

	public final Method method;
	protected final String annotation;
	protected String predicateName;
	private PredicateValue predicateValue;

	// private Object predicateArgs;

	// final String value;

	JavaEffect(Method method, String annotation) {
		this.annotation = annotation;
		this.method = method;
		// this.methodName = method.getName();
		parseAnnotation(annotation);

	}

	private void parseAnnotation(String annotation) {
		String pattern = "\\s*(.*)\\s*\\(\\s*(.*)\\s*\\)(?:\\s*=\\s*(true|false|\\d)*)?\\s*";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(annotation);
		m.find();
		this.predicateName = m.group(1);
		if (m.group(3) != null) {
			this.predicateValue = PredicateValue.newFromString(m.group(3));
		} else {
			// TODO: MUST IMPROVE THIS!
			System.out.println("assume " + predicateName + " is numeric.");
			this.predicateValue = PredicateValue.newFromString(Integer.MAX_VALUE + "");
		}
		// this.predicateArgs = PredicateArgs.newFromString(m.group(2));

	}

	@Override
	public String name() {
		return predicateName;
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
		return predicateName.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return other != null && predicateName.equals(((JavaEffect) other).predicateName);
	}

	public PredicateValue getValue() {
		if (predicateValue == null) {
			System.out.println("here");
		}
		return predicateValue;
	}
}
