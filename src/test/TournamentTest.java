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
