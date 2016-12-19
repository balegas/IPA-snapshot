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

import indigo.impl.javaclass.JavaClassSpecification;
import indigo.impl.json.JSONSpecification;
import indigo.runtime.IndigoAnalyzer;
import indigo.runtime.ProgramSpecification;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

public class TournamentTest {

	private static ProgramSpecification JSON_SPEC;
	private static ProgramSpecification JAVA_SPEC;

	@Before
	public void loadJSONSpec() throws Exception {
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

		JSON_SPEC = new JSONSpecification((JSONObject) obj);
	}

	@Before
	public void LoadClass() {
		JAVA_SPEC = new JavaClassSpecification(app.OLDITournament.class);
	}

	@Test
	public void testJAVA() {
		IndigoAnalyzer.analyse(JAVA_SPEC, false);
	}

	@Test
	public void testJSON() {
		// ATTENTION: [ setLeader, rem_player] : OK
		// ATTENTION: [ beginTournament, enroll] : is not correctly specified
		// (uses a single player instead of wildcard)
		IndigoAnalyzer.analyse(JSON_SPEC, false);
	}

	// MUST MAKE TWO EQUIVALENT IMPLEMENTATIONS AND CHECK RESULTS ARE THE
	// SAME!!!

}
