import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  /**
   * @param args - input of array of strings
   */
  private Main(String[] args) {
  }

  private void run() {
    try {
      String content = new String(Files.readAllBytes(Paths.get("C:\\Users\\HP\\Desktop\\Tanay Subramanian\\Education\\Brown\\Sophomore\\CS 320\\Projects\\term-project-halcanta-jle37-bhkang-tsubram4\\scraper\\mock.json")));
      JSONObject jsonData = new JSONObject(content);
      CourseSimilarity cs = new CourseSimilarity(jsonData);

      Map<String, Float> scores = cs.calculateSimilarity("Arabic");
      for (Map.Entry<String, Float> score : scores.entrySet()) {
        System.out.println("Course: " + score.getKey() + " - Similarity: " + score.getValue());
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error reading JSON file.");
    }
  }
}