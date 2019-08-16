/**
-------------------------------------------------------------------

Copyright (c) 2014 SyncFree Consortium.  All Rights Reserved.

This file is provided to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License.  You may obtain
a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

-------------------------------------------------------------------
**/
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
