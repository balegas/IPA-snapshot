package indigo;

import indigo.Parser.Expression;
import indigo.impl.javaclass.JavaClassSpecification;
import indigo.impl.json.JSONSpecification;
import indigo.interfaces.Clause;
import indigo.interfaces.Invariant;
import indigo.interfaces.Operation;
import indigo.interfaces.PREDICATE_TYPE;
import indigo.interfaces.PredicateAssignment;
import indigo.invariants.LogicExpression;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import z3.Z3;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class IndigoAnalyzer {

	private final Logger analysisLog = Logger.getLogger(IndigoAnalyzer.class.getName());
	private final Collection<OperationTest> analysisResults = Sets.newHashSet();
	private final ProgramSpecification spec;
	// private final Map<Operation, Collection<PredicateAssignment>> opEffects;

	private final boolean solveOpposing;
	private final boolean z3Show = true;

	// TODO: note that some operation transformations might affect the
	// predicates of an operation,
	// therefore also affects the invariants affected by it.
	private final Map<PredicateAssignment, Set<Invariant>> predicate2Invariants;

	// private final AnalysisContext rootContext;
	private final Collection<String> opNames;

	private IndigoAnalyzer(ProgramSpecification spec, boolean solveOpposing) {
		this.spec = spec;
		// this.opEffects = Maps.newHashMap();
		this.solveOpposing = solveOpposing;

		// Set<Operation> operations = spec.getOperations();
		Map<String, Collection<PredicateAssignment>> ops = spec.getAllOperationEffects();
		opNames = ops.keySet();
		// this.rootContext = AnalysisContext.getNewContext(ops,
		// spec.getDefaultConflictResolutionPolicy());
		this.predicate2Invariants = spec.invariantsAffectedPerPredicateAssignemnt();

		// operations
		// .forEach(op -> {
		// opEffects.put(
		// op,
		// effects.stream().filter(i ->
		// op.opName().equals(i.getOperationName()))
		// .collect(Collectors.toList()));
		// });
	}

	// TODO: idempotence test does not depend on the invariant.
	// It might impact the invariant or not, but its not the invariant that
	// makes the operation idempotent or not.
	private boolean idempotent(SingleOperationTest op, LogicExpression inv, AnalysisContext context) {
		Z3 z3 = new Z3(z3Show);
		analysisLog.fine("; Testing Idempotence for {" + op + "}\n");

		Set<Expression> assertions = Sets.newHashSet();

		assertions.add(inv.expression());

		// Collect operation numeric effects over the invariant, applied once
		LogicExpression wpc = inv.copyOf();
		long numerics = context.getOperationEffects(op.getOpName()).stream().filter(ei -> {
			PredicateAssignment e = ei.copyOf();
			wpc.applyEffect(e, 1);
			return e.isType(PREDICATE_TYPE.numeric);
		}).count();

		if (numerics > 0) {
			assertions.add(wpc.expression());

			// Collect operation numeric effects over the invariant, applied
			// twice
			LogicExpression invariant = inv.copyOf();

			for (PredicateAssignment ei : context.getOperationEffects(op.getOpName())) {
				PredicateAssignment e = ei.copyOf();
				invariant.applyEffect(e, 1);
				invariant.applyEffect(e, 1);
			}
			z3.Assert(assertions);
			z3.Assert(invariant.expression(), false);
			boolean sat = z3.Check(z3Show);

			z3.Dispose();

			if (sat) {
				analysisLog.fine("; {" + op + "} is NOT idempotent!\n\n");
				return false;
			}
		}
		analysisLog.fine("; {" + op + "} is idempotent!\n\n");
		return true;
	}

	private boolean notSatisfies(Collection<String> opNames, LogicExpression invExpr, AnalysisContext context) {

		Set<Expression> assertions = Sets.newHashSet();

		assertions.add(invExpr.expression());

		// Collect operation effects over the invariant, applied separately
		for (String op : opNames) {
			LogicExpression invariant = invExpr.copyOf();
			for (PredicateAssignment ei : context.getOperationEffects(op)) {
				PredicateAssignment e = ei.copyOf();
				invariant.applyEffect(e, 1);
			}
			assertions.add(invariant.expression());
		}

		// Collect operation effects over the invariant, applied together
		LogicExpression negInvariant = invExpr.copyOf();
		for (String op : opNames) {
			for (PredicateAssignment ei : context.getOperationEffects(op)) {
				PredicateAssignment e = ei.copyOf();
				negInvariant.applyEffect/* OnLogicExpression */(e, 1);
			}
		}

		Z3 z3 = new Z3(z3Show);
		z3.Assert(assertions);
		z3.Assert(negInvariant.expression(), false);
		boolean res = z3.Check(z3Show);
		z3.Dispose();
		return res;
	}

	private void checkOpposing(OperationPairTest ops, AnalysisContext context) {
		analysisLog.fine("; Checking: contraditory post-conditions... ");
		Z3 z3 = new Z3(z3Show);

		context.getAllOperationEffects(ops.asSet()).forEach(op -> {
			op.getSecond().forEach(e -> {
				if (e.isType(PREDICATE_TYPE.bool)) {
					System.out.println("Assert " + e.getExpression());
					z3.Assert(e.getExpression());
				}
			});
		});

		boolean sat = z3.Check(z3Show);
		z3.Dispose();
		if (!sat) {
			analysisLog.fine("; Operations " + ops + " conflict... [contraditory effects/recommended CRDT resolution]");
		} else {
			analysisLog.fine("; Passed...");

		}
		if (!sat) {
			ops.setOpposing();
		}
	}

	private void checkSelfConflicting(SingleOperationTest op, LogicExpression inv, AnalysisContext context) {
		List<String> opList = new ArrayList<>();
		opList.add(op.getOpName());
		opList.add(op.getOpName());
		if (notSatisfies(opList, inv, context)) {
			op.setSelfConflicting();
		}
	}

	private void checkNonIdempotent(SingleOperationTest op, LogicExpression inv, AnalysisContext context) {
		if (!idempotent(op, inv, context)) {
			op.setNonIdempotent();
		}
	}

	private void checkConflicting(OperationPairTest ops, LogicExpression inv, AnalysisContext context) {
		analysisLog.fine("; Checking: Negated Invariant satisfiability...");
		boolean satNotI = notSatisfies(ops.asSet(), inv.copyOf(), context);

		analysisLog.fine("; Negated Invariant is: " + (satNotI ? "SAT" : "UnSAT"));
		if (satNotI) {
			ops.setConflicting();
			analysisLog.fine("; Operations " + ops + " are conflicting...");
		} else {
			analysisLog.fine("; Operations " + ops + " are safe together...");
		}
	}

	private Clause invariantFor(String op, AnalysisContext context) {
		return invariantFor(ImmutableSet.of(op), context);
	}

	private Clause invariantFor(Collection<String> ops, AnalysisContext context) {
		Clause res;
		Set<Clause> ss = null;
		for (String op : ops) {
			Set<Clause> si = Sets.newHashSet();
			context.getOperationEffects(op).forEach(e -> {
				si.addAll(predicate2Invariants.get(e));
			});
			ss = Sets.intersection(ss != null ? ss : si, si);
		}

		if (ss.isEmpty()) {
			res = spec.newEmptyInv();
		} else {
			res = ss.stream().reduce(null, (mergeAcc, next) -> {
				if (mergeAcc == null) {
					return next;
				} else {
					try {
						return mergeAcc.mergeClause(next);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return mergeAcc;
				}
			});
		}

		analysisLog.fine("\n; -----------------------------------------------------------------------");
		analysisLog.fine("Operations:");
		analysisLog.fine("; " + ops);
		analysisLog.fine("; Simplified Invariant for " + ops + " --> " + res);
		return res;
	}

	@SuppressWarnings("unchecked")
	private void doIt() throws Exception {
		Set<Operation> operations = spec.getOperations();
		Set<OperationTest> results = Sets.newTreeSet();

		AnalysisContext context = AnalysisContext.getNewContext(spec.getAllOperationEffects(),
				spec.getDefaultConflictResolutionPolicy());

		Sets.cartesianProduct(operations, operations).forEach(ops -> {
			String firstOp = ops.get(0).opName();
			String secondOp = ops.get(1).opName();

			// if (firstOp.equals("beginTournament") &&
			// secondOp.equals("setLeader")) {

			if (firstOp.equals(secondOp)) {
				SingleOperationTest op = new SingleOperationTest(firstOp);
				// TODO: It appears that both checks require
				// applying the effects of the operation twice, so,
				// why
				// dont we simply keep the pair of ops instead of
				// checking if op1 and op2 are equal and then adding
				// extra logic to distinguish the case
				checkSelfConflicting(op, invariantFor(op.getOpName(), context).toLogicExpression(),
						context.newContextFrom());
				checkNonIdempotent(op, invariantFor(op.getOpName(), context).toLogicExpression(),
						context.newContextFrom());
				// TODO: are there any pair of operations that
				// might not be idempotent? but idempotent
				// alone?
				results.add(op);

			} else {
				OperationPairTest opPair = new OperationPairTest(firstOp, secondOp);
				checkOpposing(opPair, context.newContextFrom());
				checkConflicting(opPair, invariantFor(opPair.asSet(), context).toLogicExpression(),
						context.newContextFrom());
				results.add(opPair);
			}
			// }
		});
		analysisLog.info("CONFLICT ANALYSIS RESULTS");
		for (OperationTest op : results) {
			analysisLog.info(": " + op);
		}
		analysisResults.clear();
		analysisResults.addAll(results);
	}

	public static void main(String[] args) throws Exception {
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
			IndigoAnalyzer.analyse(spec, false);
		}
	}

	public static Collection<OperationTest> analyse(ProgramSpecification spec, boolean solveOpposing) {
		try {
			IndigoAnalyzer analyzer = new IndigoAnalyzer(spec, solveOpposing);
			analyzer.doIt();
			return analyzer.analysisResults;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ImmutableSet.of();
	}
}
