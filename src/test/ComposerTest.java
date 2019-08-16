/**
-------------------------------------------------------------------

Copyright (c) 2014 SyncFree Consortium.  All Rights Reserved.

This file is provided to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License.  You may obtain
a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

-------------------------------------------------------------------
**/
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