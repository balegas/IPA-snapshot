package app;

import indigo.annotations.Increments;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Room : r, Day : d) :- #isBooked(r, d) <= 1")
@Invariant("forall( Person : p, Room : r, Day : d) :- #assigned(*, r, d) <= 1")
@Invariant("forall( Room : r, Day : d, Professor : p) :- isBooked(r, d) <=> assigned(p, r, d)")

public interface Calendar {

	@Increments("booked($1,$2)")
	@True("isBooked($1, $2)")
	@True("assigned($0, $1, $2)")
	public void bookRoom(Professor p, Room r, Day d);

	class Professor {
	}

	class Room {
	}

	class Day {
	}
}
