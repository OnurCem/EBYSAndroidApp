package ocs.com.ebys;

import java.util.ArrayList;
import java.util.List;

public class Course {
	private String code;
	private String name;
    private String credit;
	private String instructor;
	private String attendance;
	private List<Grade> grades;
    private String letterGrade;

	public Course() {
		grades = new ArrayList<Grade>();
        code = "";
        name = "";
        credit = "";
        instructor = "";
        attendance = "";
        letterGrade = "";
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

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getInstructor() {
		return instructor;
	}
	
	public void setInstructor(String instructor) {
		this.instructor = instructor;
        addGrade(new Grade("Öğretim Üyesi", instructor));
	}
	
	public String getAttendance() {
		return attendance;
	}
	
	public void setAttendance(String attendance) {
		this.attendance = attendance;
        addGrade(new Grade("Devam Durumu", attendance));
	}
	
	public List<Grade> getGrades() {
		return grades;
	}
	
	public void setGrades(List<Grade> grades) {
		this.grades = grades;
	}

    public String getLetterGrade() {
        return letterGrade;
    }

    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }
	
	public void addGrade(Grade grade) {
		this.grades.add(grade);
	}
}
