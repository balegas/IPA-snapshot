package indigo.generic;

import java.util.Set;

import indigo.interfaces.PredicateAssignment;

public interface EffectSetFunction {

	public Set<PredicateAssignment> transformEffects(Set<PredicateAssignment> set);

}
