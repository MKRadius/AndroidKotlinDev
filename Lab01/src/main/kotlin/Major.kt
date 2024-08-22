class Major(
    private var name: String,
    private var students: MutableList<Student> = mutableListOf())
{
    fun getName(): String = name
    fun setName(name: String) { this.name = name }

    fun addStudent(student: Student) { students.add(student) }

    fun stats(): Triple<Double, Double, Double> {
        val minMaxPairs: List<Pair<Double, Double>> = students.map { it.minMaxGrades() }
        val averages: List<Double> = students.map { it.weightedAverage()}
        return if(students.isEmpty()) Triple(0.0, 0.0, 0.0) else Triple(minMaxPairs.minOf { it.first }, minMaxPairs.maxOf { it.second }, averages.average())
    }

    fun stats(courseName: String): Triple<Double, Double, Double> {
        val coursesGrade: List<Double> = students
            .flatMap { it.getCourses() }
            .filter { it.getName() == courseName }
            .map { it.getGrade()}

        return Triple(coursesGrade.min(), coursesGrade.max(), coursesGrade.average())
    }
}