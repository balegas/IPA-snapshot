package indigo;

import indigo.Parser.Expression;
import indigo.impl.javaclass.JavaClassSpecification;
import indigo.impl.json.JSONSpecification;
import indigo.interfaces.Clause;
import indigo.interfaces.Operation;
import indigo.interfaces.PredicateAssignment;
import indigo.invariants.LogicExpression;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import z3.Z3;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class IndigoAnalyzer {

	Map<Operation, List<PredicateAssignment>> opEffects;
	Map<PredicateAssignment, Set<Clause>> predicate2Invariants;
	private final static Logger analysisLog = Logger.getLogger(IndigoAnalyzer.class.getName());

	private static final boolean z3Show = true;
	private static AbstractSpecification spec;

	static enum Result {
		OK, Idempotent, NonIdempotent, Opposing, Conflicting, SelfConflicting
	};

	boolean idempotent(Operation op, LogicExpression inv) {
		Z3 z3 = new Z3(z3Show);
		analysisLog.fine("; Testing Idempotence for {" + op + "}\n");

		Set<Expression> assertions = new HashSet<>();

		assertions.add(inv.expression());

		// Collect operation numeric effects over the invariant, applied once
		LogicExpression wpc = inv.copyOf();
		long numerics = opEffects.get(op).stream().filter(ei -> {
			PredicateAssignment e = ei.copyOf();
			e.applyEffectOnLogicExpression(wpc, 1);
			return e.isNumeric();
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

	Result opposing(final Collection<Operation> ops) {
		analysisLog.fine("; Checking: contraditory post-conditions...");
		Z3 z3 = new Z3(z3Show);

		ops.forEach(op -> {
			opEffects.get(op).forEach(e -> {
				if (!e.isNumeric()) {
					z3.Assert(e.getAssertion());
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
		return sat ? Result.OK : Result.Opposing;
	}

	boolean notSatisfies(final Collection<Operation> ops, LogicExpression invExpr) {

		Set<Expression> assertions = new HashSet<>();

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

	Result selfConflicting(Operation op, LogicExpression inv) {
		if (!idempotent(op, inv)) {
			return Result.NonIdempotent;
		}
		List<Operation> opList = new ArrayList<>();
		opList.add(op);
		opList.add(op);
		if (notSatisfies(opList, inv)) {
			return Result.SelfConflicting;
		} else
			return Result.OK;
	}

	Result conflict(LogicExpression inv, Collection<Operation> ops) {
		analysisLog.fine("; Checking: Negated Invariant satisfiability...");
		boolean satNotI = notSatisfies(ops, inv.copyOf());

		analysisLog.fine("; Negated Invariant is: " + (satNotI ? "SAT" : "UnSAT"));
		if (satNotI) {
			return Result.Conflicting;
		}

		analysisLog.fine("; Operations " + ops + " are safe together...");
		return Result.OK;
	}

	static List<PredicateAssignment> collectOperationEffects(AbstractSpecification target) {
		List<PredicateAssignment> res = new ArrayList<PredicateAssignment>();

		for (Operation m : target.getOperations()) {
			res.addAll(m.getEffects());
		}
		return res;
	}

	static <T> Set<T> setWith(Set<T> old, T item) {
		return new ImmutableSet.Builder<T>().addAll(old).add(item).build();
	}

	Clause invariantFor(Collection<Operation> ops) {
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
	void doIt(AbstractSpecification target) throws Exception {
		predicate2Invariants = spec.collectInvariantsForPredicate();

		final Collection<PredicateAssignment> effects = spec.getAllOperationEffects();

		Set<Operation> operations = target.getOperations();

		opEffects = new HashMap<>();
		operations
		.forEach(op -> {
			opEffects.put(op,
					effects.stream().filter(i -> op.opName().equals(i.opName())).collect(Collectors.toList()));
		});

		Map<Set<Operation>, Result> R = new HashMap<>();
		Sets.cartesianProduct(operations, operations)
		.forEach(
				opPair -> {
					Result r;

					if (!opPair.get(0).equals(opPair.get(1))) {
						r = opposing(opPair);
						if (!r.equals(Result.Opposing)) {
							r = conflict(invariantFor(opPair).toLogicExpression(), opPair);
						}
					} else {
						r = selfConflicting(opPair.get(0), invariantFor(opPair).toLogicExpression());
					}

					Set<Operation> opPairAsSet = Sets.newHashSet(opPair);

					Result currRes = R.get(opPairAsSet);
					if (currRes != null && currRes != r) {
						analysisLog
						.warning("------------------ Pair of operations has different results for different substitution orders.");
					}
					if (currRes == null || !r.equals(Result.OK)) {
						R.put(opPairAsSet, r);
					}

				});

		analysisLog.info("\n\n; Analysis Results for: " + target.getAppName());
		Lists.newArrayList(Result.Idempotent, Result.NonIdempotent, Result.OK, Result.SelfConflicting, Result.Opposing,
				Result.Conflicting).forEach(r -> {
					R.forEach((k, v) -> {
						if (r == v && k.size() == 1)
							analysisLog.info("; " + k + " -> " + r);
					});
				});
		Lists.newArrayList(Result.Idempotent, Result.NonIdempotent, Result.OK, Result.SelfConflicting, Result.Opposing,
				Result.Conflicting).forEach(r -> {
					R.forEach((k, v) -> {
						if (r == v && k.size() == 2)
							analysisLog.info("; " + k + " -> " + r);
					});
				});

	}

	public static void main(String[] args) throws Exception {
		if (args[0].equals("-java")) {

			spec = new JavaClassSpecification(Class.forName(args[1]));
			new IndigoAnalyzer().doIt(spec);

		} else if (args[0].equals("-json")) {
			// web-parser/spec.json
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

			new IndigoAnalyzer().doIt(spec);
		}

	}
}
