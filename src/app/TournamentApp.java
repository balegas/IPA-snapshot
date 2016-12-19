package app;

import indigo.annotations.Decrements;
import indigo.annotations.False;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;
import indigo.annotations.PreFalse;
import indigo.annotations.True;

// Referential Integrity
@Invariant("forall( Player : p, Tournament : t) :- enrolled(p, t) => player(p) and tournament(t)")
@Invariant("forall( Player : p, Player : q, Tournament : t) :- inMatch(p, q,  t) => enrolled(p, t)")
@Invariant("forall( Player : p, Tournament : t) :- #enrolled(p, t) <=  Capacity")
@Invariant("forall( Tournament : t) :- not (active(t) and finished(t))")

public interface TournamentApp {

	@True("player($0)")
	public OP_RESULT addPlayer(Player p);

	@False("player($0)")
	public OP_RESULT remPlayer(Player p);

	@True("tournament($0)")
	@False("active($0)")
	@False("finished($0)")
	public OP_RESULT addTournament(Tournament t, int maxPlayers);

	@False("tournament($0)")
	public OP_RESULT remTournament(Tournament t);

	@Increments("#enrolled($0, $1)")
	@True("enrolled($0, $1)")
	// @PreFalse("active($1)")
	// @PreFalse("finished($1)")
	public OP_RESULT enroll(Player p, Tournament t);

	@Decrements("#enrolled($0, $1)")
	@False("enrolled($0, $1)")
	@PreFalse("active($1)")
	@PreFalse("finished($1)")
	public OP_RESULT disenroll(Player p, Tournament t);

	@True("active($0)")
	// @PreFalse("active($0)")
	// @PreFalse("finished($0)")
	public OP_RESULT beginTournament(Tournament t);

	@True("finished($0)")
	@False("active($0)")
	// @PreTrue("active($0)")
	public OP_RESULT endTournament(Tournament t);

	@True("inMatch($0, $1, $2)")
	// @PreTrue("active($2)")
	public OP_RESULT doMatch(Player p, Player q, Tournament t);

	// public OP_RESULT viewStatus(Player p);

	enum OP_RESULT {
	};
}

class Player {
}

class Tournament {
}
