package app;

import indigo.annotations.Assert;
import indigo.annotations.Numeric;
import indigo.annotations.Decrements;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;

@Invariant("forall( Student : s, Course : c ) :- enrolledStudent( s, c) => ( existsStudent( s ) and existsCourse( c ) and (CourseEnrollments(c) < 30))")
public interface YCourseware {

	@Numeric
	final String CourseEnrollments = "CourseEnrollments( Course : c )";

	@Assert("existsStudent($0) = true")
	void addStudent(Student s);

	@Assert("existsStudent($0) = false")
	void remStudent(Student s);

	@Assert("existsCourse($0) = true")
	void addCourse(Course c);

	@Assert("existsCourse($0) = false")
	void remCourse(Course c);

	@Increments(CourseEnrollments)
	@Assert("enrolledStudent( $0, $1 ) = true")
	void enrollStudent(Student s, Course c);

	@Decrements(CourseEnrollments)
	@Assert("enrolledStudent( $0, $1 ) = false")
	void disenrollStudent(Student s, Course c);

	// @Assert("group( $0, $1, $2 ) = true")
	// void createGroup(Student s, Student ss, Course c);

	class Student {
	}

	class Course {
	}

}
