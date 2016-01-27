package indigo.interactive.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import indigo.conflicts.test.OperationTest;
import indigo.generic.GenericOperation;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.operations.Operation;
import indigo.runtime.AnalysisContext;
import indigo.runtime.ProgramSpecification;

public class OperationComposer extends AbstractOperationGenerator {

	private final Map<String, Operation> operations;
	private final int maxDegree;

	public OperationComposer(Set<Operation> operations, int maxDegree) {
		this.operations = operations.stream().collect(Collectors.toMap(Operation::opName, Function.identity()));
		this.maxDegree = maxDegree;
	}

	public OperationComposer(ProgramSpecification spec, int maxDegree) {
		this(spec.getOperations(), maxDegree);
	}

	@Override
	public List<List<Operation>> generate(OperationTest operationTest, AnalysisContext context) {
		List<List<Operation>> newTestPairs = Lists.newArrayList();
		List<String> test = operationTest.asList();
		for (int i = 0; i < test.size(); i++) {
			Map<String, Operation> map = Maps.newHashMap(operations);
			Operation elem = map.remove(test.get(i));
			List<List<Operation>> results = Lists.newArrayList();

			List<List<Operation>> lists = PowerSetGenerator.powerSet(map.values(), maxDegree);

			List<List<Operation>> allPerm = lists.stream().map(l -> {
				List<List<Operation>> accum = Lists.newArrayList();
				permutation(Lists.newArrayList(), l, accum);
				return accum;
			}).reduce(Lists.newArrayList(), (acc, li) -> {
				acc.addAll(li);
				return acc;
			});

			allPerm.stream().forEach(l -> l.add(0, elem));
			results.addAll(allPerm);

			for (List<Operation> opSequence : results) {
				Map<String, PredicateAssignment> predicateToValue = Maps.newHashMap();
				for (Operation seq : opSequence) {
					for (PredicateAssignment p : seq.getEffects()) {
						predicateToValue.put(p.getPredicateName(), p);
					}
				}

				ArrayList<Operation> newTestPair = Lists.newArrayList(
						new GenericOperation(test.get(i), predicateToValue.values(), elem.getParameters()));
				operationTest.asSet().forEach(t -> {
					if (!t.equals(elem.opName()))
						newTestPair.add(context.getOperation(t));
				});
				newTestPairs.add(newTestPair);
			}
		}

		return newTestPairs;
	}

	private static <T> void permutation(List<T> prefix, List<T> sequence, List<List<T>> accum) {
		int n = sequence.size();
		if (n == 0)
			accum.add(prefix);
		else {
			for (int i = 0; i < n; i++) {
				ArrayList<T> prefixCopy = Lists.newArrayList(prefix);
				prefixCopy.add(sequence.get(i));
				List<T> rest = Lists.newArrayList(sequence.subList(0, i));
				List<T> part2 = Lists.newArrayList(sequence.subList(i + 1, n));
				rest.addAll(part2);
				permutation(prefixCopy, rest, accum);
			}
		}
	}

}
