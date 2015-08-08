package app;

import indigo.annotations.Numeric;
import indigo.annotations.Decrements;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;

//@Invariant("CourseEnrollments < 30")
@Invariant("forall(Course : c) :- ( CourseEnrollments(c) > 0 ) and ( CourseEnrollments(c) < 30 )")
// @Invariant("CourseEnrollments > 0")
// @Invariant("TotalStudents >= 0")
// @Invariant("A + B - C < 30")
public interface XCourseware {

	@Numeric
	final String CourseEnrollments = "CourseEnrollments( Course : c )";

	@Numeric
	final String TotalStudents = "TotalStudents";

	// @Exists("$0")
	// @Increments(TotalStudents)
	// void addStudent(Student s);

	//
	// @NotExists("$0")
	// void remStudent(Student s);
	//
	// @Exists("$0")
	// void addCourse(Course c);
	//
	// @NotExists("$0")
	// // void remCourse(Course c);
	//

	@Increments(CourseEnrollments)
	void enrollStudent(Student s1, Course c1);

	@Decrements(CourseEnrollments)
	void disenrollStudent(Student s1, Course c1);

	// @Increments(A)
	// @Increments(B)
	// @Decrements(C)
	// void a();
	//
	// @Increments(A)
	// void c();
	//
	// @Increments(B)
	// @Decrements(C)
	// void d();
	//
	// @Increments(A)
	// void e();
	//
	// @Increments(B)
	// @Decrements(C)
	// void f();
	//
	// @Decrements(C)
	// @Increments(A)
	// void g();
	//
	// @Increments(B)
	// void h();

	class Student {
	}

	class Course {
	}

}
