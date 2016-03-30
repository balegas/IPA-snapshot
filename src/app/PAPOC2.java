package app;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : a, Var : b ) :- PredicateAB(a, b) => PredicateA(a) and PredicateB(b)")
@Invariant("forall( Var : a, Var : b, Var : c ) :- PredicateC(c) => PredicateAB(a, b)")
public interface PAPOC2 {

	@True("PredicateAB($0, $1)")
	public void doPredicateAB(Var a, Var b);

	@False("PredicateA($0)")
	public void undoPredicateA(Var a);

	@False("PredicateB($0)")
	public void undoPredicateB(Var b);

	@True("PredicateC($0)")
	public void doPredicateC(Var c);

	@False("PredicateC($0)")
	public void undoPredicateC(Var c);

	class Var {
	}
}
