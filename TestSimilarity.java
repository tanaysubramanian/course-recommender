package edu.brown.cs.student;

import edu.brown.cs.student.main.similarityalg.CourseSimilarity;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;

/**
 * Class to test similarity algorithm
 */
public class TestSimilarity {

  private CourseSimilarity courseSimilarity;

  @Before
  public void setUp() throws IOException {
    String filePath = "C:\\Users\\HP\\Desktop\\Tanay Subramanian\\Education\\Brown\\Sophomore\\CS 320\\Projects\\term-project-halcanta-jle37-bhkang-tsubram4\\src\\main\\java\\edu\\brown\\cs\\student\\main\\scraper\\mock.json";
    String jsonInput = new String(Files.readAllBytes(Paths.get(filePath)));
    JSONObject jsonData = new JSONObject(jsonInput);
    courseSimilarity = new CourseSimilarity(jsonData);
  }

  @Test
  public void testSimilarityWithUniqueKeyword() {
    Set<String> pastCourses = new HashSet<>();
    JSONObject scores = courseSimilarity.calculateSimilarityAndModifyJson("araBIC", pastCourses, false);
    double index1 = scores.getJSONArray("ARAB").getJSONObject(0).getDouble("similarity_index");
    double index2 = scores.getJSONArray("ARAB").getJSONObject(1).getDouble("similarity_index");
    double index3 = scores.getJSONArray("ARAB").getJSONObject(2).getDouble("similarity_index");
    double index4 = scores.getJSONArray("ARAB").getJSONObject(3).getDouble("similarity_index");

    // all similarity scores are greater than 0, indicating that there were keyword matches
    assertEquals(0.018691588193178177, index1, 0.0);
    assertEquals(    0.02150537632405758, index2, 0.0);
    assertEquals(0.03361344709992409, index3, 0.0);
    assertEquals(0.04938271641731262, index4, 0.0);
  }

  @Test
  public void testDeptWeight() {
    Set<String> pastCourses = new HashSet<>();
    pastCourses.add("ARAB 0500");
    pastCourses.add("ARAB 0950");

    JSONObject scores = courseSimilarity.calculateSimilarityAndModifyJson("araBIC", pastCourses, false);
    double index1 = scores.getJSONArray("ARAB").getJSONObject(0).getDouble("similarity_index");
    double index2 = scores.getJSONArray("ARAB").getJSONObject(1).getDouble("similarity_index");
    double index3 = scores.getJSONArray("ARAB").getJSONObject(2).getDouble("similarity_index");
    double index4 = scores.getJSONArray("ARAB").getJSONObject(3).getDouble("similarity_index");

    // similarity increases by 0.2 for courses in same department
    assertEquals(0.21869158744812012, index1, 0.0);
    assertEquals(0.22150537371635437, index2, 0.0);

    // similarity is 0 for courses that have already been taken
    assertEquals(0.0, index3, 0.0);
    assertEquals(0.0, index4, 0.0);
  }

  @Test
  public void testProfWeight() {
    Set<String> pastCourses = new HashSet<>();
    pastCourses.add("ARAB 0100");
    pastCourses.add("ARAB 0300");

    JSONObject scores = courseSimilarity.calculateSimilarityAndModifyJson("araBIC", pastCourses, true);
    double index1 = scores.getJSONArray("ARAB").getJSONObject(0).getDouble("similarity_index");
    double index2 = scores.getJSONArray("ARAB").getJSONObject(1).getDouble("similarity_index");
    double index3 = scores.getJSONArray("ARAB").getJSONObject(2).getDouble("similarity_index"); //ARAB 0500
    double index4 = scores.getJSONArray("ARAB").getJSONObject(3).getDouble("similarity_index"); //ARAB 0950

    // similarity increases by 0.3 for courses taught by same prof
    assertEquals(0.3493827283382416, index4, 0.0);

    // similarity is 0 for courses that have already been taken
    assertEquals(0.0, index1, 0.0);
    assertEquals(0.0, index2, 0.0);

    // similarity is same as before for courses in same department but different professor
    assertEquals(0.03361344709992409, index3, 0.0);
  }

  @Test
  public void testSimilarityWithNoOccurrences() {
    Set<String> pastCourses = new HashSet<>();
    JSONObject scores = courseSimilarity.calculateSimilarityAndModifyJson("randomword123", pastCourses, false);
    double index = scores.getJSONArray("COLT").getJSONObject(0).getDouble("similarity_index");
    assertEquals(0.0, index, 0.0);
  }
}
