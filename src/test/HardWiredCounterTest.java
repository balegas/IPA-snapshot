package test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import indigo.conflicts.test.SingleOperationTest;
import indigo.conflitcs.GenericConflictResolutionPolicy;
import indigo.generic.GenericOperation;
import indigo.generic.GenericPredicateAssignment;
import indigo.generic.GenericPredicateFactory;
import indigo.generic.GenericVariable;
import indigo.impl.javaclass.JavaInvariantClause;
import indigo.impl.json.JSONConstant;
import indigo.interfaces.interactive.ConflictResolutionPolicy;
import indigo.interfaces.logic.Invariant;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.Operation;
import indigo.interfaces.operations.Parameter;
import indigo.runtime.AnalysisContext;
import indigo.runtime.IndigoAnalyzer;
import indigo.runtime.ProgramSpecification;

public class HardWiredCounterTest {

	public static void main(String[] args) {
		DummySpec spec = new DummySpec();
		IndigoAnalyzer analyzer = new IndigoAnalyzer(spec, true);
		AnalysisContext rootContext = AnalysisContext.getNewContext(spec.getOperations(),
				GenericConflictResolutionPolicy.getDefault(), GenericPredicateFactory.getFactory());
		analyzer.testIdempotence(new SingleOperationTest(DummySpec.op1Name), rootContext.childContext(false));
	}

}

class DummySpec implements ProgramSpecification {

	Invariant invariant = new JavaInvariantClause("forall( Int : x ) :- Value(x) <= 20");

	Set<Invariant> invariants = ImmutableSet.of(invariant);

	static String op1Name = "increment";

	String predicateName = "Value";

	List<Parameter> params = ImmutableList.of(new GenericVariable("arg", "Int"));
	PredicateAssignment effect1 = new GenericPredicateAssignment(op1Name, predicateName,
			new JSONConstant(PREDICATE_TYPE.numeric, "1"), params);

	List<PredicateAssignment> effects = ImmutableList.of(effect1);

	List<Parameter> opParams = params;

	Operation op1 = new GenericOperation(op1Name, effects, opParams);

	Set<Operation> operations = ImmutableSet.of(op1);

	@Override
	public Set<Operation> getOperations() {
		return operations;
	}

	@Override
	public Set<String> getOperationsNames() {
		return ImmutableSet.of(op1Name);
	}

	@Override
	public Set<Invariant> getInvariantClauses() {
		return invariants;
	}

	@Override
	public String getAppName() {
		return "NO_NAME";
	}

	@Override
	public Invariant newEmptyInv() {
		return null;
	}

	@Override
	public ConflictResolutionPolicy getDefaultConflictResolutionPolicy() {
		return null;
	}

	@Override
	public void updateOperations(Collection<Operation> newOperations) {
		// TODO Auto-generated method stub

	}

	@Override
	public Invariant invariantFor(Collection<String> asSet, AnalysisContext context) {
		return invariant;
	}
}
