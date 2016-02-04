package indigo.interfaces.logic;

import indigo.interfaces.logic.enums.PREDICATE_TYPE;

public interface Value {

	String getValue();

	PREDICATE_TYPE getType();

	Value copyOf();

	// Value negatedValue();

}
