package indigo.impl.javaclass.effects;

import indigo.impl.javaclass.JavaPredicateValue;

import java.lang.reflect.Method;

public abstract class Predicate extends JavaEffect {

	final boolean value;

	protected Predicate(boolean value, Method method, String args) {
		super(method, args);
		this.value = value;
	}

	protected Predicate(String operationName, String predicateName, Method method, String annotation,
			JavaPredicateValue value) {
		super(operationName, predicateName, method, annotation, value);
		this.value = Boolean.parseBoolean((String) value.getValue());
	}
}
