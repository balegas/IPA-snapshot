package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : x, Var : y ) :- A(x)  => B(y)")
public interface ReferentialIntegrity {

	@True("A($0)")
	public void doA(Var x);

	@False("B($0)")
	public void doNotB(Var x);

	class Var {

	}

}
