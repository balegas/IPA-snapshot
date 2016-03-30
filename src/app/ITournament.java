package app;

import indigo.annotations.Decrements;
import indigo.annotations.False;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;
import indigo.annotations.True;

//Referential Integrity
@Invariant("forall( Player : p, Tournament : t) :- enrolled(p, t)  => player(p) and tournament(t)")
@Invariant("forall( Player : p, Tournament : t) :- leader(t, p) => enrolled(p, t)")
@Invariant("forall( Player : p, Player : q, Tournament : t) :- inMatch(p, q, t) => active(t) and participant(p, t) and participant(q, t)")

// Bounded Counters
@Invariant("forall( Player : p ) :- pBudget(p) >= 0")
@Invariant("forall( Tournament : t ) :- nrPlayers(t) <= Capacity")
@Invariant("forall( Tournament : t) :- active(t) => nrPlayers(t) >= 1 and nrLeaders(t) == 1")

// State transition
@Invariant("forall( Tournament : t, Player : p ) :- ( active(t) and enrolled(p,t) ) <=> participant(p, t)")

public interface ITournament {

	@True("player($0)")
	public void addPlayer(Player p);

	@False("player($0)")
	public void remPlayer(Player p);

	@True("tournament($0)")
	public void addTournament(Tournament t);

	@False("tournament($0)")
	public void remTournament(Tournament t);

	@Decrements("pBudget($0)")
	@Increments("nrPlayers($1)")
	@True("enrolled($0, $1)")
	@False("participant($0, $1)")
	public void enroll(Player p, Tournament t);

	@Decrements("nrPlayers($1)")
	@False("enrolled($0, $1)")
	public void disenroll(Player p, Tournament t);

	@True("active($0)")
	@True("participant(Player : _, $0)")
	public void beginTournament(Tournament t);

	@False("active($0)")
	@False("participant(Player : _1, $0)")
	@False("inMatch(Player : _2, Player : _3, $0)")
	public void endTournament(Tournament t);

	//
	@Increments("nrLeaders($0)")
	@True("leader($0, $1)")
	public void setLeader(Tournament t, Player p);

	//
	@Increments("pBudget($0)")
	public void addFunds(Player p);

	//
	@True("inMatch($0, $1, $2)")
	public void doMatch(Player p, Player q, Tournament t);

	class Player {
	}

	class Team {
	}

	class Tournament {
	}
}
