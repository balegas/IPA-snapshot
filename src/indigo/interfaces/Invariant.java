package indigo.interfaces;

public interface Invariant extends Clause<Invariant> {

	boolean affectedBy(String predicateName);

}
