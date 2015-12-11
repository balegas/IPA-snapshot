package test;

import indigo.annotations.False;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;
import indigo.annotations.True;

//TODO: Need some extra stuff to work. We want "test.Opposing" to run.

@Invariant("forall( Var : x ) :- A(x)")
@Invariant("forall( Var : x ) :- B(x) > 0")
public interface Opposing {

	@True("A(x)")
	public void doIt();

	@False("A(x)")
	@Increments("B(x)")
	public void doNotDoIt();

	class Var {

	}

}
