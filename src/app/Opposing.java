package app;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : x ) :- A(x) and true")
public interface Opposing {

	@True("A(x)")
	public void doIt();

	@False("A(x)")
	public void doNotDoIt();

	class Var {

	}

}
