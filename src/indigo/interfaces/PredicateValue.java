package indigo.interfaces;

public abstract class PredicateValue {

	protected Object value;

	public static PredicateValue newFromString(String value) {
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

	public abstract PredicateType getType();

}

class BooleanValue extends PredicateValue {

	BooleanValue(boolean value) {
		this.value = value;
	}

	@Override
	public PredicateType getType() {
		return PredicateType.bool;
	}

}

class IntegerValue extends PredicateValue {

	IntegerValue(int value) {
		this.value = value;
	}

	@Override
	public PredicateType getType() {
		return PredicateType.numeric;
	}
}
