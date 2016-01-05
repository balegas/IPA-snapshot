package indigo.interfaces;

public interface Value {

	Object getValue();

	PREDICATE_TYPE getType();

	Value copyOf();

	// Value negatedValue();

}
