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
