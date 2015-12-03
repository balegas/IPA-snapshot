package test;

import static org.junit.Assert.assertEquals;
import indigo.IndigoAnalyzer;
import indigo.OperationConflicts;
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
		Collection<OperationConflicts> result = IndigoAnalyzer.analyse(JAVA_SPEC, false);
		for (OperationConflicts op : result) {
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
		Collection<OperationConflicts> result = IndigoAnalyzer.analyse(JAVA_SPEC, false);
		for (OperationConflicts op : result) {
			if (!op.isSingleOp()) {
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
		Collection<OperationConflicts> result = IndigoAnalyzer.analyse(JAVA_SPEC, false);
		for (OperationConflicts op : result) {
			if (!op.isSingleOp()) {
				assertEquals(true, op.isOpposing());
			}
		}
	}

}
