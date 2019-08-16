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
package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;

import indigo.impl.javaclass.JavaClassSpecification;
import indigo.impl.json.JSONSpecification;
import indigo.runtime.ProgramSpecification;

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

	// @Test
	// public void test() {
	// ImmutableSet<String> opsToTest = ImmutableSet.of("doIt", "doNotDoIt");
	// Map<String, Value> predicateToResolution = Maps.newHashMap();
	// predicateToResolution.put("A", BooleanValue.FalseValue());
	// GenericConflictResolutionPolicy conflictResolution = new
	// GenericConflictResolutionPolicy(predicateToResolution);
	// GenericPredicateFactory factory = GenericPredicateFactory.getFactory();
	// AnalysisContext context =
	// AnalysisContext.getNewContext(spec.getOperations(), conflictResolution,
	// factory);
	// AnalysisContext innerContext = context.childContext(false);
	// innerContext.solveOpposing(opsToTest, true);
	// }
}
