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
// import static org.junit.Assert.assertEquals;
//
// import java.util.Collection;
//
// import org.junit.Test;
//
// import com.google.common.collect.ImmutableSet;
//
// import indigo.conflicts.test.OperationTest;
// import indigo.impl.javaclass.JavaClassSpecification;
// import indigo.runtime.IndigoAnalyzer;
// import indigo.runtime.ProgramSpecification;
//
// public class SimpleJavaTests {
//
// private static ProgramSpecification JAVA_SPEC;
//
// @Test
// public void testDisjunction() {
// JAVA_SPEC = new JavaClassSpecification(test.Disjunction.class);
// Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, false);
// for (OperationTest op : result) {
// if (op.asSet().containsAll(ImmutableSet.of("a", "b"))) {
// assertEquals(true, op.isConflicting());
// }
// if (op.asSet().containsAll(ImmutableSet.of("a", "c"))) {
// assertEquals(true, op.isConflicting());
// }
// if (op.asSet().containsAll(ImmutableSet.of("c", "b"))) {
// assertEquals(true, op.isConflicting());
// }
// }
// }
//
// @Test
// public void integrity() {
// JAVA_SPEC = new JavaClassSpecification(test.ReferentialIntegrity.class);
// Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, false);
// for (OperationTest op : result) {
// if (op.asSet().containsAll(ImmutableSet.of("doA", "doNotB"))) {
// assertEquals(true, op.isConflicting());
// }
// }
// }
//
// // @Test
// // public void opposing() {
// // JAVA_SPEC = new JavaClassSpecification(test.Opposing.class);
// // Collection<OperationConflicts> result = IndigoAnalyzer.analyse(JAVA_SPEC,
// // false);
// // }
//
// @Test
// public void opposing0() {
// JAVA_SPEC = new JavaClassSpecification(test.OpposingForDebug.class);
// Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, false);
// for (OperationTest op : result) {
// if (op.asSet().containsAll(ImmutableSet.of("doIt", "doNotDoIt"))) {
// assertEquals(true, op.isOpposing());
// }
// }
// }
//
// @Test
// public void notAllTrue() {
// JAVA_SPEC = new JavaClassSpecification(test.NotAllTrue.class);
// Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, false);
// boolean conflict1 = false;
// for (OperationTest op : result) {
// if (op.asSet().containsAll(ImmutableSet.of("doA", "doB"))) {
// conflict1 = op.isConflicting();
// }
// }
// assertEquals(true, conflict1);
// }
//
// @Test
// public void fixConflict() {
// JAVA_SPEC = new JavaClassSpecification(test.ResolutionAndConflict.class);
// Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, true);
// boolean conflict1 = false, conflict2 = false;
// for (OperationTest op : result) {
// if (op.asSet().containsAll(ImmutableSet.of("doA", "doNotB"))) {
// conflict1 = op.isConflicting();
// }
// if (op.asSet().containsAll(ImmutableSet.of("doNotA_mod", "doNotB"))) {
// conflict2 = op.isConflicting();
// }
// }
// assertEquals(true, conflict1 & conflict2);
// }
//
// @Test
// public void fixMultipleConflict() {
// JAVA_SPEC = new
// JavaClassSpecification(test.MultiplePredicateResolution.class);
// Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, true);
// boolean conflict = false;
// boolean noconflict = true;
// for (OperationTest op : result) {
// if (op.asSet().containsAll(ImmutableSet.of("doNotA-A", "doNotBNotA-A"))) {
// conflict = !op.isValidWPC();
// }
// if (op.asSet().containsAll(ImmutableSet.of("doNotA-A", "doA-A"))) {
// noconflict = op.isOK();
// }
// }
// assertEquals(true, conflict && noconflict);
// }
//
// @Test
// public void fixMultipleConflict2() {
// JAVA_SPEC = new
// JavaClassSpecification(test.MultiplePredicateResolution2.class);
// Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, true);
// boolean noConflict = true;
// for (OperationTest op : result) {
// if (op.asSet().containsAll(ImmutableSet.of("doNotA-A-Other", "doA"))) {
// noConflict = op.isConflicting();
// }
// }
// assertEquals(false, noConflict);
// }
//
// @Test
// public void fixReferentialIntegrity() {
// JAVA_SPEC = new JavaClassSpecification(test.ReferentialIntegrity.class);
// Collection<OperationTest> result =
// IndigoAnalyzer.interactiveResolution(JAVA_SPEC);
// boolean noConflict = false;
// for (OperationTest op : result) {
// if (op.asSet().containsAll(ImmutableSet.of("doA", "doNotB"))) {
// noConflict = op.isOK();
// }
// }
// assertEquals(true, noConflict);
// }
//
// @Test
// public void fixReferentialIntegrity2() {
// JAVA_SPEC = new JavaClassSpecification(test.ReferentialIntegrity2.class);
// Collection<OperationTest> result =
// IndigoAnalyzer.interactiveResolution(JAVA_SPEC);
// boolean noConflict = false;
// for (OperationTest op : result) {
// if (op.asSet().containsAll(ImmutableSet.of("doA", "doNotB"))) {
// noConflict = op.isOK();
// }
// }
// assertEquals(true, noConflict);
// }
//
// }
