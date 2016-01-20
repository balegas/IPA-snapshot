package indigo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import indigo.Text.ANSWER;
import indigo.generic.GenericConflictResolutionPolicy;
import indigo.generic.InputDrivenConflictResolutionPolicy;
import indigo.generic.OperationPairTest;
import indigo.generic.OperationTest;
import indigo.generic.PredicateFactory;
import indigo.generic.SingleOperationTest;
import indigo.impl.javaclass.JavaClassSpecification;
import indigo.impl.json.JSONSpecification;
import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Operation;

public class InteractiveAnalysis {
	private ProgramSpecification spec;
	private IndigoAnalyzer analysis;
	private AnalysisContext rootContext;
	private AnalysisContext currentContext;
	private Queue<SingleOperationTest> idempotenceTestQueue;
	private Queue<OperationPairTest> unresolvedPairwiseConflicts;
	private Queue<OperationTest> toFixQueue;

	// TODO: Operations with different predicate values must have different
	// names.
	private final Set<String> trackedOperations;
	private final Set<OperationTest> notSolvable;

	private static Iterator<String> in;
	private static PrintStream out;
	private static PrintStream resultOut;
	private final List<String> stepResults;

	private int numberOfSteps;
	private boolean stopped;
	private static final boolean breakOnEachStep = false;

	private InteractiveAnalysis() {
		this.numberOfSteps = 0;
		this.stopped = false;
		this.stepResults = Lists.newLinkedList();
		this.trackedOperations = Sets.newHashSet();
		this.notSolvable = Sets.newHashSet();
	}

	public static void main(String[] args) throws Exception {
		Args.use(args);
		String argsDump = Args.dumpArgs();

		ProgramSpecification spec = null;
		if (args[0].equals("-java")) {
			spec = new JavaClassSpecification(Class.forName(args[1]));
		} else if (args[0].equals("-json")) {

			File file = new File(args[1]);
			InputStream inputStream = new FileInputStream(file);
			byte[] buffer = new byte[65000];
			StringBuilder specFile = new StringBuilder();
			int count = -1;
			while (true) {
				count = inputStream.read(buffer);
				if (count > 0) {
					specFile.append(new String(buffer, 0, count, "UTF-8"));
				} else {
					break;
				}
			}
			inputStream.close();
			Object obj = JSONValue.parse(specFile.toString());
			spec = new JSONSpecification((JSONObject) obj);
		} else {
			System.out.println("Invalid arguments use -java className | -json path_to_spec");
		}
		if (spec != null) {
			Iterator<String> in;
			ConflictResolutionPolicy solveConflicts;

			if (Args.contains("-y")) {
				in = new AutoConfirmIterator();
			} else {
				in = new Scanner(System.in);
			}

			if (Args.contains("-f")) {
				resultOut = new PrintStream(new FileOutputStream(Args.valueOf("-f", "./analysis-output.txt")));
			} else {
				resultOut = System.out;
			}

			out = System.out;

			if (Args.contains("-ir")) {
				solveConflicts = new InputDrivenConflictResolutionPolicy(in, out);
			} else {
				solveConflicts = new GenericConflictResolutionPolicy();
			}
			InteractiveAnalysis analysis = new InteractiveAnalysis();
			analysis.init(spec, solveConflicts, in, out, resultOut);
			System.out.println("Arguments :");
			System.out.println(argsDump);
			analysis.dumpContext();
			if (breakOnEachStep) {
				System.out.println("BREAK ON EACH STEP IS ON: TYPE ANY CHARACTER BETWEEN TESTS TO CONTINUE.");
			}
			analysis.start();
		}
	}

	private void dumpContext() throws IOException {
		out.println(Text.DUMP_CONTEXT);
		for (String opName : trackedOperations) {
			out.println(currentContext.getOperation(opName));
		}
	}

	private void start() throws IOException {
		outputStart();
		do {
			prepareStep();
			resolutionLoop();
			outputCurrentState();
			promptStop();
		} while (!isFinished());
		outputEnd();

	}

	private void prepareStep() {
		numberOfSteps++;
	}

	private void outputCurrentState() throws IOException {
		StringBuilder outputString = new StringBuilder();
		outputString.append(Text.CURRENT_STATE_MSG + Text.NEW_LINE);
		stepResults.add(Text.CURRENT_STATE_MSG);
		stepResults.forEach(s -> outputString.append(s + Text.NEW_LINE));

		List<String> currentCR = currentContext.getConflictResolutionPolicy();
		if (currentCR.size() > 0) {
			out.println(Text.CURRENT_AUTO_RES_MSG);
			outputString.append(Text.CURRENT_AUTO_RES_MSG + Text.NEW_LINE);
			currentContext.getConflictResolutionPolicy().forEach(l -> {
				out.println("; " + l);
				outputString.append("; " + l + Text.NEW_LINE);
			});
		}

		out.println(Text.CURRENT_OPS_MSG);
		outputString.append(Text.CURRENT_OPS_MSG + Text.NEW_LINE);
		trackedOperations.forEach(op -> {
			Operation operation = currentContext.getOperation(op);
			out.println(operation);
			outputString.append(operation + Text.NEW_LINE);
		});

		if (resultOut != out) {
			resultOut.println(outputString);
		}
		stepResults.clear();
	}

