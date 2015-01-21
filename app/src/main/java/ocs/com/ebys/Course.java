package ocs.com.ebys;

import java.util.ArrayList;
import java.util.List;

public class Course {
	private String code;
	private String name;
	private String instructor;
	private String attendance;
	private List<Grade> grades;
	
	public Course() {
		grades = new ArrayList<Grade>();
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getInstructor() {
		return instructor;
	}
	
	public void setInstructor(String instructor) {
		this.instructor = instructor;
	}
	
	public String getAttendance() {
		return attendance;
	}
	
	public void setAttendance(String attendance) {
		this.attendance = attendance;
	}
	
	public List<Grade> getGrades() {
		return grades;
	}
	
	public void setGrades(List<Grade> grades) {
		this.grades = grades;
	}
	
	public void addGrade(Grade grade) {
		this.grades.add(grade);
	}
}
