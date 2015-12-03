package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

//TODO: Need some extra stuff to work. We want "test.Opposing" to run.

@Invariant("forall( Var : x ) :- A(x) and true")
public interface OpposingForDebug {

	@True("A(x)")
	@True("B(x)")
	public void doIt();

	@False("A(x)")
	public void doNotDoIt();

	class Var {

	}

}
