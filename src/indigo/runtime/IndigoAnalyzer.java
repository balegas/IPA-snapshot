package indigo.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import indigo.conflicts.test.OperationPairTest;
import indigo.conflicts.test.OperationTest;
import indigo.conflicts.test.SingleOperationTest;
import indigo.generic.ConditionPredicateAssignment;
import indigo.generic.GenericPredicateFactory;
import indigo.impl.javaclass.JavaClassSpecification;
import indigo.impl.json.JSONSpecification;
import indigo.interactive.filters.FilterSubsetAndSameName;
import indigo.interfaces.interactive.TestPairFilter;
import indigo.interfaces.interactive.TestPairPruneFilter;
import indigo.interfaces.logic.Invariant;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.Operation;
import indigo.interfaces.operations.OperationGenerator;
import indigo.invariants.LogicExpression;
import indigo.runtime.Parser.Expression;
import z3.Z3;

public class IndigoAnalyzer {

	private final Logger analysisLog = Logger.getLogger(IndigoAnalyzer.class.getName());
	private final boolean z3Show = true;
	private final boolean z3ShowFine = true;

	private final ProgramSpecification spec;
	private final boolean solveOpposing;

	private final Set<TestPairFilter> toTestFilters;
	private final Set<TestPairPruneFilter> pruneTestsFilters;
	private final Collection<OperationTest> analysisResults = Sets.newHashSet();
	private OperationGenerator testSetGenerator;

	public IndigoAnalyzer(ProgramSpecification spec, boolean solveOpposing) {
		this.spec = spec;
		this.solveOpposing = solveOpposing;
		this.toTestFilters = Sets.newHashSet();
		this.pruneTestsFilters = Sets.newHashSet();
	}

