package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : x ) :- A(x) and Other(x) => B(x)")
public interface MultiplePredicateResolution {

	@True("A(x)")
	@True("Other(x)")
	public void doA();

	@False("A(x)")
	@False("Other(x)")
	public void doNotA();

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
