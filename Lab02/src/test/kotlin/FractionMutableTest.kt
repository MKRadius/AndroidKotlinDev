import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class FractionMutableTest {
    @Test
    fun testCons() {
        val a = FractionMutable(2,4,-1)
        assert(a.toString() == "-1/2")
    }
    @Test
    fun testToString() {
        val a = FractionMutable(1,2,-1)
        assert(a.toString() == "-1/2")
    }
    @Test
    fun negate() {
        val a = FractionMutable(1,2, -1)
        a.negate()
        assert(a.toString() == "1/2")
    }
    @Test
    fun addPos1() {
        val a = FractionMutable(1,2)
        a.add(FractionMutable(1,3))
        assert(a.toString() == "5/6")
    }
    @Test
    fun addPosNeg1() {
        val a = FractionMutable(1,2)
        a.add(FractionMutable(1,3, -1))
        assert(a.toString() == "1/6")
    }
    @Test
    fun multPos() {
        val a = FractionMutable(1,2)
        a.mult(FractionMutable(1,3))
        assert(a.toString() == "1/6")
    }
    @Test
    fun multPosNeg1() {
        val a = FractionMutable(1,2)
        a.mult(FractionMutable(1,3, -1))
        assert(a.toString() == "-1/6")
    }
    @Test
    fun div() {
        val a = FractionMutable(8,3)
        a.div(FractionMutable(4,6))
        assert(a.toString() == "4/1")
    }
    @Test
    fun intPart() {
        val a = FractionMutable(8,3)
        assert(a.intPart() == 2)
    }
}