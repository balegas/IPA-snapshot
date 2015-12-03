package indigo.interfaces;

import indigo.invariants.LogicExpression;

public interface Effect {

	public boolean applyEffect(LogicExpression invariant, int iteration);

	public boolean hasEffects(LogicExpression invariant);

}
