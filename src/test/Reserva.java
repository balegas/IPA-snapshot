package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( T:x, s1, e1, s2, e2 ) :- reserva(s1, e1) and reserva(s2, e2)) => ((e2 < s1) or (s2 > e1)) and exists(x)")
public interface Reserva {

	@True("reserva($0, $1)")
	void makeReservation1(int s1, int e2);

	@True("reserva($0, $1)")
	void makeReservation2(int s1, int e2);

	@False("exists($0)")
	void xxx(T x);

	class T {
	}
}
