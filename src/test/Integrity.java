package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( Student : s, Course : c ) :- enrolled(s, c) => existsStudent(s) and existsCourse(c)")
public interface Integrity {

	@True("existsStudent($0)")
	void addStudent(Student s);

	@False("existsStudent($0)")
	void remStudent(Student s);

	@True("existsCourse($0)")
	void addCourse(Course c);

	@False("existsCourse($0)")
	void remCourse(Course c);

	@True("enrolled($0, $1)")
	void enrol(Student s, Course c);

	@False("enrolled($0, $1)")
	void disenrol(Student s, Course c);

	class Student {
	}

	class Course {
	}
}
