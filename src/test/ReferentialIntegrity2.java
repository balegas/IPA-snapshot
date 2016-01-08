package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : x, Var : y) :- A(x, y)  => B(x, y) and C(x, y)")
public interface ReferentialIntegrity2 {

	@True("A($0, $1)")
	public void doA(Var x, Var y);

	@False("B($0, $1)")
	public void doNotB(Var x, Var y);

	class Var {

	}

}
