package test;

import indigo.annotations.Numeric;
import indigo.annotations.Decrements;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;

@Invariant("forall( Arg : x ) :- Counter_A(x) > 0")
@Invariant("forall( Arg : x ) :- Counter_B(x) <= 20")
@Invariant("forall( Arg : x ) :- Counter_C(x) >= 0 and Counter_C(x) <= 30")
public interface Numerics {

	@Numeric
	final String Counter_A = "Counter_A( Arg : x )";

	@Numeric
	final String Counter_B = "Counter_B( Arg : x )";

	@Numeric
	final String Counter_C = "Counter_C( Arg : x )";

	@Increments(Counter_A)
	void incA(Arg x);

	@Decrements(Counter_A)
	void decA(Arg x);

	@Increments(Counter_B)
	void incB(Arg x);

	@Decrements(Counter_B)
	void decB(Arg x);

	@Increments(Counter_C)
	void incC(Arg x);

	@Decrements(Counter_C)
	void decC(Arg x);

	class Arg {
	}

}
