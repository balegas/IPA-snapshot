package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : x ) :- A(x) and Other(x) => B(x)")
public interface MultiplePredicateResolution2 {

	@True("A($0)")
	@True("Other($0)")
	public void doA(Var x);

	@False("A($0)")
	@False("Other($0)")
	public void doNotA(Var x);

	/*
	 * Operation always breaks the invariant!!! Analysis must detect and
	 * eliminate this case.
	 * 
	 * @False("Other(x)")
	 * 
	 * @True("B(x)") public void doB();
	 */

	class Var {

	}

}
