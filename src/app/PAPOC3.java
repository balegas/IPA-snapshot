package app;

import indigo.annotations.False;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : a, Var : b ) :- PredicateAB(a, b) => PredicateA(a) and PredicateB(b)")

@Invariant("forall( Var : a, Var : b ) :- #PredicateAB(a, b) <= K")

@Invariant("forall( Var : a, Var : b ) :- PredicateC(a) => PredicateAB(a, b)")

public interface PAPOC3 {

	@True("PredicateAB($0, $1)")
	@Increments("#PredicateAB($0, $1)")
	public void doPredicateAB(Var a, Var b);

	@True("PredicateC($0)")
	public void doPredicateC(Var a);

	@False("PredicateA($0)")
	public void undoPredicateA(Var a);

	@False("PredicateB($0)")
	public void undoPredicateB(Var b);

	class Var {
	}
}
