package app;

import indigo.annotations.False;
import indigo.annotations.Invariant;

//Non-negative number of prints.
@Invariant("forall( V : x ) :- a(x) or b(x) or c(x)")
public interface DisjunctionExample {

	@False("a($0)")
	public void not_a(V x);

	@False("b($0)")
	public void not_b(V x);

	@False("c($0)")
	public void not_c(V x);

	class V {
	}

}
