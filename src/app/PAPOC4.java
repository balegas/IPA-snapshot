package app;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.PreTrue;
import indigo.annotations.True;

@Invariant("forall( Var : a, Var : b ) :- active(a) => leader(a)")

public interface PAPOC4 {

	@PreTrue("leader($0)")
	@True("active($0)")
	public void doLeader(Var a);

	@False("leader($0)")
	@False("active($0)")
	public void undoPredicateD(Var b);

	class Var {
	}
}
