package indigo.impl.javaclass;

import indigo.interfaces.PREDICATE_TYPE;

public class BooleanValue extends JavaPredicateValue {

	BooleanValue(boolean value) {
		this.value = value;
	}

	@Override
	public PREDICATE_TYPE getType() {
		return PREDICATE_TYPE.bool;
	}

	public static BooleanValue fromString(String value) {
		if (value.equals("true")) {
			return new BooleanValue(true);
		}
		return new BooleanValue(false);
	}

	public static BooleanValue TrueValue() {
		return new BooleanValue(true);
	}

	public static BooleanValue FalseValue() {
		return new BooleanValue(false);
	}

}