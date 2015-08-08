package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;

@Invariant("forall( X : x) :- A(x) or B(x) or C(x)")
public interface Disjuntion {

	@False("A($0)")
	void a(X x);

	@False("B($0)")
	void b(X x);

	@False("C($0)")
	void c(X x);

	class X {
	}
}
