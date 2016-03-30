package indigo.conflitcs;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import indigo.generic.GenericPredicateAssignment;
import indigo.impl.json.AbstractConflictResolutionPolicy;
import indigo.interfaces.interactive.ConflictResolutionPolicy;
import indigo.interfaces.logic.Value;
import indigo.runtime.Text;

public class InputDrivenConflictResolutionPolicy extends AbstractConflictResolutionPolicy
		implements ConflictResolutionPolicy {

	private final static boolean defaultValue = true;
	private final static Value defaultBooleanValue = GenericPredicateAssignment.newBoolean(defaultValue);
	private final Iterator<String> input;
	private final PrintStream out;
	private final static Set<String> trueSet = ImmutableSet.of("TRUE", "true", "T", "t");
	private final static Set<String> falseSet = ImmutableSet.of("FALSE", "false", "F", "f");
	private static final String PROVIDE_RESOLUTION_FOR_MSG = "PLEASE PROVIDE PREFERRED PREDICATE VALUE FOR "
			+ Text.opColor("%s") + ".";
	private static final String DEFAULT_RESOLUTION_FOR_MSG = "USING DEFAULT VALUE: " + Text.opColor("%s") + ".";

	enum BOOLEAN_RESOLUTION {
		TRUE, FALSE
	}

	/**
	 * Solves all opposing conflicts with default value True.
	 */
	public InputDrivenConflictResolutionPolicy(Iterator<String> input, PrintStream out) {
		super();
		this.input = input;
		this.out = out;
	}

	@Override
	public Value resolutionFor(String predicateName) {
		Value res = conflictResolution.get(predicateName);
		if (res != null)
			return res;
		out.println(String.format(PROVIDE_RESOLUTION_FOR_MSG, predicateName));
		if (input.hasNext()) {
			boolean resolution = readBoolean();
			conflictResolution.put(predicateName, GenericPredicateAssignment.newBoolean(resolution));
			return resolutionFor(predicateName);
		} else {
			out.println(String.format(DEFAULT_RESOLUTION_FOR_MSG, defaultBooleanValue));
			return defaultBooleanValue;
		}

	}

	private boolean readBoolean() {
		while (input.hasNext()) {
			String inputStr = input.next();
			if (trueSet.contains(inputStr)) {
				return true;
			} else if (falseSet.contains(inputStr)) {
				return false;
			}
		}
		return defaultValue;
	}

}
