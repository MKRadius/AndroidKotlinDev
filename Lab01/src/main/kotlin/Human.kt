open class Human(var name: String, var age: Int) {
    fun getOlder() = age++
    override fun toString(): String = "$name, $age years old"
}