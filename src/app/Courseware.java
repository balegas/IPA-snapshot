package app;

import indigo.annotations.Numeric;
import indigo.annotations.Decrements;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;

@Invariant("CourseEnrollments < 30")
@Invariant("CourseEnrollments > 0")
public interface Courseware {

	@Numeric
	final String CourseEnrollments = "CourseEnrollments";

	void addStudent(Student s);

	void remStudent(Student s);

	void addCourse(Course c);

	void remCourse(Course c);

	@Increments(CourseEnrollments)
	void enrollStudent(Student s, Course c);

	@Decrements(CourseEnrollments)
	void disenrollStudent(Student s, Course c);

	class Student {
	}

	class Course {
	}

}
