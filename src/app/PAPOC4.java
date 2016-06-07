package app;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Var : a, Var : b ) :- active(a) => leader(a)")

public interface PAPOC4 {

	@True("active($0)")
	// @PreTrue("leader($0)")
	public void makeActive(Var a);

	@False("leader($0)")
	// @PreFalse("active($0)")
	public void removeLeader(Var b);

	class Var {
	}
}
