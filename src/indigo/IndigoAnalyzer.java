package indigo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import indigo.Parser.Expression;
import indigo.impl.javaclass.JavaClassSpecification;
import indigo.impl.json.JSONConstant;
import indigo.impl.json.JSONSpecification;
import indigo.interfaces.Invariant;
import indigo.interfaces.Operation;
import indigo.interfaces.PREDICATE_TYPE;
import indigo.interfaces.PredicateAssignment;
import indigo.interfaces.Value;
import indigo.invariants.LogicExpression;
import z3.Z3;

public class IndigoAnalyzer {

	private final Logger analysisLog = Logger.getLogger(IndigoAnalyzer.class.getName());
	private final Collection<OperationTest> analysisResults = Sets.newHashSet();
	private final ProgramSpecification spec;
	private final PredicateFactory factory;
	// private final Map<Operation, Collection<PredicateAssignment>> opEffects;

	private final boolean solveOpposing;
	private final boolean z3Show = true;
	private final boolean z3ShowFine = true;

	private final Map<PredicateAssignment, Set<Invariant>> predicate2Invariants;

	public IndigoAnalyzer(ProgramSpecification spec, boolean solveOpposing) {
		this.spec = spec;
		this.factory = PredicateFactory.getFactory();
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

	// This check verifies if the operation in idempotent when applied to any
	// invariant. If my return false negatives.
	private boolean idempotent(SingleOperationTest op, Invariant invariant, AnalysisContext context) {
		Z3 z3 = new Z3(z3Show);
		analysisLog.fine("; Testing Idempotence for {" + op + "}\n");

		Set<Expression> assertions = Sets.newHashSet();

		assertions.add(invariant.toLogicExpression().expression());

		// Collect operation numeric effects over the invariant, applied once
		LogicExpression wpc = invariant.toLogicExpression();
		long numerics = context.getOperationEffects(op.getOpName(), false).stream().filter(ei -> {
			PredicateAssignment e = ei.copyOf();
			wpc.applyEffect(e, 1);
			return e.isType(PREDICATE_TYPE.numeric);
		}).count();

		if (numerics > 0) {
			assertions.add(wpc.expression());

			// Collect operation numeric effects over the invariant, applied
			// twice
			LogicExpression invariantExp = invariant.toLogicExpression();

			for (PredicateAssignment ei : context.getOperationEffects(op.getOpName(), false)) {
				PredicateAssignment e = ei.copyOf();
				invariantExp.applyEffect(e, 1);
				invariantExp.applyEffect(e, 1);
			}
			z3.Assert(assertions);
			z3.Assert(invariantExp.expression(), false);
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

	// private List<PredicateAssignment> notSatisfies(Collection<String>
	// opNames, LogicExpression invExpr,
	// AnalysisContext context) {
	//
	// Set<Expression> assertions = Sets.newLinkedHashSet();
	//
	// // assertions.add(invExpr.expression());
	//
	// // Collect operation effects over the invariant, applied separately
	// for (String op : opNames) {
	// LogicExpression invariant = invExpr.copyOf();
	// for (PredicateAssignment ei : context.getOperationEffects(op, true)) {
	// PredicateAssignment e = ei.copyOf();
	// // add effects of each operation individually.
	// assertions.add(e.copyOf().getExpression());
	// invariant.applyEffect(e, 1);
	// }
	// // add wpc.
	// assertions.add(invariant.expression());
	// }
	//
	// // Negated invariant.
	// LogicExpression negInvariant = invExpr.copyOf();
	// // for (String op : opNames) {
	// // for (PredicateAssignment ei : context.getOperationEffects(op, true))
	// // {
	// // PredicateAssignment e = ei.copyOf();
	// // negInvariant.applyEffect/* OnLogicExpression */(e, 1);
	// // }
	// // }
	//
	// Z3 z3 = new Z3(z3Show);
	// z3.Assert(assertions);
	// z3.Assert(negInvariant.expression(), false);
	// boolean res = z3.Check(z3Show);
	// z3.Dispose();
	// if (res) {
	// List<PredicateAssignment> model = z3.getModel();
	// return model;
	// } else {
	// return ImmutableList.of();
	// }
	// }

	private List<PredicateAssignment> notSatisfies(Collection<String> opNames, Invariant invariant,
			AnalysisContext context) {

		Set<Expression> assertions = Sets.newHashSet();

		assertions.add(invariant.toLogicExpression().expression());

		boolean wpcOK = checkWPC(opNames, invariant, context);

		if (!wpcOK) {
			analysisLog.finest("; Weakest pre-condition test failed");
			return null;
		}

		// Collect operation effects over the invariant, applied separately
		for (String op : opNames) {
			LogicExpression invExp0 = invariant.toLogicExpression();
			for (PredicateAssignment ei : context.getOperationEffects(op, true)) {
				PredicateAssignment e = ei.copyOf();
				invExp0.applyEffect(e, 1);
			}
			assertions.add(invExp0.expression());
		}

		// Collect operation effects over the invariant, applied together
		LogicExpression invExp1 = invariant.toLogicExpression();
		for (String op : opNames) {
			for (PredicateAssignment ei : context.getOperationEffects(op, true)) {
				// PredicateAssignment e = ei.copyOf();
				invExp1.applyEffect/* OnLogicExpression */(ei, 1);
			}
		}

		Z3 z3 = new Z3(z3Show);
		z3.Assert(assertions);
		// test invariant negation;
		z3.Assert(invExp1.expression(), false);
		boolean res = z3.Check(z3Show);
		z3.Dispose();
		if (res) {
			List<PredicateAssignment> model = z3.getModel();
			return model;
		} else {
			return ImmutableList.of();
		}
	}

	private boolean checkWPC(Collection<String> opNames, Invariant invariant, AnalysisContext context) {
		if (opNames.contains("doAA_trueB_false")) {
			System.out.println("here");
		}
		analysisLog.fine("; Analysing weakest pre-conditions validity for operations: " + opNames);
		boolean result = true;
		LinkedList<LogicExpression> wpc = Lists.newLinkedList();

		for (String op : opNames) {
			LogicExpression modifiedInv = invariant.toLogicExpression();
			for (PredicateAssignment ei : context.getOperationEffects(op, true)) {
				modifiedInv.applyEffect(ei/* .copyOf() */, 1);
			}
			result &= checkAssertionsValid(ImmutableList.of(modifiedInv));
			if (analysisLog.isLoggable(Level.FINE)) {
				if (result) {
					analysisLog.fine("; Operation " + op + " is valid");
				} else {
					analysisLog.fine("; Operation " + op + " does not maintain the invariant.");
				}

			}
			wpc.add(modifiedInv);
		}

		boolean resultWPC = true;
		checkAssertionsValid(wpc);
		if (resultWPC) {
			analysisLog.fine("; Both operations can be executed together.");
		} else {
			analysisLog.fine("; There is no initial state that is compatible with both operations.");
		}

		return result & resultWPC;
	}

	private boolean checkAssertionsValid(List<LogicExpression> expressionList) {
		Z3 z3 = new Z3(z3Show);
		expressionList.forEach(exp -> z3.Assert(exp.expression()));
		boolean res = z3.Check(z3ShowFine);
		z3.Dispose();
		return res;
	}

	protected void checkOpposing(OperationPairTest ops, AnalysisContext context) {
		analysisLog.fine("; Contraditory post-conditions for " + ops + " starts.");
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
		analysisLog.fine("; Contraditory post-conditions for " + ops + " ends.");
	}

	private void checkSelfConflicting(SingleOperationTest op, AnalysisContext context) {
		analysisLog.fine("; Self Conflicting test for " + op + " starts.");
		List<String> opList = new ArrayList<>();
		opList.add(op.getOpName());
		opList.add(op.getOpName());
		List<PredicateAssignment> model = notSatisfies(opList, invariantFor(op.getOpName(), context), context);
		if (model == null) {
			op.setInvalidWPC();
		} else if (!model.isEmpty()) {
			op.setSelfConflicting();
			op.addCounterExample(model, context);
		}
		analysisLog.fine("; Self Conflicting test for " + op + " ends.");
	}

	protected void checkNonIdempotent(SingleOperationTest op, AnalysisContext context) {
		analysisLog.fine("; NonIdempotent test for " + op + " starts.");
		if (!idempotent(op, invariantFor(op.getOpName(), context), context)) {
			op.setNonIdempotent();
		}
		analysisLog.fine("; NonIdempotent test for " + op + " ends.");
	}

	protected void checkConflicting(OperationTest ops, AnalysisContext context) {
		analysisLog.fine("; Negated Invariant satisfiability test for " + ops + " starts.");
		List<PredicateAssignment> model = notSatisfies(ops.asSet(), invariantFor(ops.asSet(), context), context);
		if (model == null) {
			ops.setInvalidWPC();
			return;
		} else {
			analysisLog.fine("; Negated Invariant is: " + (!model.isEmpty() ? "SAT" : "UnSAT"));
		}
		if (!model.isEmpty()) {
			ops.setConflicting();
			ops.addCounterExample(model, context);
			analysisLog.info("; Operations " + ops + " are conflicting...");
		} else {
			analysisLog.info("; Operations " + ops + " are safe together...");
		}
		analysisLog.fine("; Negated Invariant satisfiability test for " + ops + " ends.");
	}

	private Invariant invariantFor(String op, AnalysisContext context) {
		return invariantFor(ImmutableSet.of(op), context);
	}

	private Invariant invariantFor(Collection<String> ops, AnalysisContext context) {
		Invariant res;
		Set<Invariant> ss = null;
		for (String op : ops) {
			Set<Invariant> si = Sets.newHashSet();
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
						return new GenericInvariant(mergeAcc.mergeClause(next));
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
				spec.getDefaultConflictResolutionPolicy(), PredicateFactory.getFactory());

		while (opsToProcess.size() > 0) {
			Set<Operation> loopGeneratedOps = Sets.newHashSet();
			AnalysisContext currentContext = rootContext.childContext(false);
			opsToProcess.remove().forEach(ops -> {
				String firstOp = ops.get(0).opName();
				String secondOp = ops.get(1).opName();
				analysisLog.info("Analyzing pair: [" + firstOp + " , " + secondOp + "];");
				if (firstOp.equals("doA") && secondOp.equals("doNotBNotA-A")) {
					System.out.println("here");
				}

				if (firstOp.equals(secondOp)) {
					SingleOperationTest op = new SingleOperationTest(firstOp);
					// TODO: It appears that both checks require
					// applying the effects of the operation twice, so,
					// why
					// dont we simply keep the pair of ops instead of
					// checking if op1 and op2 are equal and then adding
					// extra logic to distinguish the case
					checkSelfConflicting(op, currentContext.childContext(false));
					checkNonIdempotent(op, currentContext.childContext(false));
					// TODO: Should we do nonIdempotenceCheck for pairs of
					// different operations? e.g. when two different
					// operations have the same effect.
					results.add(op);

				} else {
					OperationPairTest opPair = new OperationPairTest(firstOp, secondOp);
					AnalysisContext innerContext = currentContext.childContext(false);
					if (solveOpposing) {
						// TODO: Should check opposing before solving.
						List<Operation> newOps = innerContext.solveOpposing(opPair);
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
					}
					checkOpposing(opPair, innerContext);
					checkConflicting(opPair, innerContext);
					results.add(opPair);
				}
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
				predicate2Invariants.putAll(spec.invariantsAffectedPerPredicateAssignemnt());
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

	protected Collection<List<Operation>> solveConflict(OperationTest operationTest, AnalysisContext context) {
		try {

			Set<PredicateAssignment> explorationSeed = Sets.newHashSet();
			operationTest.asSet().forEach(op -> explorationSeed.addAll(context.getOperationEffects(op, true)));

			Set<Set<PredicateAssignment>> setsPredsForNewOps = powerSet(explorationSeed).stream().map(set -> {
				return negatedEffects(set);
			}).collect(Collectors.toSet());

			List<List<Operation>> allTestPairs = Lists.newLinkedList();
			List<OperationPairTest> successfulPairs = Lists.newLinkedList();
			Collection<Collection<PredicateAssignment>> distinctOps = Lists.newLinkedList();
			for (String opName : operationTest.asSet()) {
				for (Set<PredicateAssignment> predsForNewOps : setsPredsForNewOps) {
					String newOpName = opName;
					Set<PredicateAssignment> predsForNewOp = Sets.newHashSet();

					predsForNewOp.addAll(context.getOperationEffects(opName, true));
					for (PredicateAssignment predForNewOps : predsForNewOps) {
						if (!predsForNewOp.contains(predForNewOps)) {
							predsForNewOp.add(predForNewOps);
							newOpName += predForNewOps.getPredicateName() + "_" + predForNewOps.getAssignedValue();
						}
					}

					// Check same predicate set and value
					// TODO: predicate assignment equals does not check values.
					if (!strictContains(predsForNewOp, distinctOps)) {
						distinctOps.add(predsForNewOp);
						GenericOperation newOp = new GenericOperation(newOpName, predsForNewOp);
						List<String> otherOps = Lists.newLinkedList(operationTest.asSet());
						otherOps.remove(opName);
						for (String otherOpName : otherOps) {
							GenericOperation otherOp = new GenericOperation(otherOpName,
									context.getOperationEffects(otherOpName, true));
							allTestPairs.add(ImmutableList.of(newOp, otherOp));
							analysisLog.fine("Added operation with effect set: " + predsForNewOp);
						}
					} else {
						analysisLog.fine("Operation with effect set: " + predsForNewOp + " already exists");
					}

				}
			}

			List<String> results = Lists.newLinkedList();
			for (List<Operation> l : allTestPairs) {
				Operation opA = l.get(0);
				Operation opB = l.get(1);
				analysisLog.fine("TEST " + opA + " " + opB);
				OperationTest result = testPair(opA, opB,
						context.childContext(ImmutableSet.of(l.get(0), l.get(1)), true));
				results.add(result + "");
				analysisLog.fine("TEST " + opA + " " + opB + " END");
			}

			analysisLog.info("Initial seed to generate operations " + explorationSeed);
			analysisLog.info("New effects to test " + setsPredsForNewOps);
			results.forEach(x -> analysisLog.info(x));

			// TODO: return only successful transformations.
			return allTestPairs;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ImmutableSet.of();
	}

	private static boolean strictContains(Set<PredicateAssignment> predicateSet,
			Collection<Collection<PredicateAssignment>> predicateSetSet) {
		for (Collection<PredicateAssignment> existingOp : predicateSetSet) {
			if (existingOp.equals(predicateSet)) {
				if (allEqualValues(existingOp, predicateSet)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean allEqualValues(Collection<PredicateAssignment> op1, Collection<PredicateAssignment> op2) {
		boolean allEqual = true;
		for (PredicateAssignment pred : op2) {
			for (PredicateAssignment existingOpPred : op1) {
				if (pred.getOperationName().equals(existingOpPred.getOperationName())) {
					allEqual &= pred.getAssignedValue().equals(existingOpPred.getAssignedValue());
				}
			}
		}
		return allEqual;
	}

	private OperationTest testPair(Operation op1, Operation op2, AnalysisContext context) {
		OperationPairTest test = new OperationPairTest(op1.opName(), op2.opName());
		checkOpposing(test, context);
		// context.solveOpposing(test);
		checkConflicting(test, context);
		Set<PredicateAssignment> counterExample = test.getCounterExample();
		if (counterExample != null && !counterExample.isEmpty()) {
			test.addCounterExample(counterExample, context);
		}
		return test;
	}

	private Set<PredicateAssignment> negatedEffects(Set<PredicateAssignment> set) {
		Set<PredicateAssignment> negatedEffects = Sets.newHashSet();
		for (PredicateAssignment effect : set) {
			Value value = effect.getAssignedValue();
			if (value.toString().equals("true")) {
				value = new JSONConstant(PREDICATE_TYPE.bool, "false");
			} else if (value.toString().equals("false")) {
				value = new JSONConstant(PREDICATE_TYPE.bool, "true");
			} else {
				System.out.println("NOT EXPECTED TYPE");
				System.exit(0);
			}
			PredicateAssignment modEffect = factory.newPredicateAssignmentFrom(effect, value);
			negatedEffects.add(modEffect);
		}
		return negatedEffects;
	}

	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
		Set<Set<T>> sets = new HashSet<Set<T>>();
		if (originalSet.isEmpty()) {
			sets.add(new HashSet<T>());
			return sets;
		}
		List<T> list = new ArrayList<T>(originalSet);
		T head = list.get(0);
		Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
		for (Set<T> set : powerSet(rest)) {
			Set<T> newSet = new HashSet<T>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}
		return sets;
	}

}
