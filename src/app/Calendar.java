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
