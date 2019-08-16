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
