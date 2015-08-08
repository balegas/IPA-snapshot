package indigo.impl.javaclass;

import indigo.abtract.Operation;
import indigo.abtract.PredicateAssignment;
import indigo.effects.Effect;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

public class JavaOperation implements Operation {

	private final String opName;
	private final Set<Effect> opEffects;

	public JavaOperation(String opName, Set<Effect> effectListPerOperation) {
		this.opName = opName;
		this.opEffects = effectListPerOperation;
	}

	public JavaOperation(String opName, ArrayList<Effect> effectList) {
		this.opName = opName;
		this.opEffects = ImmutableSet.copyOf(effectList);

	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof JavaOperation) {
			// TODO: Effect comparator does not check equality very precisely.
			JavaOperation jop = ((JavaOperation) o);
			return this.opName.equals(jop.opName) && opEffects.equals(jop.opEffects);
		}
		return false;
	}

	@Override
	public String opName() {
		return opName;
	}

	@Override
	public Set<PredicateAssignment> getPredicateAssignments() {
		return opEffects.stream().map(e -> JavaPredicateAssignment.fromEffect(e)).collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		// return String.format("{%s : %s}", opName, opEffects);
		return String.format("%s", opName);
	}

}
