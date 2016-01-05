package indigo.impl.javaclass;

import indigo.interfaces.PREDICATE_TYPE;
import indigo.interfaces.Value;

class IntegerValue extends JavaPredicateValue {

	IntegerValue(int value) {
		this.value = value;
	}

	@Override
	public PREDICATE_TYPE getType() {
		return PREDICATE_TYPE.numeric;
	}

	@Override
	public Value copyOf() {
		return new IntegerValue((int) value);
	}
}