package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : x ) :- A(x) => B(x)")
public interface MultiplePredicateResolution {

	@True("A($0)")
	public void doA(Var x);

	@False("A($0)")
	public void doNotA(Var x);

	@False("B($0)")
	@False("A($0)")
	public void doNotBNotA(Var x);

	class Var {

	}

}
