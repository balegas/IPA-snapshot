package indigo.interfaces.logic;

public interface Invariant extends Clause<Invariant> {

	boolean affectedBy(String predicateName);

}
