package indigo;

import indigo.Parser.Expression;

import java.util.HashMap;

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
