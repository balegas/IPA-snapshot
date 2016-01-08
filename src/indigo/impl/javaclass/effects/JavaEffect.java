package indigo.impl.javaclass.effects;

import indigo.impl.javaclass.JavaPredicateValue;
import indigo.interfaces.PredicateAssignment;
import indigo.interfaces.Value;
import indigo.invariants.LogicExpression;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class JavaEffect implements Comparable<JavaEffect> {

	private enum ANNOTATION {
		OPERATION_NAME, PRED_NAME, PRED_VALUE /* PRED_ARGS */
	};

	protected final String annotation;
	protected final Method method;

	protected final String operationName;
	protected final String predicateName;
	protected final JavaPredicateValue predicateValue;

	JavaEffect(Method method, String annotation) {
		Map<ANNOTATION, Object> parsedAnnotation = processAnnotation(method, annotation);

		this.operationName = (String) parsedAnnotation.get(ANNOTATION.OPERATION_NAME);
		this.predicateName = (String) parsedAnnotation.get(ANNOTATION.PRED_NAME);
		this.predicateValue = (JavaPredicateValue) parsedAnnotation.get(ANNOTATION.PRED_VALUE);

		this.method = method;
		this.annotation = annotation;
	}

	public JavaEffect(String operationName, String predicateName, Method method, String annotation,
			JavaPredicateValue value) {
		this.method = method;
		this.operationName = operationName;
		this.predicateName = predicateName;
		this.annotation = annotation;
		this.predicateValue = value;
	}

	private Map<ANNOTATION, Object> processAnnotation(Method method, String annotation) {
		Map<ANNOTATION, Object> parsedTokens = new HashMap<>();
		// TODO: Should not match true/false multiple times
		String pattern = "\\s*(.*)\\s*\\(\\s*(.*)\\s*\\)(?:\\s*=\\s*(true|false|\\d)*)?\\s*";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(annotation);
		m.find();
		parsedTokens.put(ANNOTATION.PRED_NAME, m.group(1));
		if (m.group(3) != null) {
			parsedTokens.put(ANNOTATION.PRED_VALUE, JavaPredicateValue.newFromString(m.group(3)));
		} else {
			// TODO: MUST IMPROVE THIS!
			// System.out.println("assume " + m.group(1) + " is numeric.");
			parsedTokens.put(ANNOTATION.PRED_VALUE, JavaPredicateValue.newFromString(Integer.MAX_VALUE + ""));
		}
		// TODO: Must parse annotation arguments;
		// this.predicateArgs = PredicateArgs.newFromString(m.group(2));
		parsedTokens.put(ANNOTATION.OPERATION_NAME, method.getName());
		return parsedTokens;
	}

	public String applyIterationToEffect(int iteration) {
		Parameter[] pm = method.getParameters();
		Pattern p = Pattern.compile("\\$\\d+");
		Matcher mm = p.matcher(annotation);

		String res = annotation;
		while (mm.find()) {
			String num = annotation.substring(mm.start(), mm.end());
			int param = Integer.valueOf(num.substring(1));

			res = res.replace(num,
					String.format(" %s : %s%s ", pm[param].getType().getSimpleName(), pm[param].getName(), iteration));
		}
		return res;
	}

	@Override
	public int hashCode() {
		return predicateName.hashCode();
		/* return (predicateName + predicateValue.toString()).hashCode(); */
	}

	@Override
	public boolean equals(Object other) {
		return other != null && predicateName.equals(((PredicateAssignment) other).getPredicateName());
		/*
		 * public boolean equals(Object otherEffect) { JavaEffect other =
		 * (JavaEffect) otherEffect; return
		 * predicateName.equals(other.predicateName) &&
		 * predicateValue.equals(other.predicateValue);
		 */
	}

	public String getPredicateName() {
		return predicateName;
	}

	public JavaPredicateValue getValue() {
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

	public abstract boolean applyEffect(LogicExpression e, int iteration);

	public abstract JavaEffect copyWithNewValue(Value newValue);
}
