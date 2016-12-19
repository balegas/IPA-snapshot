/**
-------------------------------------------------------------------

Copyright (c) 2014 SyncFree Consortium.  All Rights Reserved.

This file is provided to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License.  You may obtain
a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

-------------------------------------------------------------------
**/
package indigo.runtime;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import indigo.conflicts.test.OperationTest;

public class Text {

	static boolean useColor = false;

	static final String opColor = (char) 27 + "[34m";
	static final String OKColor = (char) 27 + "[32m";
	static final String NOKColor = (char) 27 + "[31m";
	static final String headerColor = (char) 27 + "[1m";
	static final String clear = (char) 27 + "[0;21m";

	static final String CURRENT_OPS_MSG = headerColor("CURRENT OPERATIONS:");
	static final String PLEASE_TYPE_A_NUMBER = "PLEASE TYPE A NUMBER BETWEEN %d and %d.";
	static final String CURRENT_AUTO_RES_MSG = headerColor("AUTOMATIC CONFLICT RESOLUTION RULES:");
	static final String KEEP_CONFLICT_MSG = "TYPE (%d) TO KEEP THE CONFLICTING OPS.";
	static final String FIX_PAIR_QUESTION_MSG = "PICK A RESOLUTION FOR THE CONFLICT.";
	static final String LAST_STEP_REPORT = headerColor("LAST STEP REPORT:");
	static final String FIX_PAIR_SOLUTIONS_MSG = "SOLUTIONS FOR CONFLICT: ";
	static final String FIX_PAIR_MSG = "GOING TO ANALYSE POSSIBLE SOLUTIONS FOR CONFLICTING PAIR: ";
	static final String TO_FIX_MSG = "THERE ARE " + nokColor("%d")
			+ " CONFLICTS TO FIX. DO YOU WANT TO FIX THEM INTERACTIVELY?";
	static final String PRESS_KEY = "PRESS ANY KEY TO CONTINUE.";
	static final String NEW_LINE = System.getProperty("line.separator");
	static final String NEW_STEP = "STEP %s CONFLICTS:";
	static final String IDEMPOTENCE_TEST_MSG = headerColor("CHECKING IDEMPOTENCE.");
	static final String IDEMPOTENCE_TEST_RESULTS_MSG = "IDEMPOTENCE TEST RESULTS.";
	static final String OPPOSING_TEST_MSG = headerColor("CHECKING OPPOSING POST-CONDITIONS.");
	static final String CONFLICTS_TEST_MSG = headerColor("CHECKING CONFLICTS.");
	static final String CONFLICTS_TEST_RESULT_MSG = "CONFLICTS TEST RESULTS.";
	static final String FIX_CONFLICTS_TEST_RESULT_MSG = headerColor("RESOLUTION LOOP FINISHED.");
	static final String STOP_QUESTION = headerColor("DO YOU WANT CONTINUE THE ANALYSIS?");
	static final String FINISH_MSG = headerColor("ANALYSYS STOPPED AFTER ") + okColor("%d") + headerColor(" STEPS.");
	public static final String DUMP_CONTEXT = headerColor("CURRENT OPERATIONS EFFECTS.");
	static final Set<String> yesOrNo = ImmutableSet.of("yes", "y", "Y", "no", "n", "N");

	static String operationTestToString(OperationTest op) {
		return String.format("; " + opColor("%s") + " : " + opColor("%s"), op.asList(),
				op.isOK() ? okColor("[OK]") : nokColor(op.getConflicts().toString()));
	}

	static String okColor(String text) {
		if (useColor) {
			return OKColor + text + clear;
		}
		return text;
	}

	static String nokColor(String text) {
		if (useColor) {
			return NOKColor + text + clear;
		}
		return text;
	}

	public static String opColor(String text) {
		if (useColor) {
			return opColor + text + clear;
		}
		return text;
	}

	static String headerColor(String text) {
		if (useColor) {
			return headerColor + text + clear;
		}
		return text;
	}

	public static ANSWER parseYesOrNo(String input) {
		return (input.equals("yes") || input.equals("y") || input.equals("Y")) ? ANSWER.YES : ANSWER.NO;
	}

	enum ANSWER {
		YES, NO
	};
}