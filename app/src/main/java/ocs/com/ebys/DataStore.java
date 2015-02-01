package ocs.com.ebys;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Onur Cem on 2/1/2015.
 */
public class DataStore {
    private static DataStore instance;
    private List<Course> courses;
    private HashMap<Course, List<Grade>> grades;
    private List<Transcript> transcripts;
    private HashMap<Transcript, List<Course>> transcriptCourses;

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public HashMap<Course, List<Grade>> getGrades() {
        return grades;
    }

    public void saveGrades(HashMap<Course, List<Grade>> grades) {
        this.grades = grades;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void saveCourses(List<Course> courses) {
        this.courses = courses;
    }

    public HashMap<Transcript, List<Course>> getTranscriptCourses() {
        return transcriptCourses;
    }

    public void saveTranscriptCourses(HashMap<Transcript, List<Course>> transcriptCourses) {
        this.transcriptCourses = transcriptCourses;
    }

    public List<Transcript> getTranscripts() {
        return transcripts;
    }

    public void saveTranscripts(List<Transcript> transcripts) {
        this.transcripts = transcripts;
    }

    public void deleteData() {
        courses = null;
        grades = null;
    }
}
