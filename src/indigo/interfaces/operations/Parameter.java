package indigo.interfaces.operations;

import indigo.interfaces.logic.enums.PREDICATE_TYPE;

public interface Parameter {

	PREDICATE_TYPE getType();

	String getName();

	Parameter copyOf();

}
