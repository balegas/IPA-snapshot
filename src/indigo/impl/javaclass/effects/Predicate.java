package indigo.impl.javaclass.effects;

import java.lang.reflect.Method;

public abstract class Predicate extends JavaEffect {

	final boolean value;

	protected Predicate(boolean value, Method method, String args) {
		super(method, args);
		this.value = value;
	}
}
