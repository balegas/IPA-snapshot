package app;

import indigo.annotations.False;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Player : p, Tournament : t ) :- enrolled(p, t) => player(p) and tournament(t)")
@Invariant("forall( Player : p, Tournament : t ) :- leader(p, t ) => enrolled(p, t)")
@Invariant("forall( Player : p, Tournament : t ) :- #enrolled(p, t) <= SIZE_LIMIT")

public interface PAPOC3 {

	@True("enrolled($0, $1)")
	@Increments("#enrolled($0, $1)")
	public void enrolled(Player p, Tournament t);

	@False("tournament($0)")
	public void remTournament(Tournament t);

	@True("leader($0, $1)")
	public void makeLeader(Player p, Tournament t);

	class Player {
	}

	class Tournament {
	}
}
