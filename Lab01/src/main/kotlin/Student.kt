class Student(name: String, age: Int) : Human(name, age) {
    private val courses: MutableList<CourseRecord> = mutableListOf()

    fun addCourse(course: CourseRecord) {
        courses.add(course)
    }

    fun getCourses(): List<CourseRecord> {
        return courses
    }

    fun weightedAverage(): Double {
        return if (courses.isEmpty()) 0.0 else courses.sumOf { it.getGrade() * it.getCredits() } / courses.sumOf { it.getCredits() }
    }

    fun weightedAverage(year: Int): Double {
        val filteredCourses = courses.filter { it.getYearCompleted() == year }
        return if (filteredCourses.isEmpty()) 0.0 else filteredCourses.sumOf { it.getGrade() * it.getCredits() } / filteredCourses.sumOf { it.getCredits() }
    }

    fun minMaxGrades(): Pair<Double, Double> {
        val grades = courses.map { it.getGrade() }
        return if (grades.isEmpty()) 0.0 to 0.0 else grades.min() to grades.max()
    }

    override fun toString(): String = "Student $name, $age years old"
}