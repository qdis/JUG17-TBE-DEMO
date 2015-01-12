package org.tbe.jug.demo.showcase;

import java.util.List;

import org.tbe.jug.demo.annotations.EliteSchool;

@EliteSchool
public class School extends Institution implements Comparable<School> {

	private int teacherCount;
	private List<Student> students;

	public int getTeacherCount() {
		return teacherCount;
	}

	public void setTeacherCount(int teacherCount) {
		this.teacherCount = teacherCount;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public int compareTo(School o) {
		return getName().compareTo(o.getName());
	}
}
