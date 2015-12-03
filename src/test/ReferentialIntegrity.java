package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : x ) :- A(x)  => B(x)")
public interface ReferentialIntegrity {

	@True("A(x)")
	public void doA();

	@False("B(x)")
	public void doNotB();

	class Var {

	}

}
