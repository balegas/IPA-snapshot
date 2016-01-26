package indigo.runtime;

import java.util.HashMap;

import indigo.runtime.Parser.Expression;

@SuppressWarnings("serial")
public class Bindings extends HashMap<Expression, Expression> {

	Bindings() {
	}

	Bindings(Expression a, Expression b) {
		put(a, b);
	}

	Bindings(Bindings a, Bindings b) {
		if (a != null)
			super.putAll(a);
		if (b != null)
			super.putAll(b);
	}
}
