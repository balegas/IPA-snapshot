package test;

import static org.junit.Assert.assertEquals;
import indigo.IndigoAnalyzer;
import indigo.OperationTest;
import indigo.ProgramSpecification;
import indigo.impl.javaclass.JavaClassSpecification;

import java.util.Collection;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class SimpleJavaTests {

	private static ProgramSpecification JAVA_SPEC;

	@Test
	public void testDisjunction() {
		JAVA_SPEC = new JavaClassSpecification(test.Disjunction.class);
		Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, false);
		for (OperationTest op : result) {
			if (op.asSet().containsAll(ImmutableSet.of("a", "b"))) {
				assertEquals(true, op.isConflicting());
			}
			if (op.asSet().containsAll(ImmutableSet.of("a", "c"))) {
				assertEquals(true, op.isConflicting());
			}
			if (op.asSet().containsAll(ImmutableSet.of("c", "b"))) {
				assertEquals(true, op.isConflicting());
			}
		}
	}

	@Test
	public void integrity() {
		JAVA_SPEC = new JavaClassSpecification(test.ReferentialIntegrity.class);
		Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, false);
		for (OperationTest op : result) {
			if (op.asSet().containsAll(ImmutableSet.of("doA", "doNotB"))) {
				assertEquals(true, op.isConflicting());
			}
		}
	}

	// @Test
	// public void opposing() {
	// JAVA_SPEC = new JavaClassSpecification(test.Opposing.class);
	// Collection<OperationConflicts> result = IndigoAnalyzer.analyse(JAVA_SPEC,
	// false);
	// }

	@Test
	public void opposing0() {
		JAVA_SPEC = new JavaClassSpecification(test.OpposingForDebug.class);
		Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, false);
		for (OperationTest op : result) {
			if (op.asSet().containsAll(ImmutableSet.of("doIt", "doNotDoIt"))) {
				assertEquals(true, op.isOpposing());
			}
		}
	}

	@Test
	public void notAllTrue() {
		JAVA_SPEC = new JavaClassSpecification(test.NotAllTrue.class);
		Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, false);
		boolean conflict1 = false;
		for (OperationTest op : result) {
			if (op.asSet().containsAll(ImmutableSet.of("doA", "doB"))) {
				conflict1 = op.isConflicting();
			}
		}
		assertEquals(true, conflict1);
	}

	@Test
	public void fixConflict() {
		JAVA_SPEC = new JavaClassSpecification(test.ResolutionAndConflict.class);
		Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, true);
		boolean conflict1 = false, conflict2 = false;
		for (OperationTest op : result) {
			if (op.asSet().containsAll(ImmutableSet.of("doA", "doNotB"))) {
				conflict1 = op.isConflicting();
			}
			if (op.asSet().containsAll(ImmutableSet.of("doNotA_mod", "doNotB"))) {
				conflict2 = op.isConflicting();
			}
		}
		assertEquals(true, conflict1 & conflict2);
	}

	@Test
	public void fixMultipleConflict() {
		JAVA_SPEC = new JavaClassSpecification(test.MultiplePredicateResolution.class);
		Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, true);
		boolean conflict = false;
		boolean noconflict = true;
		for (OperationTest op : result) {
			if (op.asSet().containsAll(ImmutableSet.of("doNotA-A", "doNotBNotA-A"))) {
				conflict = op.isConflicting();
			}
			if (op.asSet().containsAll(ImmutableSet.of("doNotA", "doNotBNotA"))) {
				conflict = op.isConflicting();
			}
		}
		assertEquals(true, conflict && noconflict);
	}

	@Test
	public void fixMultipleConflict2() {
		JAVA_SPEC = new JavaClassSpecification(test.MultiplePredicateResolution2.class);
		Collection<OperationTest> result = IndigoAnalyzer.analyse(JAVA_SPEC, true);
		boolean noConflict = true;
		for (OperationTest op : result) {
			if (op.asSet().containsAll(ImmutableSet.of("doNotA-A-Other", "doA"))) {
				noConflict = op.isConflicting();
			}
		}
		assertEquals(false, noConflict);
	}

	@Test
	public void fixReferentialIntegrity() {
		JAVA_SPEC = new JavaClassSpecification(test.ReferentialIntegrity.class);
		Collection<OperationTest> result = IndigoAnalyzer.interactiveResolution(JAVA_SPEC);
		boolean noConflict = false;
		for (OperationTest op : result) {
			if (op.asSet().containsAll(ImmutableSet.of("doA", "doNotB"))) {
				noConflict = op.isOK();
			}
		}
		assertEquals(true, noConflict);
	}

}
