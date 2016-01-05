package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : x, Var : y ) :- A(x, y)  => B(x, y)")
public interface ReferentialIntegrity {

	@True("A(x, y)")
	public void doA();

	@False("B(x, y)")
	public void doNotB();

	class Var {

	}

}
