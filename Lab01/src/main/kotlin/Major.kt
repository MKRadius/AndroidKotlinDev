class Major(
    private var name: String,
    private var students: MutableList<Student> = mutableListOf())
{
    fun getName(): String = name
    fun setName(name: String) { this.name = name }

    fun addStudent(student: Student) { students.add(student) }

    fun stats(): Triple<Double, Double, Double> {
        val gradeStats: List<Double> = students.map { it.weightedAverage() }
        return if (gradeStats.isEmpty()) Triple(0.0, 0.0, 0.0) else Triple(gradeStats.min(), gradeStats.max(), gradeStats.average())
    }

    fun stats(courseName: String): Triple<Double, Double, Double> {
        val courseNameGradeList: List<Double> = students
            .flatMap { student ->
                student.getCourses()
                    .filter { it.getName() == courseName }
                    .map { it.getGrade() }
            }

        return if (courseNameGradeList.isEmpty()) Triple(0.0, 0.0, 0.0) else Triple(courseNameGradeList.min(), courseNameGradeList.max(), courseNameGradeList.average())
    }
}