package indigo.interfaces.operations;

public interface Parameter {

	String getType();

	String getName();

	Parameter copyOf();

	void setType(String type);

}
