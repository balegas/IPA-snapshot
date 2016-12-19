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
import indigo.annotations.Numeric;
import indigo.annotations.True;

@Invariant("forall( Show : s) :- VotesRemaining(s) >= 0")
@Invariant("forall( Show : s, Voter : v, Participant : p) :- vote(s, v, p) => participant(s, p)")
public interface Dreamboat {

	@Numeric
	final String VotesRemaining = "VotesRemaining( Show : s )";

	@Numeric
	final String ParticipantVotes = "Votes( Show : s, Participant : p )";

	@Decrements(VotesRemaining)
	@Increments(ParticipantVotes)
	@True("vote($0, $1, $2)")
	void vote(Show s, Voter v, Participant p);

	@Increments(VotesRemaining)
	@False("vote($0, Voter : _, $1)")
	@False("participant($0, $1)")
	void eliminate(Show s, Participant p);

	class Voter {
	}

	class Participant {
	}

	class Show {
	}
}
