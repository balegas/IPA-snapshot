package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : x ) :- A(x)  => B(x)")
public interface ResolutionAndConflict {

	@True("A(x)")
	public void doA();

	@False("A(x)")
	public void doNotA();

	@False("B(x)")
	public void doNotB();

	class Var {

	}

}
