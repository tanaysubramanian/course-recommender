package edu.brown.cs.student.main.similarityalg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

public class CourseSimilarity {

  private JSONObject coursesJson;

  public CourseSimilarity(JSONObject jsonData) {
    this.coursesJson = jsonData;
  }

  public Set<String> getInstructorsFromPastCourses(Set<String> pastCourses) {
    Set<String> pastInstructors = new HashSet<>();
    for (String courseCode : pastCourses) {
      for (Object deptKey : coursesJson.keySet()) {
        JSONArray deptCourses = coursesJson.getJSONArray((String) deptKey);
        for (int i = 0; i < deptCourses.length(); i++) {
          JSONObject courseObj = deptCourses.getJSONObject(i);
          if (courseObj.getString("class_code").equals(courseCode)) {
            JSONArray instructors = courseObj.getJSONArray("class_instructors");
            for (int j = 0; j < instructors.length(); j++) {
              pastInstructors.add(instructors.getJSONObject(j).getString("name"));
            }
            break;
          }
        }
      }
    }
    return pastInstructors;
  }

  public JSONObject calculateSimilarityAndModifyJson(String keyword, Set<String> pastCourses, boolean prioritizeInstructors) {
    Set<String> pastInstructors = getInstructorsFromPastCourses(pastCourses);
    Set<String> pastDepts = new HashSet<>();
    for (String courseCode : pastCourses) {
      pastDepts.add(courseCode.split(" ")[0]);
    }

    for (Object deptKey : coursesJson.keySet()) {
      JSONArray deptCourses = coursesJson.getJSONArray((String) deptKey);
      for (int i = 0; i < deptCourses.length(); i++) {
        // matching search keyword frequency
        JSONObject courseObj = deptCourses.getJSONObject(i);
        String classCode = courseObj.getString("class_code");
        String classTitle = courseObj.getString("class_title");
        String classDescription = courseObj.getString("class_description");
        String combinedText = classTitle + " " + classDescription;

        // making matching case-insensitive
        combinedText = combinedText.toLowerCase();
        keyword = keyword.toLowerCase();

        int totalWords = combinedText.split("\\s+").length;
        int keywordFrequency = (combinedText.split("\\b" + keyword + "\\b", -1).length) - 1;
        float similarity = (totalWords > 0) ? (float) keywordFrequency / totalWords : 0;

        // instructor weight
        if (prioritizeInstructors) {
          JSONArray instructors = courseObj.getJSONArray("class_instructors");
          for (int j = 0; j < instructors.length(); j++) {
            Object instructorObj = instructors.get(j);
            if (instructorObj instanceof JSONObject) {
              String instructorName = ((JSONObject) instructorObj).getString("name");
              if (pastInstructors.contains(instructorName)) {
                similarity += 0.3;
                break;
              }
            }
          }
        }

        else { // department weight
          String dept = courseObj.getString("class_dept");
          if (pastDepts.contains(dept)) {
            similarity += 0.2;
          }
        }

        // Set similarity to 0 for past courses
        if (pastCourses.contains(classCode)) {
          similarity = 0;
        }

        // Adding similarity_index field to each course object in JSON
        courseObj.put("similarity_index", similarity);
      }
    }
    return coursesJson;
  }
}

//  public JSONObject calculateSimilarityAndModifyJson(String keyword, Set<String> pastCourses, boolean prioritizeInstructor) {
//    for (Object deptKey : coursesJson.keySet()) {
//      Set<String> pastInstructors = new HashSet<>();
//      Set<String> pastDepartments = new HashSet<>();
//
//      for (String course : pastCourses) {
//        String[] parts = course.split(" ");
//        if (parts.length == 2) {
//          String dept = parts[0];
//          String code = parts[1];
//
//      JSONArray deptCourses = coursesJson.getJSONArray((String) deptKey);
//      for (int i = 0; i < deptCourses.length(); i++) {
//        JSONObject courseObj = deptCourses.getJSONObject(i);
//        String classTitle = courseObj.getString("class_title");
//        String classDescription = courseObj.getString("class_description");
//        String combinedText = classTitle + " " + classDescription;
//        int totalWords = combinedText.split("\\s+").length;
//        int keywordFrequency = (combinedText.split("\\b" + keyword + "\\b", -1).length) - 1;
//        float similarity = totalWords > 0 ? (float) keywordFrequency / totalWords : 0;
//        // Adding the similarity_index field to each course object
//        courseObj.put("similarity_index", similarity);
//      }
//    }
//    return coursesJson;
//  }
//}

//  private Map<String, String[]> courses;
//
//  public CourseSimilarity(JSONObject jsonData) {
//    this.courses = new HashMap<>(); // hashmap of keys as classcodes and values as aggregated class descriptions
//    parseCourses(jsonData);
//  }
//
//  private void parseCourses(JSONObject jsonData) {
//    for (Object deptKey : jsonData.keySet()) {
//      JSONArray deptCourses = jsonData.getJSONArray((String) deptKey);
//      for (int i = 0; i < deptCourses.length(); i++) {
//        JSONObject courseObj = deptCourses.getJSONObject(i);
//        String classCode = courseObj.getString("class_code");
//        String classTitle = courseObj.getString("class_title");
//        String classDescription = courseObj.getString("class_description");
//        this.courses.put(classCode, new String[] {classTitle, classDescription});
//      }
//    }
//  }
//
//  public Map<String, Float> calculateSimilarity(String keyword) {
//    Map<String, Float> similarityScores = new HashMap<>();
//    for (Map.Entry<String, String[]> entry : this.courses.entrySet()) {
//      String classCode = entry.getKey();
//      String[] details = entry.getValue();
//      String combinedText = details[0] + " " + details[1];
//      int totalWords = combinedText.split("\\s+").length;
//      int keywordFrequency = (combinedText.split("\\b" + keyword + "\\b", -1).length) - 1;
//      float similarity = totalWords > 0 ? (float) keywordFrequency / totalWords : 0;
//      similarityScores.put(classCode, similarity);
//    }
//    return similarityScores;
//  }
