package test;

import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : x ) :- not (A(x) and B(x) and C(x))")
public interface NotAllTrue {

	@True("A(x)")
	public void doA();

	@True("B(x)")
	public void doB();

	@True("C(x)")
	public void doC();
}
