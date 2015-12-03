package indigo.impl.javaclass.effects;

import indigo.Parser;
import indigo.Parser.Expression;
import indigo.interfaces.Effect;
import indigo.interfaces.PredicateValue;
import indigo.invariants.LogicExpression;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class JavaEffect implements Effect, Comparable<JavaEffect> {

	private enum ANNOTATION {
		OPERATION_NAME, PRED_NAME, PRED_VALUE /* PRED_ARGS */
	};

	private final Map<ANNOTATION, Object> parsedAnnotation;

	// MUST REMOVE THESE IN THE FUTURE.
	protected final String annotation;
	protected final Method method;

	protected final String operationName;
	protected final String predicateName;
	protected final PredicateValue predicateValue;

	JavaEffect(Method method, String annotation) {
		parsedAnnotation = parseAnnotation(method, annotation);

		this.operationName = (String) parsedAnnotation.get(ANNOTATION.OPERATION_NAME);
		this.predicateName = (String) parsedAnnotation.get(ANNOTATION.PRED_NAME);
		this.predicateValue = (PredicateValue) parsedAnnotation.get(ANNOTATION.PRED_VALUE);

		this.method = method;
		this.annotation = annotation;
	}

	private Map<ANNOTATION, Object> parseAnnotation(Method method, String annotation) {
		Map<ANNOTATION, Object> parsedTokens = new HashMap<>();
		String pattern = "\\s*(.*)\\s*\\(\\s*(.*)\\s*\\)(?:\\s*=\\s*(true|false|\\d)*)?\\s*";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(annotation);
		m.find();
		parsedTokens.put(ANNOTATION.PRED_NAME, m.group(1));
		if (m.group(3) != null) {
			parsedTokens.put(ANNOTATION.PRED_VALUE, PredicateValue.newFromString(m.group(3)));
		} else {
			// TODO: MUST IMPROVE THIS!
			// System.out.println("assume " + m.group(1) + " is numeric.");
			parsedTokens.put(ANNOTATION.PRED_VALUE, PredicateValue.newFromString(Integer.MAX_VALUE + ""));
		}
		// TODO: Must parse annotation arguments;
		// this.predicateArgs = PredicateArgs.newFromString(m.group(2));
		parsedTokens.put(ANNOTATION.OPERATION_NAME, method.getName());
		return parsedTokens;
	}

	// @Override
	// public String name() {
	// return predicateName;
	// }

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

	public String getPredicateName() {
		return predicateName;
	}

	public PredicateValue getValue() {
		if (predicateValue == null) {
			System.out.println("here");
		}
		return predicateValue;
	}

	public String getValueAsString() {
		return predicateValue.toString();
	}

	public String getOperationName() {
		return operationName;
	}

	@Override
	public int compareTo(JavaEffect other) {
		return predicateName.compareTo(other.predicateName);
	}
}
