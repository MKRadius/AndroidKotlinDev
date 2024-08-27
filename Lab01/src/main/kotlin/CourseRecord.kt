class CourseRecord(
    private var name: String,
    private var yearCompleted: Int,
    private var credits: Int,
    private var grade: Double
) {
    fun getName(): String = name
    fun setName(name: String) { this.name = name }

    fun getYearCompleted(): Int = yearCompleted
    fun setYearCompleted(yearCompleted: Int) { this.yearCompleted = yearCompleted }

    fun getCredits(): Int = credits
    fun setCredits(credits: Int) { this.credits = credits }

    fun getGrade(): Double = grade
    fun setGrade(grade: Double) { this.grade = grade }

    override fun toString(): String {
        return "$name \n" +
                "   Year completed: $yearCompleted" +
                "   Credits: $credits" +
                "   Grade: $grade"
    }
}