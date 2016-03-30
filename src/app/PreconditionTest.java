package app;

import indigo.annotations.Decrements;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;
import indigo.annotations.PreFalse;
import indigo.annotations.PreTrue;
import indigo.annotations.True;

//Numerical
@Invariant("forall( Player : p, Tournament : t) :- #enrolled(p, t) <= K")

// Referential Integrity
@Invariant("forall( Player : p, Tournament : t) :- enrolled(p, t)  => player(p) and tournament(t)")
@Invariant("forall( Player : p, Tournament : t) :- leader(t, p) => enrolled(p, t)")
@Invariant("forall( Player : p, Player : q, Tournament : t) :- inMatch(p, q, t) => enrolled(p, t)")

public interface PreconditionTest {

	enum OP_RESULT {
	};

	@Decrements("pBudget($0)")
	@Increments("#enrolled($0, $1)")
	@True("enrolled($0, $1)")
	@PreFalse("active($1)")
	public OP_RESULT enroll(Player p, Tournament t);

	@True("inMatch($0, $1, $2)")
	@PreTrue("active($2)")
	public OP_RESULT doMatch(Player p, Player q, Tournament t);

	public OP_RESULT viewStatus(Player p);

	class Player {
	}

	class Tournament {
	}
}
