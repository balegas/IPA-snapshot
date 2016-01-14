package test;

import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : x ) :- not (A(x) and B(x) and C(x))")
public interface NotAllTrue {

	@True("A($0)")
	public void doA(Var x);

	@True("B($0)")
	public void doB(Var x);

	@True("C($0)")
	public void doC(Var x);
}

class Var {

}