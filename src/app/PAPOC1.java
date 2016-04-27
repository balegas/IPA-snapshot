package app;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Player : p, Tournament : t ) :- enrolled(p, t) => player(p) and tournament(t)")
public interface PAPOC1 {

	@True("enrolled($0, $1)")
	public void enroll(Player p, Tournament t);

	@False("tournament($0)")
	public void remTournament(Tournament t);

	class Player {
	}

	class Tournament {
	}
}