	private void outputStart() {

	}

	private void outputEnd() throws IOException {
		StringBuilder outputString = new StringBuilder();
		outputString.append(String.format(Text.FINISH_MSG + Text.NEW_LINE, numberOfSteps));
		trackedOperations.forEach(opName -> {
			Operation op = currentContext.getOperation(opName);
			outputString.append(op + Text.NEW_LINE);
		});

		out.println(outputString);
	}

	private void promptStop() throws IOException {
		if (!isFinished()) {
			StringBuilder outputString = new StringBuilder();
			outputString.append(Text.STOP_QUESTION);
			out.println(outputString);
			ANSWER ans = readAnswer();
			if (ans.equals(ANSWER.NO)) {
				stopped = true;
			}
		}
	}

	private ANSWER readAnswer() throws IOException {
		String input = "";
		do {
			input = in.next();
		} while (!Text.yesOrNo.contains(input));
		ANSWER answer = Text.parseYesOrNo(input);
		return answer;
	}

	private void pressKeyToContinue() {
		if (!(in instanceof AutoConfirmIterator)) {
			out.println(Text.PRESS_KEY);
		}
		in.next();
	}

	private void resolutionLoop() throws IOException {

		if (!idempotenceTestQueue.isEmpty()) {
			out.println(Text.IDEMPOTENCE_TEST_MSG);
			stepResults.add(Text.IDEMPOTENCE_TEST_MSG);
			breakOnEachStep();
			while (!idempotenceTestQueue.isEmpty()) {
				SingleOperationTest op = idempotenceTestQueue.remove();
				analysis.testIdempotence(op, currentContext.childContext(false));
				stepResults.add(Text.operationTestToString(op));
			}
		}

		if (!unresolvedPairwiseConflicts.isEmpty()) {
			out.println(Text.CONFLICTS_TEST_MSG);
			stepResults.add(Text.CONFLICTS_TEST_MSG);
			breakOnEachStep();
			while (!unresolvedPairwiseConflicts.isEmpty()) {
				OperationPairTest opPair = unresolvedPairwiseConflicts.remove();
				analysis.testPair(opPair, currentContext.childContext(false));
				stepResults.add(Text.operationTestToString(opPair));
				if (opPair.isConflicting()) {
					if (!notSolvable.contains(opPair)) {
						toFixQueue.add(opPair);
					} else {
						opPair.setIgnored();
					}
				}
			}
		}

		outputCurrentState();

		if (!toFixQueue.isEmpty()) {
			out.println(String.format(Text.TO_FIX_MSG, toFixQueue.size()));
			ANSWER ans = readAnswer();

			Set<Operation> newOps = Sets.newHashSet();
			if (ans.equals(ANSWER.YES)) {
				while (!toFixQueue.isEmpty()) {
					OperationTest opPair = toFixQueue.remove();
					out.println(String.format(Text.FIX_PAIR_MSG, opPair));
					if (opPair.toString().equals("[disenroll , beginTournament ] : [CONFLICT]")) {
						System.out.println("here");
					}
					breakOnEachStep();
					List<Operation> result = analysis.solveConflict(opPair, currentContext.childContext(false));
					out.println(String.format(Text.FIX_PAIR_SOLUTIONS_MSG, opPair));
					for (int i = 0; i < result.size(); i++) {
						out.println(String.format("(%d) : " + result.get(i), i));
					}
					out.println(Text.FIX_PAIR_QUESTION_MSG);
					out.println(String.format(Text.KEEP_CONFLICT_MSG, result.size()));
					int choice = readInteger(0, result.size());
					if (choice == result.size()) {
						notSolvable.add(opPair);
						opPair.setIgnored();
					} else {
						opPair.setConflictSolved();
						// resolution should be a single operation.
						Operation transformedOp = result.get(choice);
						newOps.add(transformedOp);
						Set<OperationPairTest> newOpPairs = Sets.cartesianProduct(newOps, spec.getOperations()).stream()
								.map(ops -> new OperationPairTest(ops.get(0).opName(), ops.get(1).opName()))
								.collect(Collectors.toSet());
						newOpPairs.forEach(op -> {
							if (!unresolvedPairwiseConflicts.contains(op)) {
								unresolvedPairwiseConflicts.add(op);
							}
						});
					}
					stepResults.add(Text.operationTestToString(opPair));
				}
			}
			currentContext = currentContext.childContext(newOps, false);
		}

		out.println(Text.FIX_CONFLICTS_TEST_RESULT_MSG);

	}

	private int readInteger(int startRange, int endRange) {
		int parsedInt = -1;
		do {
			String input = in.next();
			try {
				parsedInt = Integer.parseInt(input);
			} catch (NumberFormatException e) {
				out.println(String.format(Text.PLEASE_TYPE_A_NUMBER, startRange, endRange));
			}
		} while (!(parsedInt >= startRange && parsedInt <= endRange));
		return parsedInt;
	}

