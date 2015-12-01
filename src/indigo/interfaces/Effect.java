package indigo.interfaces;

import indigo.invariants.LogicExpression;

public interface Effect {

	// protected String nameFromArgs(String args);

	public String name();

	// public boolean isNumeric();

	// public Expression assertion();

	public boolean applyEffect(LogicExpression invariant, int iteration);

	public boolean hasEffects(LogicExpression invariant);

}
