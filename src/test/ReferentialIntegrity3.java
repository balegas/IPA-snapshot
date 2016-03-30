package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : x, Var : y ) :- A(x)  => B(y)")
public interface ReferentialIntegrity3 {

	@True("A($0)")
	public void doA(Var x);

	@False("A($0)")
	public void doNotA(Var x);

	@True("B($0)")
	public void doB(Var x);

	@False("B($0)")
	public void doNotB(Var x);

	class Var {

	}

}
