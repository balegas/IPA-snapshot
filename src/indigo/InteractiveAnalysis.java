package indigo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import indigo.Text.ANSWER;
import indigo.impl.javaclass.GenericConflictResolutionPolicy;
import indigo.impl.javaclass.JavaClassSpecification;
import indigo.impl.json.JSONSpecification;
import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Operation;
import indigo.interfaces.PredicateAssignment;

public class InteractiveAnalysis {
	private IndigoAnalyzer analysis;
	// private ConflictResolutionPolicy resolutionPolicy;
	private AnalysisContext rootContext;
	private AnalysisContext currentContext;
	private Queue<OperationTest> opposingTestQueue;
	private Queue<SingleOperationTest> idempotenceTestQueue;
	private Queue<OperationTest> unresolvedPairwiseConflicts;
	private Queue<OperationTest> toFixQueue;
	// TODO: Operations with different predicate values must have different
	// names.
	private final Set<String> trackedOperations;

	private Iterator<String> in;
	private PrintStream out;
	private final List<String> stepResults;

	private int numberOfSteps;
	private boolean stopped;
	private static final boolean breakOnEachStep = true;

	private InteractiveAnalysis() {
		this.numberOfSteps = 0;
		this.stopped = false;
		this.stepResults = Lists.newLinkedList();
		this.trackedOperations = Sets.newHashSet();
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
			ConflictResolutionPolicy solveConflicts = new GenericConflictResolutionPolicy();

			Iterator<String> in;
			if (args.length >= 3 && args[2].equals("-y")) {
				in = new AutoConfirmIterator();
			} else {
				in = new Scanner(System.in);
			}
			InteractiveAnalysis analysis = new InteractiveAnalysis();
			analysis.init(spec, solveConflicts, in, System.out);
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
		for (String op : trackedOperations) {
			Collection<PredicateAssignment> effects = currentContext.getOperationEffects(op, true);
			out.println(Text.operationEffectsToString(op, effects));
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
		// stepResults.add(String.format((Text.NEW_STEP + Text.NEW_LINE),
		// numberOfSteps));
	}

	private void outputCurrentState() throws IOException {
		StringBuilder outputString = new StringBuilder();
		stepResults.forEach(s -> outputString.append(s));
		out.println(outputString.toString());
		stepResults.clear();
	}

	private void outputStart() {
		// TODO Auto-generated method stub

	}

	private void outputEnd() throws IOException {
		StringBuilder outputString = new StringBuilder();
		outputString.append(String.format(Text.FINISH_MSG, numberOfSteps) + Text.NEW_LINE);
		currentContext.getAllOperationEffects(trackedOperations, true).forEach(pair -> {
			outputString.append(Text.operationEffectsToString(pair.getFirst(), pair.getSecond()) + Text.NEW_LINE);
		});
		out.println(outputString);
	}

	private void promptStop() throws IOException {
		StringBuilder outputString = new StringBuilder();
		outputString.append(Text.STOP_QUESTION + Text.NEW_LINE);
		out.println(outputString);
		ANSWER ans = readAnswer();
		if (ans.equals(ANSWER.NO)) {
			stopped = true;
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
		out.println(Text.PRESS_KEY);
		in.next();
	}

	private void resolutionLoop() throws IOException {

		if (!idempotenceTestQueue.isEmpty()) {
			out.println(Text.IDEMPOTENCE_TEST_MSG);
			breakOnEachStep();
			while (!idempotenceTestQueue.isEmpty()) {
				SingleOperationTest op = idempotenceTestQueue.remove();
				analysis.checkNonIdempotent(op, currentContext);
				stepResults.add(Text.operationTestToString(op) + Text.NEW_LINE);
			}
		}

		outputCurrentState();

		// Verificar as opposing post-conditions neste passo.
		if (!unresolvedPairwiseConflicts.isEmpty()) {
			out.println(Text.CONFLICTS_TEST_MSG);
			breakOnEachStep();
			while (!unresolvedPairwiseConflicts.isEmpty()) {
				OperationTest opPair = unresolvedPairwiseConflicts.remove();
				analysis.checkConflicting(opPair, currentContext);
				stepResults.add(Text.operationTestToString(opPair) + Text.NEW_LINE);
				if (opPair.isConflicting()) {
					toFixQueue.add(opPair);
				}
			}
		}

		if (!toFixQueue.isEmpty()) {
			out.println(String.format(Text.TO_FIX_MSG, toFixQueue.size()));
			ANSWER ans = readAnswer();
			Collection<Operation> newOps = Sets.newHashSet();
			if (ans.equals(ANSWER.YES)) {
				while (!toFixQueue.isEmpty()) {
					OperationTest opPair = toFixQueue.remove();
					out.println(String.format(Text.FIX_PAIR_MSG, opPair));
					breakOnEachStep();
					Collection<List<Operation>> result = analysis.solveConflict(opPair, currentContext);
					out.println(Text.FIX_PAIR_SOLUTIONS_MSG);

					result.forEach(r -> {
						try {
							out.println("; " + r);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
				}
			}
		}

		// breakOnEachStep();
		// Must test if any new fixes have created new conflicts.
		if (!opposingTestQueue.isEmpty()) {
			stepResults.add(Text.OPPOSING_TEST_MSG + Text.NEW_LINE);
			opposingTestQueue.forEach(opPair -> {

			});
		}

	}

	private void breakOnEachStep() throws IOException {
		if (breakOnEachStep) {
			// outputCurrentState();
			pressKeyToContinue();
		}
	}

	private boolean isFinished() {
		return unresolvedPairwiseConflicts.isEmpty() && opposingTestQueue.isEmpty() || stopped;
	}

	@SuppressWarnings("unchecked")
	private void init(ProgramSpecification spec, ConflictResolutionPolicy resolutionPolicy, Iterator<String> in,
			PrintStream out) {
		Set<Operation> operations = spec.getOperations();

		this.analysis = new IndigoAnalyzer(spec, resolutionPolicy != null);
		this.rootContext = AnalysisContext.getNewContext(operations, resolutionPolicy, PredicateFactory.getFactory());
		this.currentContext = rootContext;

		this.idempotenceTestQueue = Queues.newLinkedBlockingQueue();
		this.opposingTestQueue = Queues.newLinkedBlockingQueue();
		this.unresolvedPairwiseConflicts = Queues.newLinkedBlockingQueue();
		this.toFixQueue = Queues.newLinkedBlockingQueue();
		// this.resolutionPolicy = resolutionPolicy;
		currentContext = rootContext.childContext(false);

		operations.forEach(op -> {
			idempotenceTestQueue.add(new SingleOperationTest(op.opName()));
			trackedOperations.add(op.opName());
		});

		this.in = in;
		this.out = out;

		Set<Set<String>> repeatedPairs = Sets.newHashSet();
		Set<Operation> createdOps = Sets.newHashSet();

		// Solve opposing post-conditions.
		Sets.cartesianProduct(operations, operations).stream().forEach(opsPair -> {
			Operation firstOp = opsPair.get(0);
			Operation secondOp = opsPair.get(1);
			OperationPairTest opPair = new OperationPairTest(firstOp.opName(), secondOp.opName());
			if (repeatedPairs.contains(opPair)) {
			} else if (!firstOp.opName().equals(secondOp.opName())) {
				repeatedPairs.add(opPair.asSet());
				if (resolutionPolicy != null) {
					analysis.checkOpposing(opPair, currentContext);
					if (opPair.isOpposing()) {
						List<Operation> modifiedOps = currentContext.solveOpposing(opPair);
						// CREATES NEW OPERATIONS INSTEAD OF MODIFYING THE
						// EXISTING.
						currentContext = currentContext.childContext(modifiedOps, false);
						if (!modifiedOps.isEmpty()) {
							modifiedOps.forEach(op -> {
								trackedOperations.add(op.opName());
								idempotenceTestQueue.add(new SingleOperationTest(op.opName()));
							});
							createdOps.addAll(modifiedOps);
						}
					}
				}
			}
		});
		// TODO: Operations with different predicate values must have different
		// names.
		createdOps.addAll(operations);
		Set<OperationTest> repeated = Sets.newHashSet();
		Sets.cartesianProduct(createdOps, createdOps).stream().forEach(opsPair -> {
			Operation firstOp = opsPair.get(0);
			Operation secondOp = opsPair.get(1);
			OperationTest operationPair = new OperationPairTest(firstOp.opName(), secondOp.opName());
			if (!repeated.contains(operationPair)) {
				this.unresolvedPairwiseConflicts.add(operationPair);
				repeated.add(operationPair);
			}
		});
	}
}

class Text {
	static final String NEW_LINE = System.getProperty("line.separator");
	public static final String FIX_PAIR_SOLUTIONS_MSG = "SOLUTIONS FOR CONFLICT:";
	public static final String FIX_PAIR_MSG = "GOING TO ANALYSE POSSIBLE SOLUTIONS FOR CONFLICTING PAIR: %s.";
	public static final String TO_FIX_MSG = "THERE ARE %d CONFLICTS TO FIX. DO YOU WANT TO FIX THEM INTERACTIVELY?";
	public static final String PRESS_KEY = "PRESS ANY KEY TO CONTINUE";
	// static final String NEW_LINE = System.getProperty("line.separator");
	static final String NEW_STEP = "STEP %s CONFLICTS:";
	static final String IDEMPOTENCE_TEST_MSG = "CHECKING IDEMPOTENCE";
	static final String OPPOSING_TEST_MSG = "CHECKING OPPOSING POST-CONDITIONS";
	static final String CONFLICTS_TEST_MSG = "CHECKING CONFLICTS";
	static final String STOP_QUESTION = "DO YOU WANT CONTINUE THE ANALYSIS?";
	static final String FINISH_MSG = "ANALYSYS STOPPED AFTER %d STEPS";
	public static final String DUMP_CONTEXT = "CURRENT OPERATIONS EFFECTS";
	static final Set<String> yesOrNo = ImmutableSet.of("yes", "y", "no", "n");

	static String operationTestToString(OperationTest op) {
		return String.format("; %s : %s", op.asSet(), op.isOK() ? "[OK]" : op.getConflicts());
	}

	public static ANSWER parseYesOrNo(String input) {
		return (input.equals("yes") || input.equals("y")) ? ANSWER.YES : ANSWER.NO;
	}

	static String operationEffectsToString(String opName, Collection<PredicateAssignment> effects) {
		return String.format("; %s : %s", opName, effects);
	}

	enum ANSWER {
		YES, NO
	};
}
