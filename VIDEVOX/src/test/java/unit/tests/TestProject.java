package unit.tests;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nz.ac.auckland.model.AudioFile;
import nz.ac.auckland.model.Project;
import nz.ac.auckland.model.VidevoxException;

public class TestProject {

	private static final Logger logger = Logger.getLogger(TestProject.class);

	static Project project;
	static File file;

	@BeforeClass
	public static void setUp() {
		project = Project.getProject();
		file = new File("test.txt");
		FileWriter w;
		try {
			w = new FileWriter(file);
			w.write("");
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void cleanUp() {
		file.deleteOnExit();
	}

	@Before
	public void beforeTest() {
		Project.reset();
		project = Project.getProject();
		FileWriter w;
		try {
			w = new FileWriter(file);
			w.write("");
			w.flush();
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConstructor() {
		if (!project.getName().equals("New Project")) {
			fail("Wrong name: " + project.getName());
		}
	}

	@Test
	public void testSimpleJSON() {
		try {
			// This project was made using the reset method
			project.toFile(file);
			String str1 = ((JSONObject) new JSONParser().parse(new FileReader(file))).toJSONString();
			// This project was built from the first project's output file
			Project.buildProject(file);
			project.toFile(file);
			String str2 = ((JSONObject) new JSONParser().parse(new FileReader(file))).toJSONString();
			// If both the projects produce the same json doc, then it should be
			// working fine
			if (!str2.equals(str1)) {
				logger.debug(str1);
				logger.debug(str2);
				fail("Strings were different");
			}
		} catch (VidevoxException | IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAudioJSON() {
		try {
			AudioFile audio = new AudioFile();
			logger.debug(audio.getName());
			project.addAudio(audio);
			project.toFile(file);
			project.toFile(file);
			String str1 = ((JSONObject) new JSONParser().parse(new FileReader(file))).toJSONString();
			// This project was built from the first project's output file
			Project.buildProject(file);
			project.toFile(file);
			String str2 = ((JSONObject) new JSONParser().parse(new FileReader(file))).toJSONString();
			// If both the projects produce the same json doc, then it should be
			// working fine
			if (!str2.equals(str1)) {
				logger.debug(str1);
				logger.debug(str2);
				fail("Strings were different");
			}
		} catch (VidevoxException | IOException | ParseException e) {
			e.printStackTrace();
		}
	}

}
