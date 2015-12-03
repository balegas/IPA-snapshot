package indigo;

import indigo.Parser.Expression;
import indigo.impl.javaclass.JavaClassSpecification;
import indigo.impl.json.JSONSpecification;
import indigo.interfaces.Clause;
import indigo.interfaces.Operation;
import indigo.interfaces.PredicateAssignment;
import indigo.interfaces.PredicateType;
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
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import z3.Z3;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class IndigoAnalyzer {

	private final Logger analysisLog = Logger.getLogger(IndigoAnalyzer.class.getName());
	private final Collection<OperationConflicts> analysisResults = Sets.newHashSet();
	private final ProgramSpecification spec;
	private final Map<Operation, Collection<PredicateAssignment>> opEffects;

	private final boolean solveOpposing;
	private final boolean z3Show = true;
	private final Map<PredicateAssignment, Set<Clause>> predicate2Invariants;

	private IndigoAnalyzer(ProgramSpecification spec, boolean solveOpposing) {
		this.spec = spec;
		this.opEffects = Maps.newHashMap();
		this.solveOpposing = solveOpposing;

		Set<Operation> operations = spec.getOperations();
		Collection<PredicateAssignment> effects = spec.getAllOperationEffects();
		this.predicate2Invariants = spec.collectInvariantsForPredicate();

		operations
		.forEach(op -> {
			opEffects.put(
					op,
					effects.stream().filter(i -> op.opName().equals(i.getOperationName()))
					.collect(Collectors.toList()));
		});
	}

	private boolean idempotent(Operation op, LogicExpression inv) {
		Z3 z3 = new Z3(z3Show);
		analysisLog.fine("; Testing Idempotence for {" + op + "}\n");

		Set<Expression> assertions = Sets.newHashSet();

		assertions.add(inv.expression());

		// Collect operation numeric effects over the invariant, applied once
		LogicExpression wpc = inv.copyOf();
		long numerics = opEffects.get(op).stream().filter(ei -> {
			PredicateAssignment e = ei.copyOf();
			e.applyEffectOnLogicExpression(wpc, 1);
			return e.getType().equals(PredicateType.numeric);
		}).count();

		if (numerics > 0) {
			assertions.add(wpc.expression());

			// Collect operation numeric effects over the invariant, applied
			// twice
			LogicExpression j = inv.copyOf();

			for (PredicateAssignment ei : opEffects.get(op)) {
				PredicateAssignment e = ei.copyOf();
				e.applyEffectOnLogicExpression(j, 1);
				e.applyEffectOnLogicExpression(j, 1);
			}
			z3.Assert(assertions);
			z3.Assert(j.expression(), false);
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

	private boolean notSatisfies(final Collection<Operation> ops, LogicExpression invExpr) {

		Set<Expression> assertions = Sets.newHashSet();

		assertions.add(invExpr.expression());

		// Collect operation effects over the invariant, applied separately
		for (Operation op : ops) {
			LogicExpression i = invExpr.copyOf();
			for (PredicateAssignment ei : opEffects.get(op)) {
				PredicateAssignment e = ei.copyOf();
				e.applyEffectOnLogicExpression(i, 1);
			}
			assertions.add(i.expression());
		}

		// Collect operation effects over the invariant, applied together
		LogicExpression j = invExpr.copyOf();
		for (Operation op : ops) {
			for (PredicateAssignment ei : opEffects.get(op)) {
				PredicateAssignment e = ei.copyOf();
				e.applyEffectOnLogicExpression(j, 1);
			}
		}

		Z3 z3 = new Z3(z3Show);
		z3.Assert(assertions);
		z3.Assert(j.expression(), false);
		boolean res = z3.Check(z3Show);
		z3.Dispose();
		return res;
	}

	private void checkOpposing(final OperationConflicts ops) {
		analysisLog.fine("; Checking: contraditory post-conditions... ");
		Z3 z3 = new Z3(z3Show);

		ops.asSet().forEach(op -> {
			opEffects.get(op).forEach(e -> {
				if (e.getType().equals(PredicateType.bool)) {
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

	private void checkSelfConflicting(OperationConflicts op, LogicExpression inv) {
		List<Operation> opList = new ArrayList<>();
		opList.add(op.firstOperation);
		opList.add(op.firstOperation);
		if (notSatisfies(opList, inv)) {
			op.setSelfConflicting();
		}
	}

	private void checkNonIdempotent(OperationConflicts op, LogicExpression inv) {
		if (!idempotent(op.firstOperation, inv)) {
			op.setNonIdempotent();
		}
	}

	private void checkConflicting(OperationConflicts ops, LogicExpression inv) {
		analysisLog.fine("; Checking: Negated Invariant satisfiability...");
		boolean satNotI = notSatisfies(ops.asSet(), inv.copyOf());

		analysisLog.fine("; Negated Invariant is: " + (satNotI ? "SAT" : "UnSAT"));
		if (satNotI) {
			ops.setConflicting();
			analysisLog.fine("; Operations " + ops + " are conflicting...");
		} else {
			analysisLog.fine("; Operations " + ops + " are safe together...");
		}
	}

	private Clause invariantFor(Collection<Operation> ops) {
		Clause res;
		Set<Clause> ss = null;
		for (Operation op : ops) {
			Set<Clause> si = Sets.newHashSet();
			opEffects.get(op).forEach(e -> {
				si.addAll(predicate2Invariants.get(e));
			});
			ss = Sets.intersection(ss != null ? ss : si, si);
		}

		if (ss.isEmpty()) {
			res = spec.newTrueClause();
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
		Set<OperationConflicts> results = Sets.newTreeSet();
		Sets.cartesianProduct(operations, operations).forEach(ops -> {
			/*
			 * boolean cond1 = ops.get(0).opName().equals("beginTournament") &&
			 * ops.get(1).opName().equals("enroll");
			 *
			 * boolean cond2 = ops.get(0).opName().equals("beginTournament") &&
			 * ops.get(1).opName().equals("enroll"); if (cond1 || cond2 ) {
			 */

			OperationConflicts opPair = new OperationConflicts(ops.get(0), ops.get(1));
			// TODO: Before, we were making a single test, now we are
			// testing all properties.
			// Not sure the state is cleared between checks!
			if (!opPair.isSingleOp()) {
				checkOpposing(opPair);
				checkConflicting(opPair, invariantFor(opPair.asSet()).toLogicExpression());
			} else {
				// TODO: It appears that both checks require
				// applying the effects of the operation twice, so,
				// why
				// dont we simply keep the pair of ops instead of
				// checking if op1 and op2 are equal and then adding
				// extra logic to distinguish the case
				checkSelfConflicting(opPair, invariantFor(opPair.asSet()).toLogicExpression());
				checkNonIdempotent(opPair, invariantFor(opPair.asSet()).toLogicExpression());
				// TODO: are there any pair of operations that
				// might not be idempotent? but idempotent
				// alone?
			}
			results.add(opPair);
			// }
		});
		analysisLog.info("CONFLICT ANALYSIS RESULTS");
		for (OperationConflicts op : results) {
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

	public static Collection<OperationConflicts> analyse(ProgramSpecification spec, boolean solveOpposing) {
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
