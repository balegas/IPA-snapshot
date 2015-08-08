package app;

import indigo.annotations.Assert;
import indigo.annotations.Invariant;

@Invariant("forall( Student : s, Course : c ) :- enrolledStudent( s, c) => existsStudent( s ) and existsCourse( c )")
@Invariant("(forall( Student : x, Student : y, Course : c ) :- group(x,y,c) => enrolledStudent( x, c) and enrolledStudent( y, c) and (x <> y)")
public interface ZCourseware {

	// @Exists("$0")
	// @Assert("existsStudent($0) = true")
	// void addStudent(Student s);

	// @NotExists("$0")
	@Assert("existsStudent($0) = false")
	void remStudent(Student s);

	// @Exists("$0")
	// void addCourse(Course c1);

	// @NotExists("$0")
	// void remCourse(Course c1);

	@Assert("enrolledStudent( $0, $1 ) = true")
	void enrollStudent(Student s, Course c);

	// @Assert("enrolledStudent( $0, $1 ) = false")
	// void disenrollStudent(Student s, Course c);

	// @Assert("group( $0, $1, $2 ) = true")
	// void createGroup(Student s, Student ss, Course c);

	class Student {
	}

	class Course {
	}

}