	public IndigoAnalyzer(ProgramSpecification spec, boolean solveOpposing, OperationGenerator testSetGenerator) {
		this(spec, solveOpposing);

		this.testSetGenerator = testSetGenerator;
		pruneTestsFilters.add(new FilterSubsetAndSameName());
		toTestFilters.add(new FilterSubsetAndSameName());

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
		Map<String, LogicExpression> constraints = Maps.newHashMap();
		long numerics = context.getOperationEffects(op.getOpName(), false, false).stream().filter(ei -> {
			PredicateAssignment e = ei.copyOf();
			if (!constraints.containsKey(e.getPredicateName())) {
				ConditionPredicateAssignment constraint = context.getConstraintFor(e.getPredicateName());
				if (constraint != null) {
					constraints.put(e.getPredicateName(), constraint.toLogicExpression());
				}
				// assertions.add(constraint.expression());
				// assertions.add(constraint.variableValue());
			}
			LogicExpression c = constraints.get(e.getPredicateName());
			if (c != null) {
				c.applyEffect(e, 1);
				c.applyEffect(e, 1);
				wpc.applyEffect(e, 1);
			}
			return e.isType(PREDICATE_TYPE.Int);
		}).count();

		if (numerics > 0) {
			assertions.add(wpc.expression());

			// Collect operation numeric effects over the invariant, applied
			// twice
			LogicExpression invariantExp = invariant.toLogicExpression();

			for (LogicExpression e : constraints.values()) {
				z3.Assert(e.expression());
			}

			for (PredicateAssignment ei : context.getOperationEffects(op.getOpName(), false, false)) {
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

	private List<PredicateAssignment> notSatisfies(Collection<String> opNames, Invariant invariant,
			AnalysisContext context) {

		Set<Expression> assertions = Sets.newHashSet();

		assertions.add(invariant.toLogicExpression().expression());

		boolean wpcOK = checkWPC(opNames, context);

		if (!wpcOK) {
			analysisLog.finest("; Weakest pre-condition test failed.");
			return null;
		}

		// Collect operation effects over the invariant, applied separately
		for (String op : opNames) {
			LogicExpression invExp0 = invariant.toLogicExpression();
			Set<PredicateAssignment> effectsSet = Sets.newHashSet();
			effectsSet.addAll(context.getOperationEffects(op, false, true));
			effectsSet.addAll(context.getOperationPreConditions(op, false, true));
			for (PredicateAssignment ei : effectsSet) {
				if (ei.getType().equals(PREDICATE_TYPE.bool)) {
					PredicateAssignment e = ei.copyOf();
					invExp0.applyEffect(e, 1);
				}
			}
			assertions.add(invExp0.expression());
		}

		// Collect operation effects over the invariant, applied together
		LogicExpression invExp1 = invariant.toLogicExpression();
		for (String op : opNames) {
			Set<PredicateAssignment> effectsSet = Sets.newHashSet();
			effectsSet.addAll(context.getOperationEffects(op, false, true));
			effectsSet.addAll(context.getOperationPreConditions(op, false, true));
			for (PredicateAssignment ei : effectsSet) {
				if (ei.getType().equals(PREDICATE_TYPE.bool)) {
					// PredicateAssignment e = ei.copyOf();
					invExp1.applyEffect/* OnLogicExpression */(ei, 1);
				}
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

	private boolean checkWPC(
			Collection<String> opNames/* , Invariant invariant */, AnalysisContext context) {
		analysisLog.fine("; Analysing weakest pre-conditions validity for operations: " + opNames);
		boolean result = true;
		LinkedList<LogicExpression> wpc = Lists.newLinkedList();
		Invariant invariant = spec.invariantFor(opNames, context);
		for (String op : opNames) {
			LogicExpression modifiedInv = invariant.toLogicExpression();
			Set<PredicateAssignment> effectsSet = Sets.newHashSet();
			effectsSet.addAll(context.getOperationEffects(op, false, true));
			effectsSet.addAll(context.getOperationPreConditions(op, false, true));
			for (PredicateAssignment ei : effectsSet) {
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

		boolean resultWPC = checkAssertionsValid(wpc);
		if (resultWPC) {
			analysisLog.fine("; Both operations can be executed together.");
		} else {
			analysisLog.fine("; There is no initial state that is compatible with operations: ");
			opNames.forEach(op -> analysisLog.info("; " + op + " : " + context.getOperationEffects(op, false, true)));
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

	private void checkOpposing(OperationPairTest ops, AnalysisContext context) {
		analysisLog.fine("; Contraditory post-conditions starts." + ops);
		Z3 z3 = new Z3(z3Show);

		context.getAllOperationEffects(ops.asSet(), true, true).forEach(op -> {
			context.getOperationPreConditions(op.getFirst(), true, true).forEach(pre -> {
				if (pre.isType(PREDICATE_TYPE.bool)) {
					analysisLog.fine("Assert " + pre.expression());
					z3.Assert(pre.expression());
				}
			});
			op.getSecond().forEach(e -> {
				if (e.isType(PREDICATE_TYPE.bool)) {
					analysisLog.fine("Assert " + e.expression());
					z3.Assert(e.expression());
				}
			});
		});

		boolean sat = z3.Check(z3Show);
		z3.Dispose();
		if (!sat) {
			ops.setOpposing();
		}
		if (!sat) {
			analysisLog.info("; Operations " + ops + " conflict... [contraditory effects/recommended CRDT resolution]");
			ops.asSet()
					.forEach(op -> analysisLog.info("; " + op + " : " + context.getOperationEffects(op, false, true)));
		} else {
			analysisLog.fine("; Passed...");

		}
		analysisLog.fine("; Contraditory post-conditions ends. " + ops);
	}

	private void checkSelfConflicting(SingleOperationTest op, AnalysisContext context) {
		analysisLog.fine("; Self Conflicting test starts. " + op);
		List<String> opList = new ArrayList<>();
		opList.add(op.getOpName());
		opList.add(op.getOpName());
		List<PredicateAssignment> model = notSatisfies(opList,
				spec.invariantFor(ImmutableSet.of(op.getOpName()), context), context);
		if (model == null) {
			op.setInvalidWPC();
		} else if (!model.isEmpty()) {
			op.setSelfConflicting();
			op.addCounterExample(model, context);
		}
		analysisLog.fine("; Self Conflicting test ends. " + op);
	}

	public void testIdempotence(SingleOperationTest op, AnalysisContext context) {
		analysisLog.fine("; Non idempotent operations test starts. " + op);
		if (!idempotent(op, spec.invariantFor(ImmutableSet.of(op.getOpName()), context), context)) {
			op.setNonIdempotent();
		}
		analysisLog.fine("; Non idempotent operation ends. " + op);
	}

	private void checkConflicting(OperationTest ops, AnalysisContext context) {
		analysisLog.fine("; Negated Invariant satisfiability test start " + ops);
		List<PredicateAssignment> model = notSatisfies(ops.asList(), spec.invariantFor(ops.asSet(), context), context);
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
		analysisLog.fine("; Negated Invariant satisfiability test ends. " + ops);
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
				spec.getDefaultConflictResolutionPolicy(), GenericPredicateFactory.getFactory());

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
					checkSelfConflicting(op, currentContext.childContext(false));
					testIdempotence(op, currentContext.childContext(false));
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

			analysisLog.info("PREDICATE DEPENDENCIES");
			for (Entry<String, Set<PredicateAssignment>> predicate : spec.getDependenciesForPredicate().entrySet()) {
				analysisLog.info(predicate.getKey() + " : " + predicate.getValue());
			}

			allGeneratedOps.addAll(loopGeneratedOps);
			if (loopGeneratedOps.isEmpty()) {
				break;
			} else {
				Set<List<Operation>> nextRound = Sets.newHashSet();
				nextRound.addAll(Sets.cartesianProduct(allGeneratedOps, allGeneratedOps));
				opsToProcess.add(nextRound);
				spec.updateOperations(allGeneratedOps);
				rootContext = rootContext.childContext(allGeneratedOps, false);
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

	public List<Operation> solveConflict(OperationTest operationTest, AnalysisContext context) {
		try {

			List<List<Operation>> allTestPairs = testSetGenerator.generate(operationTest, context);
			List<List<Operation>> successfulPairs = Lists.newLinkedList();
			List<Operation> successfulOps = Lists.newLinkedList();

			List<String> results = Lists.newLinkedList();
			while (allTestPairs.size() > 0) {
				List<Operation> l = allTestPairs.remove(0);

				if (toTestFilters.stream().anyMatch(f -> !f.toTest(l.get(0), successfulOps))) {
					continue;
				}

				String opA = l.get(0).opName();
				String opB = l.get(1).opName();
				analysisLog.fine("TEST " + opA + " " + opB);
				OperationTest result = testPair(new OperationPairTest(opA, opB),
						context.childContext(ImmutableSet.of(l.get(0), l.get(1)), false));
				results.add(result + "");
				if (result.isOK()) {
					analysisLog.finest("Operation " + l.get(0) + " fixes conflict. Reducing test space.");
					successfulPairs.add(l);
					successfulOps.add(l.get(0));

					for (TestPairPruneFilter f : pruneTestsFilters) {
						allTestPairs = f.prunePending(l.get(0), allTestPairs);
					}

				}
				analysisLog.fine("TEST " + opA + " " + opB + " END");
			}

			analysisLog.info("New Operations to test " + allTestPairs);
			results.forEach(x -> analysisLog.info(x));

			return successfulOps;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ImmutableList.of();
	}

	public OperationTest testPair(OperationPairTest test, AnalysisContext context) {
		OperationPairTest testOpposing = new OperationPairTest(test.getFirst(), test.getSecond());

		if (checkWPC(test.asSet(), context)) {
			checkOpposing(testOpposing, context);
			context.solveOpposingByModifying(testOpposing);
			checkConflicting(test, context);
			Set<PredicateAssignment> counterExample = test.getCounterExample();
			if (counterExample != null && !counterExample.isEmpty()) {
				test.addCounterExample(counterExample, context);
			}
		} else {
			test.setInvalidWPC();
		}
		return test;
	}

}
