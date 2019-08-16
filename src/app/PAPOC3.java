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
