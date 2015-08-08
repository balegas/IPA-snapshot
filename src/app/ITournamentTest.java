package app;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

//
//Referential Integrity
@Invariant("forall( Player : p, Tournament : t) :- leader(t, p)  => enrolled(p, t)")
@Invariant("forall( Player : p, Tournament : t) :- enrolled(p, t)  => player(p) and tournament(t)")
@Invariant("forall( Player : p, Player : q, Tournament : t) :- inMatch(p, q, t)  => active(t) and participant(p, t) and participant(q, t)")
//
// Bounded Counters
@Invariant("forall( Player : p ) :- pBudget(p) >= 0")
@Invariant("forall( Tournament : t ) :- nrPlayers(t) <= Capacity")
@Invariant("forall( Tournament : t) :- active(t)  => nrPlayers(t) >= 1 and nrLeaders(t) == 1")
//
// State transition
@Invariant("forall( Tournament : t, Player : p ) :- ( active(t) and enrolled(p,t) ) => participant(p, t)")
//
public interface ITournamentTest {

	@False("active($0)")
	@False("participant(Player : _1, $0)")
	@False("inMatch(Player : _2, Player : _3, $0)")
	public void endTournament(Tournament t);

	@True("inMatch($0, $1, $2)")
	public void doMatch(Player p, Player q, Tournament t);

	class Player {
	}

	class Team {
	}

	class Tournament {
	}
}
