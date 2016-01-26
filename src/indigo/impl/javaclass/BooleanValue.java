package indigo.impl.javaclass;

import indigo.interfaces.logic.Value;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;

public class BooleanValue extends JavaPredicateValue {

	BooleanValue(String value) {
		this.value = value;
	}

	public BooleanValue(boolean value) {
		this.value = value + "";
	}

	@Override
	public PREDICATE_TYPE getType() {
		return PREDICATE_TYPE.bool;
	}

	public static BooleanValue fromString(String value) {
		if (value.equals("true")) {
			return new BooleanValue("true");
		}
		return new BooleanValue("false");
	}

	public static BooleanValue TrueValue() {
		return new BooleanValue("true");
	}

	public static BooleanValue FalseValue() {
		return new BooleanValue("false");
	}

	public static Value newFromBool(boolean b) {
		return new BooleanValue(b);
	}

	@Override
	public Value copyOf() {
		return new BooleanValue((String) value);
	}

}