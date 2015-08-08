package app;

import indigo.annotations.Decrements;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;

//
//Referential Integrity
@Invariant("forall( Player : p ) :- pBudget(p) >= 0")
//
public interface SimpleCounterTest {

	@Increments("pBudget($0)")
	public void addFunds(Player p);

	@Decrements("pBudget($0)")
	public void buyPower(Player p);

	class Player {
	}

	class Team {
	}

	class Tournament {
	}
}
