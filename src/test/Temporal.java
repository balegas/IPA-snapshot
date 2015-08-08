package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;
import indigo.annotations.True;

//@Invariant("forall( Course : c, Student : s ) :- active(c) => (participant(s, c) <=> enrolled(s, c))")
@Invariant("forall( Course : c, Student : s ) :-  (active(c) and enrolled(s, c)) => participant(s, c))")
@Invariant("forall( Student : s, Course : c ) :- enrolled(s, c) => existsStudent(s) and existsCourse(c)")
public interface Temporal {

	// @True("existsStudent($0)")
	// void addStudent(Student s);
	//
	// @False("existsStudent($0)")
	// void remStudent(Student s);
	//
	// @True("existsCourse($0)")
	// void addCourse(Course c);

	@False("existsCourse($0)")
	void remCourse(Course c);

	// @True("enrolled($0, $1)")
	// @False("participant($0, $1)")
	// void enrol(Student s, Course c);
	//
	// @False("enrolled($0, $1)")
	// void disenrol(Student s, Course c);

	@True("active($0)")
	void BEGINCOURSE(Course c);

	class Student {
	}

	class Course {
	}
}
