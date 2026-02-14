#SQL

#### Sample Table 


| students_id     | name            | age |
| --------------- | --------------- | --- |
| 1               | Alice           | 22  |
| 2               | Bob             | 24  |
| 3               | Charlie         | 22  |
| **students_id** | **course_id**   |     |
| 1               | C1              |     |
| 2               | C2              |     |
| 1               | C2              |     |
| 3               | C1              |     |
| **course_id**   | **course_name** |     |
| C1              | Databases       |     |
| C2              | Networks        |     |
*table 1: Students
table 2: Enrollments
table 3: courses*

**Select students aged 22:**
σ_{age = 22}(Students)
**Get names of students only:**
π_{name}(Students)
**Get names of students and the courses they are enroleld in:**
π_{name, course_name}(
	(Students ⨝ Enrollments) ⨝ Courses
)
**Find students who are not enrolled in a course:**
π_{students_id}(Students) - π_{students_id}(Enrollments)
**Rename an attribute:**
ρ_{StudentsRenamed(students_id, student_name, age)}(Students)

[askhseis](https://exams-iee.the.ihu.gr/pluginfile.php/27061/mod_resource/content/1/%CE%91%CF%83%CE%BA%CE%AE%CF%83%CE%B5%CE%B9%CF%82%20%CE%A3%CF%87%CE%B5%CF%83%CE%B9%CE%B1%CE%BA%CE%AE%CF%82%20%CE%86%CE%BB%CE%B3%CE%B5%CE%B2%CF%81%CE%B1%CF%82.pdf)
[lyseis askhseon](https://exams-iee.the.ihu.gr/pluginfile.php/34223/mod_resource/content/1/%CE%9B%CF%8D%CF%83%CE%B5%CE%B9%CF%82%20%CE%B1%CF%83%CE%BA%CE%AE%CF%83%CE%B5%CF%89%CE%BD%20%CE%A3%CF%87%CE%B5%CF%83%CE%B9%CE%B1%CE%BA%CE%AE%CF%82%20%CE%86%CE%BB%CE%B3%CE%B5%CE%B2%CF%81%CE%B1%CF%82.txt)
