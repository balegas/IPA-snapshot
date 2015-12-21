package indigo.impl.javaclass;

import indigo.impl.javaclass.effects.JavaEffect;
import indigo.interfaces.Operation;
import indigo.interfaces.PredicateAssignment;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

public class JavaOperation implements Operation {

	private final String opName;
	private final Set<JavaEffect> opEffects;

	public JavaOperation(String opName, ArrayList<JavaEffect> effectList) {
		this.opName = opName;
		this.opEffects = ImmutableSet.copyOf(effectList);
	}

	@Override
	public int hashCode() {
		return opName.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof JavaOperation) {
			JavaOperation jop = ((JavaOperation) o);
			return this.opName.equals(jop.opName)/*
			 * &&
			 * opEffects.equals(jop.opEffects
			 * )
			 */;
		}
		return false;
	}

	@Override
	public String opName() {
		return opName;
	}

	@Override
	public Set<PredicateAssignment> getEffects() {
		return opEffects.stream().map(e -> JavaPredicateAssignment.fromEffect(e)).collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		// return String.format("{%s : %s}", opName, opEffects);
		return String.format("%s %s", opName, opEffects);
	}

}
