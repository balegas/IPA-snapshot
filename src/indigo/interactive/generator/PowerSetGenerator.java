package indigo.interactive.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import indigo.conflicts.test.OperationTest;
import indigo.generic.GenericOperation;
import indigo.interfaces.logic.Invariant;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.operations.Operation;
import indigo.interfaces.operations.OperationGenerator;
import indigo.interfaces.operations.OperationTransformer;
import indigo.runtime.AnalysisContext;
import indigo.runtime.IndigoAnalyzer;
import indigo.runtime.ProgramSpecification;

public class PowerSetGenerator implements OperationGenerator {

	private final Logger analysisLog = Logger.getLogger(IndigoAnalyzer.class.getName());
	private OperationTransformer transformFunction;
	private final ProgramSpecification spec;

	private final static Comparator<Set<PredicateAssignment>> compareBySize = new Comparator<Set<PredicateAssignment>>() {

		@Override
		public int compare(Set<PredicateAssignment> o1, Set<PredicateAssignment> o2) {
			return o1.size() - o2.size();
		}
	};

	private PowerSetGenerator(ProgramSpecification spec) {
		this.spec = spec;
	}

	public PowerSetGenerator(ProgramSpecification spec, OperationTransformer function) {
		this(spec);
		this.transformFunction = function;
	}

	@Override
	public List<List<Operation>> generate(OperationTest operationTest, AnalysisContext context) {
		Invariant invariant = spec.invariantFor(operationTest.asSet(), context);
		Set<PredicateAssignment> explorationSeed = Sets.newHashSet();
		operationTest.asList().forEach(
				op -> explorationSeed.addAll(context.getOperationEffects(op, true, true).stream().filter(effect -> {
					return effect.affects(invariant);

				}).collect(Collectors.toSet())));

		Set<Set<PredicateAssignment>> setsPredsForNewOpsUnordered = powerSet(explorationSeed).stream().map(set -> {
			if (transformFunction != null) {
				return transformFunction.transformEffects(set);
			} else {
				return set;
			}
		}).collect(Collectors.toSet());

		PriorityBlockingQueue<Set<PredicateAssignment>> setsPredsForNewOps = new PriorityBlockingQueue<Set<PredicateAssignment>>(
				1, compareBySize);
		setsPredsForNewOpsUnordered.forEach(e -> setsPredsForNewOps.add(e));

		List<List<Operation>> allTestPairs = Lists.newLinkedList();
		Collection<Collection<PredicateAssignment>> distinctOps = Lists.newLinkedList();
		for (String opName : operationTest.asList()) {
			for (Set<PredicateAssignment> predsForNewOps : setsPredsForNewOps) {
				String newOpName = opName;
				Operation operation = context.getOperation(opName);
				Set<PredicateAssignment> predsForNewOp = Sets.newHashSet();

				predsForNewOp.addAll(context.getOperationEffects(opName, false, true));
				for (PredicateAssignment predForNewOps : predsForNewOps) {
					if (!predsForNewOp.contains(predForNewOps)) {
						predsForNewOp.add(predForNewOps);
					}
				}

				// Check same predicate set and value
				// TODO: predicate assignment equals does not check values.
				if (!GenericOperation.strictContains(predsForNewOp, distinctOps)) {
					distinctOps.add(predsForNewOp);
					GenericOperation newOp = new GenericOperation(newOpName, predsForNewOp, operation.getParameters());
					List<String> otherOps = Lists.newLinkedList(operationTest.asList());
					otherOps.remove(opName);
					for (String otherOpName : otherOps) {
						GenericOperation otherOp = new GenericOperation(otherOpName,
								context.getOperationEffects(otherOpName, false, true), operation.getParameters());
						// NEW OP AT INDEX 0.
						allTestPairs.add(ImmutableList.of(newOp, otherOp));
						analysisLog.fine("Added " + newOpName + " with effect set: " + predsForNewOp);
					}
				} else {
					analysisLog.fine("Operation with effect set: " + predsForNewOp + " already exists");
				}

			}
		}
		return allTestPairs;
	}

	private static <T> Collection<Set<T>> powerSet(Set<T> originalSet) {
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
