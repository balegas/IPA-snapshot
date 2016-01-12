package test;

import indigo.AnalysisContext;
import indigo.PredicateFactory;
import indigo.ProgramSpecification;
import indigo.impl.javaclass.BooleanValue;
import indigo.impl.javaclass.GenericConflictResolutionPolicy;
import indigo.impl.javaclass.JavaClassSpecification;
import indigo.impl.json.JSONSpecification;
import indigo.interfaces.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class AnalysisContextTest {

	private static ProgramSpecification spec;

	// @Before
	public void loadTestFile() throws Exception {
		File file = new File("web-parser/spec.json");
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
	}

	@Before
	public void alternativeLoadTestFile() {
		spec = new JavaClassSpecification(test.Opposing.class);
	}

	@Test
	public void test() {
		ImmutableSet<String> opsToTest = ImmutableSet.of("doIt", "doNotDoIt");
		Map<String, Value> predicateToResolution = Maps.newHashMap();
		predicateToResolution.put("A", BooleanValue.FalseValue());
		GenericConflictResolutionPolicy conflictResolution = new GenericConflictResolutionPolicy(predicateToResolution);
		PredicateFactory factory = PredicateFactory.getFactory();
		AnalysisContext context = AnalysisContext.getNewContext(spec.getOperations(), conflictResolution, factory);
		AnalysisContext innerContext = context.childContext(false);
		innerContext.solveOpposing(opsToTest, true);
	}
}