	private void breakOnEachStep() throws IOException {
		if (breakOnEachStep) {
			// outputCurrentState();
			pressKeyToContinue();
		}
	}

	private boolean isFinished() {
		return unresolvedPairwiseConflicts.isEmpty()
				/* && opposingTestQueue.isEmpty() */ || stopped;
	}

	@SuppressWarnings("unchecked")
	private void init(ProgramSpecification spec, ConflictResolutionPolicy resolutionPolicy, Iterator<String> input,
			PrintStream consoleOutput, PrintStream resultOutput) throws IOException {
		Set<Operation> operations = spec.getOperations();

		this.spec = spec;
		this.analysis = new IndigoAnalyzer(spec, resolutionPolicy != null);
		this.rootContext = AnalysisContext.getNewContext(operations, resolutionPolicy, PredicateFactory.getFactory());
		this.currentContext = rootContext;

		this.idempotenceTestQueue = Queues.newLinkedBlockingQueue();
		// this.opposingTestQueue = Queues.newLinkedBlockingQueue();
		this.unresolvedPairwiseConflicts = Queues.newLinkedBlockingQueue();
		this.toFixQueue = Queues.newLinkedBlockingQueue();
		// this.resolutionPolicy = resolutionPolicy;
		currentContext = rootContext.childContext(false);

		operations.forEach(op -> {
			idempotenceTestQueue.add(new SingleOperationTest(op.opName()));
			trackedOperations.add(op.opName());
		});

		in = input;
		out = consoleOutput;
		resultOut = resultOutput;

		Set<OperationTest> repeated = Sets.newHashSet();
		Sets.cartesianProduct(operations, operations).stream().forEach(opsPair -> {
			Operation firstOp = opsPair.get(0);
			Operation secondOp = opsPair.get(1);
			OperationPairTest operationPair = new OperationPairTest(firstOp.opName(), secondOp.opName());
			if (!repeated.contains(operationPair)) {
				this.unresolvedPairwiseConflicts.add(operationPair);
				repeated.add(operationPair);
			}
		});

		dumpContext();

	}
}

class Text {
	public static final String CURRENT_OPS_MSG = "CURRENT OPERATIONS.";
	public static final String PLEASE_TYPE_A_NUMBER = "PLEASE TYPE A NUMBER BETWEEN %d and %d.";
	static final String CURRENT_AUTO_RES_MSG = "AUTOMATIC CONFLICT RESOLUTION RULES.";
	static final String KEEP_CONFLICT_MSG = "TYPE (%d) TO KEEP THE CONFLICTING OPS.";
	static final String FIX_PAIR_QUESTION_MSG = "PICK A RESOLUTION FOR THE CONFLICT.";
	static final String CURRENT_STATE_MSG = "CURRENT STATE.";
	static final String FIX_PAIR_SOLUTIONS_MSG = "SOLUTIONS FOR CONFLICT %s:";
	static final String FIX_PAIR_MSG = "GOING TO ANALYSE POSSIBLE SOLUTIONS FOR CONFLICTING PAIR: %s.";
	static final String TO_FIX_MSG = "THERE ARE %d CONFLICTS TO FIX. DO YOU WANT TO FIX THEM INTERACTIVELY?";
	static final String PRESS_KEY = "PRESS ANY KEY TO CONTINUE.";
	static final String NEW_LINE = System.getProperty("line.separator");
	static final String NEW_STEP = "STEP %s CONFLICTS:";
	static final String IDEMPOTENCE_TEST_MSG = "CHECKING IDEMPOTENCE.";
	static final String IDEMPOTENCE_TEST_RESULTS_MSG = "IDEMPOTENCE TEST RESULTS.";
	static final String OPPOSING_TEST_MSG = "CHECKING OPPOSING POST-CONDITIONS.";
	static final String CONFLICTS_TEST_MSG = "CHECKING CONFLICTS.";
	static final String CONFLICTS_TEST_RESULT_MSG = "CONFLICTS TEST RESULTS.";
	static final String FIX_CONFLICTS_TEST_RESULT_MSG = "CONFLICTS RESOLUTION RESULTS.";
	static final String STOP_QUESTION = "DO YOU WANT CONTINUE THE ANALYSIS?";
	static final String FINISH_MSG = "ANALYSYS STOPPED AFTER %d STEPS.";
	public static final String DUMP_CONTEXT = "CURRENT OPERATIONS EFFECTS.";
	static final Set<String> yesOrNo = ImmutableSet.of("yes", "y", "Y", "no", "n", "N");

	static String operationTestToString(OperationTest op) {
		return String.format("; %s : %s", op.asList(), op.isOK() ? "[OK]" : op.getConflicts());
	}

	public static ANSWER parseYesOrNo(String input) {
		return (input.equals("yes") || input.equals("y") || input.equals("Y")) ? ANSWER.YES : ANSWER.NO;
	}

	enum ANSWER {
		YES, NO
	};
}
