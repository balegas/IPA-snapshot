package indigo.impl.javaclass;

import indigo.interfaces.PREDICATE_TYPE;
import indigo.interfaces.Value;

public abstract class JavaPredicateValue implements Value {

	protected Object value;

	public static JavaPredicateValue newFromString(String value) {
		if (value.matches("true|false")) {
			return new BooleanValue(true);
		} else if (value.matches("\\$*\\d*\\$*")) {
			return new IntegerValue(Integer.parseInt(value));
		} else {
			System.out.println("DOES NOT MATCH VALUE TYPE");
			System.exit(1);
			return null;
		}
	}

	public Object getValue() {
		return value;
	}

	public abstract PREDICATE_TYPE getType();

	@Override
	public String toString() {
		return value.toString();
	}

}
