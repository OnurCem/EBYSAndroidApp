package ocs.com.ebys;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Onur Cem on 2/1/2015.
 */
public class Transcript {
    private String header;
    private List<Course> courses;
    private String cGNO;
    private String GNO;

    public Transcript() {
        this.courses = new ArrayList<>();
        header = "";
        cGNO = "";
        GNO = "";
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public String getcGNO() {
        return cGNO;
    }

    public void setcGNO(String cGNO) {
        this.cGNO = cGNO;
        Course course = new Course();
        course.setName("BİRİKİMLİ GNO: " + cGNO);
        courses.add(course);
    }

    public String getGNO() {
        return GNO;
    }

    public void setGNO(String GNO) {
        this.GNO = GNO;
        Course course = new Course();
        course.setName("YARIYIL GNO: " + GNO);
        courses.add(course);
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }
}
