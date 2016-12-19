// package test;
//
// import java.util.Collection;
// import java.util.List;
// import java.util.Set;
// import java.util.stream.Collectors;
//
// import org.junit.Test;
//
// import com.google.common.collect.ImmutableList;
//
// import indigo.conflicts.test.OperationPairTest;
// import indigo.interactive.generator.OperationComposer;
// import indigo.interfaces.logic.PredicateAssignment;
// import indigo.interfaces.operations.Operation;
// import indigo.interfaces.operations.Parameter;
//
// public class ComposerTest {
//
// @Test
// public void test() {
// List<String> opNames = ImmutableList.of("opA", "opB", "opC",
// "opD" /* , "opE", "opF" */);
// Set<Operation> operations = opNames.stream().map(opN -> new
// DumbOperation(opN)).collect(Collectors.toSet());
// OperationComposer composer = new OperationComposer(operations, 3);
// List<List<Operation>> gen = composer.generate(new OperationPairTest("opA",
// "opB"), null);
// for (List<Operation> g : gen) {
// System.out.println(g);
// }
// }
// }
//
// class DumbOperation implements Operation {
//
// String opName;
//
// public DumbOperation(String opName) {
// this.opName = opName;
// }
//
// @Override
// public String opName() {
// return opName;
// }
//
// @Override
// public String toString() {
// return opName();
// }
//
// @Override
// public Collection<PredicateAssignment> getEffects() {
// return ImmutableList.of();
// }
//
// @Override
// public List<Parameter> getParameters() {
// return null;
// }
//
// @Override
// public boolean isSubset(Operation otherOp) {
// return false;
// }
//
// }