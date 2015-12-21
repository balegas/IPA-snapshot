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
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import z3.Z3;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class IndigoAnalyzer {

	private final Logger analysisLog = Logger.getLogger(IndigoAnalyzer.class.getName());
	private final Collection<OperationTest> analysisResults = Sets.newHashSet();
	private final ProgramSpecification spec;
	// private final Map<Operation, Collection<PredicateAssignment>> opEffects;

	private final boolean solveOpposing;
	private final boolean z3Show = true;

	private final Map<PredicateAssignment, Set<Invariant>> predicate2Invariants;

	private IndigoAnalyzer(ProgramSpecification spec, boolean solveOpposing) {
		this.spec = spec;
		// this.opEffects = Maps.newHashMap();
		this.solveOpposing = solveOpposing;

		// Set<Operation> operations = spec.getOperations();
		// Map<String, Collection<PredicateAssignment>> ops =
		// spec.getAllOperationEffects();
		// opNames = ops.keySet();
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
		long numerics = context.getOperationEffects(op.getOpName(), false).stream().filter(ei -> {
			PredicateAssignment e = ei.copyOf();
			wpc.applyEffect(e, 1);
			return e.isType(PREDICATE_TYPE.numeric);
		}).count();

		if (numerics > 0) {
			assertions.add(wpc.expression());

			// Collect operation numeric effects over the invariant, applied
			// twice
			LogicExpression invariant = inv.copyOf();

			for (PredicateAssignment ei : context.getOperationEffects(op.getOpName(), false)) {
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
			for (PredicateAssignment ei : context.getOperationEffects(op, true)) {
				PredicateAssignment e = ei.copyOf();
				invariant.applyEffect(e, 1);
			}
			assertions.add(invariant.expression());
		}

		// Collect operation effects over the invariant, applied together
		LogicExpression negInvariant = invExpr.copyOf();
		for (String op : opNames) {
			for (PredicateAssignment ei : context.getOperationEffects(op, true)) {
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

		context.getAllOperationEffects(ops.asSet(), true).forEach(op -> {
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
			if (context.getOperationEffects(op, false) == null) {
				System.out.println("here");
			}
			context.getOperationEffects(op, false).forEach(e -> {
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
		Set<OperationTest> results = Sets.newTreeSet();

		Set<Operation> allGeneratedOps = Sets.newHashSet();
		Set<Operation> operations = spec.getOperations();
		allGeneratedOps.addAll(operations);

		Queue<Set<List<Operation>>> opsToProcess = Lists.newLinkedList();
		opsToProcess.add(Sets.cartesianProduct(operations, operations));

		AnalysisContext rootContext = AnalysisContext.getNewContext(operations,
				spec.getDefaultConflictResolutionPolicy());

		while (opsToProcess.size() > 0) {
			Set<Operation> loopGeneratedOps = Sets.newHashSet();
			AnalysisContext currentContext = rootContext.childContext(false);
			opsToProcess.remove().forEach(ops -> {
				String firstOp = ops.get(0).opName();
				String secondOp = ops.get(1).opName();
				analysisLog.info("Analyzing pair: [" + firstOp + " , " + secondOp + "];");
				if (firstOp.equals(secondOp)) {
					SingleOperationTest op = new SingleOperationTest(firstOp);
					// TODO: It appears that both checks require
					// applying the effects of the operation twice, so,
					// why
					// dont we simply keep the pair of ops instead of
					// checking if op1 and op2 are equal and then adding
					// extra logic to distinguish the case
					checkSelfConflicting(op, invariantFor(op.getOpName(), currentContext).toLogicExpression(),
							currentContext.childContext(false));
					checkNonIdempotent(op, invariantFor(op.getOpName(), currentContext).toLogicExpression(),
							currentContext.childContext(false));
					// TODO: Should we do nonIdempotenceCheck for pairs of
					// different operations? e.g. when two different
					// operations have the same effect.
					results.add(op);

				} else {
					OperationPairTest opPair = new OperationPairTest(firstOp, secondOp);
					AnalysisContext innerContext = currentContext.childContext(false);
					List<Operation> newOps = innerContext.operationsToTest(ImmutableSet.of(firstOp, secondOp),
							solveOpposing);
					if (newOps.size() > 0) {
						opPair.setModified();
						analysisLog.fine("New  operations after conflict resolution:");
						for (Operation op : newOps) {
							analysisLog.fine(op + "");
						}
						newOps.forEach(op -> {
							if (!allGeneratedOps.contains(op)) {
								loopGeneratedOps.add(op);
							} else {
								analysisLog.fine("Operation " + op + " already generated in previous round.");
							}
						});
					}

					checkOpposing(opPair, innerContext);
					// previously it was rootContext on the next line.
					checkConflicting(opPair, invariantFor(opPair.asSet(), innerContext).toLogicExpression(),
							innerContext);
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

			analysisLog.info("OPERATION EFFECTS");
			for (Operation op : allGeneratedOps) {
				analysisLog.info(": " + op);
			}

			allGeneratedOps.addAll(loopGeneratedOps);
			if (loopGeneratedOps.isEmpty()) {
				break;
			} else {
				Set<List<Operation>> nextRound = Sets.newHashSet();
				nextRound.addAll(Sets.cartesianProduct(allGeneratedOps, allGeneratedOps));
				opsToProcess.add(nextRound);
				spec.updateOperations(allGeneratedOps);
				// predicate2Invariants.clear();
				// predicate2Invariants.putAll(spec.invariantsAffectedPerPredicateAssignemnt());
				rootContext = rootContext.childContext(allGeneratedOps, false);
				System.out.println("here");
			}
		}
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
