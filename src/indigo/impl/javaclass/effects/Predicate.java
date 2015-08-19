package indigo.effects;

import java.lang.reflect.Method;

public abstract class Predicate extends Effect {

	final boolean value;

	protected Predicate(boolean value, Method method, String args) {
		super(method, args);
		this.value = value;
	}
}
