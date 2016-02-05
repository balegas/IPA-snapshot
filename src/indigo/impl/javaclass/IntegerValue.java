package indigo.impl.javaclass;

import indigo.interfaces.logic.Value;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;

class IntegerValue extends JavaPredicateValue {

	IntegerValue(int value) {
		this.value = "" + value;
	}

	@Override
	public PREDICATE_TYPE getType() {
		return PREDICATE_TYPE.Int;
	}

	@Override
	public Value copyOf() {
		return new IntegerValue(Integer.parseInt(value));
	}
}