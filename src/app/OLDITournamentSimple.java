/**
-------------------------------------------------------------------

Copyright (c) 2014 SyncFree Consortium.  All Rights Reserved.

This file is provided to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License.  You may obtain
a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

-------------------------------------------------------------------
**/
package app;

import indigo.annotations.Decrements;
import indigo.annotations.False;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;
import indigo.annotations.PreFalse;
import indigo.annotations.PreTrue;
import indigo.annotations.True;

//Referential Integrity
@Invariant("forall( Player : p, Tournament : t) :- enrolled(p, t)  => player(p) and tournament(t)")
@Invariant("forall( Player : p, Tournament : t) :- leader(t, p) => enrolled(p, t)")
@Invariant("forall( Player : p, Player : q, Tournament : t) :- inMatch(p, q, t) => enrolled(p, t)")

// Bounded Counters
@Invariant("forall( Player : p, Tournament : t) :- #enrolled(p, t)  <= Capacity")
@Invariant("forall( Player : p, Tournament : t) :- active(t) => leader(t, p)")

public interface OLDITournamentSimple {

	enum OP_RESULT {
	};

	@True("player($0)")
	public OP_RESULT addPlayer(Player p);

	@False("player($0)")
	public OP_RESULT remPlayer(Player p);

	@True("tournament($0)")
	public OP_RESULT addTournament(Tournament t, int maxPlayers);

	@False("tournament($0)")
	public OP_RESULT remTournament(Tournament t);

	@Decrements("pBudget($0)")
	@Increments("#enrolled($0, $1)")
	@True("enrolled($0, $1)")
	@PreFalse("active($1)")
	public OP_RESULT enroll(Player p, Tournament t);

	@Decrements("#enrolled($0, $1)")
	@False("enrolled($0, $1)")
	@PreFalse("active($1)")
	public OP_RESULT disenroll(Player p, Tournament t);

	@True("active($0)")
	@PreTrue("leader($0, Player : _ )")
	public OP_RESULT beginTournament(Tournament t);

	@True("finished($0)")
	@PreTrue("active($0)")
	public OP_RESULT endTournament(Tournament t);

	@Increments("#leader($0, $1)")
	@True("leader($0, $1)")
	public OP_RESULT setLeader(Tournament t, Player p);

	@Decrements("#leader($0, $1)")
	@False("leader($0, $1)")
	public OP_RESULT unsetLeader(Tournament t, Player p);

	@True("inMatch($0, $1, $2)")
	@PreTrue("active($2)")
	public OP_RESULT doMatch(Player p, Player q, Tournament t);

	public OP_RESULT viewStatus(Player p);

	class Player {
	}

	class Tournament {
	}
}
