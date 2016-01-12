package indigo;

import indigo.impl.javaclass.GenericConflictResolutionPolicy;
import indigo.impl.javaclass.JavaClassSpecification;
import indigo.impl.json.JSONSpecification;
import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Operation;
import indigo.interfaces.PredicateAssignment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class InteractiveAnalysis {
	private IndigoAnalyzer analysis;
	private ConflictResolutionPolicy resolutionPolicy;
	private AnalysisContext rootContext;
	private AnalysisContext currentContext;
	private Queue<OperationTest> opposingTestQueue;
	private Queue<SingleOperationTest> idempotenceTestQueue;
	private Set<OperationTest> unresolvedPairwiseConflicts;
	private final Set<String> trackedOperations;

	private Iterator<String> in;
	private OutputStream out;
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
			if (args.length > 3 && args[2].equals("-y")) {
				in = new AutoConfirmIterator();
			} else {
				in = new Scanner(System.in);
			}
			InteractiveAnalysis analysis = new InteractiveAnalysis();
			analysis.init(spec, solveConflicts, in, System.out);
			System.out.println("Arguments :" + Text.NEW_LINE + argsDump);
			analysis.dumpContext();
			if (breakOnEachStep) {
				System.out.println("BREAK ON EACH STEP IS ON: TYPE ANY CHARACTER BETWEEN TESTS TO CONTINUE.");
			}
			analysis.start();
		}
	}

	private void dumpContext() throws IOException {
		out.write((Text.DUMP_CONTEXT + Text.NEW_LINE).getBytes());
		for (String op : trackedOperations) {
			Collection<PredicateAssignment> effects = currentContext.getOperationEffects(op, true);
			out.write((Text.operationEffectsToString(op, effects) + Text.NEW_LINE).getBytes());
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
		stepResults.add(String.format(Text.NEW_STEP + Text.NEW_LINE, numberOfSteps));
	}

	private void outputCurrentState() throws IOException {
		StringBuilder outputString = new StringBuilder();
		stepResults.forEach(s -> outputString.append(s));
		out.write(outputString.toString().getBytes());
		stepResults.clear();
	}

	private void outputStart() {
		// TODO Auto-generated method stub

	}

	private void outputEnd() throws IOException {
		StringBuilder outputString = new StringBuilder();
		outputString.append(String.format(Text.FINISH_MSG, numberOfSteps) + Text.NEW_LINE);
		out.write(outputString.toString().getBytes());
		currentContext.getAllOperationEffects(trackedOperations, true);
	}

	private void promptStop() throws IOException {
		StringBuilder outputString = new StringBuilder();
		outputString.append(Text.STOP_QUESTION + Text.NEW_LINE);
		out.write(outputString.toString().getBytes());
		String input = readAnswer();
		if (input.equals("n") || input.equals("no")) {
			stopped = true;
		}
	}

	private String readAnswer() throws IOException {
		String input = "";
		do {
			input = in.next();
		} while (!Text.yesOrNo.contains(input));
		return input;
	}

	private void readAny() {
		in.next();
	}

	private void resolutionLoop() throws IOException {
		breakOnEachStep();
		if (!idempotenceTestQueue.isEmpty()) {
			stepResults.add(Text.IDEMPOTENCE_TEST_MSG + Text.NEW_LINE);
			idempotenceTestQueue.forEach(op -> {
				analysis.checkNonIdempotent(op, currentContext);
				stepResults.add(Text.operationTestToString(op) + Text.NEW_LINE);
			});
		}
		breakOnEachStep();
		if (!unresolvedPairwiseConflicts.isEmpty()) {
			stepResults.add(Text.CONFLICTS_TEST_MSG + Text.NEW_LINE);
			unresolvedPairwiseConflicts.forEach(opPair -> {
			});
		}
		breakOnEachStep();
		if (!opposingTestQueue.isEmpty()) {
			stepResults.add(Text.OPPOSING_TEST_MSG + Text.NEW_LINE);
			opposingTestQueue.forEach(opPair -> {

			});
		}
	}

	private void breakOnEachStep() throws IOException {
		if (breakOnEachStep) {
			outputCurrentState();
			readAny();
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
		this.unresolvedPairwiseConflicts = Sets.newHashSet();
		this.resolutionPolicy = resolutionPolicy;
		currentContext = rootContext.childContext(false);

		operations.forEach(op -> {
			idempotenceTestQueue.add(new SingleOperationTest(op.opName()));
			trackedOperations.add(op.opName());
		});

		this.in = in;
		this.out = out;

		Set<Set<String>> repeatedPairs = Sets.newHashSet();

		Sets.cartesianProduct(operations, operations).stream().forEach(opsPair -> {
			Operation firstOp = opsPair.get(0);
			Operation secondOp = opsPair.get(1);
			Set<String> opPair = ImmutableSet.of(firstOp.opName(), secondOp.opName());
			if (repeatedPairs.contains(opPair)) {
			} else if (!firstOp.opName().equals(secondOp.opName())) {
				repeatedPairs.add(opPair);
				List<Operation> modifiedOps = currentContext.solveOpposing(opPair, resolutionPolicy != null);
				// CREATES NEW OPERATIONS INSTEAD OF MODIFYING THE EXISTING.
				currentContext = currentContext.childContext(modifiedOps, false);
				if (!modifiedOps.isEmpty()) {
					modifiedOps.forEach(op -> {
						trackedOperations.add(op.opName());
						idempotenceTestQueue.add(new SingleOperationTest(op.opName()));
					});

				}
			}
			// this.unresolvedPairwiseConflicts.add(new
			// OperationPairTest(firstOp.opName(), secondOp.opName()));
		});
	}
}

class Text {
	static final String NEW_LINE = System.getProperty("line.separator");
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

	static String operationEffectsToString(String opName, Collection<PredicateAssignment> effects) {
		return String.format("; %s : %s", opName, effects);
	}
}
