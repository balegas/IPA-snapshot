package test;

import indigo.annotations.Increments;
import indigo.annotations.Invariant;

@Invariant("forall( Arg : x ) :- Value(x) <= 20")
@Invariant("forall( Arg : x ) :- Counter_C(x) >= 0 and Counter_C(x) <= 30")
public interface SimpleCounter {

	@Increments("Value($0)")
	void incValue(Arg x);

	class Arg {
	}

}
