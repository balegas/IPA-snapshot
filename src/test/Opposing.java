package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

//TODO: Need some extra stuff to work. We want "test.Opposing" to run.

@Invariant("forall( Var : x ) :- A(x)")
public interface Opposing {

	@True("A(x)")
	public void doIt();

	@False("A(x)")
	public void doNotDoIt();

	class Var {

	}

}
