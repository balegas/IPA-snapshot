package app;

import indigo.annotations.Invariant;
import indigo.annotations.True;

// We need a new data type like state-machine that accepts a list of values with a specific order of precedence. E.g. start || end, end wins.

@Invariant("forall( Tournament : t) :- not (active(t)  and finished(t))")

public interface PAPOC5 {

	// @PreFalse("active($0)")
	// @PreFalse("finished($0)")
	@True("active($0)")
	public void begin(Tournament t);

	// @PreTrue("active($0)")
	// @False("active($0)")
	@True("finished($0)")
	public void end(Tournament t);

	class Player {
	}

	class Tournament {
	}
}
