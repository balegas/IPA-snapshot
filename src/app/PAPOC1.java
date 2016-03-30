package app;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Type : a, Type : b ) :- PredicateAB(a, b) => PredicateA(a) and PredicateB(b)")
public interface PAPOC1 {

	@True("PredicateAB($0, $1)")
	public void doPredicateAB(Var a, Var b);

	@False("PredicateA($0)")
	public void undoPredicateA(Var a);

	@False("PredicateB($0)")
	public void undoPredicateB(Var b);

	class Var {
	}
}
