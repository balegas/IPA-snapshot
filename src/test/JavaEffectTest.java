package test;

import static org.junit.Assert.assertTrue;
import indigo.impl.javaclass.BooleanValue;
import indigo.impl.javaclass.effects.AssertionPredicate;
import indigo.impl.javaclass.effects.JavaEffect;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import app.ITournament;

public class JavaEffectTest {

	@Test
	public void test() {
		List<AssertionPredicate> res = null;
		for (Method m : ITournament.class.getMethods()) {
			if (m.getName().equals("enroll"))
				res = AssertionPredicate.listFor(m);
		}
		JavaEffect newEffect = res.get(0).copyWithNewValue(BooleanValue.FalseValue());
		System.out.println(newEffect);
		System.out.println(res.get(0));
		assertTrue(true);
	}
}
