package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( X : x) :- A(x) => B(x)")
public interface RemoveWithEffect {

	@False("A($0)")
	void add(X x);

	@False("B($0)")
	@True("InLog($0)")
	void remove(X x);

	class X {
	}
}
