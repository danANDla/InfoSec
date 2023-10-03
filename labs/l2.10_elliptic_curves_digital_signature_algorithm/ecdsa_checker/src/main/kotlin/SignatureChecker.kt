class SignatureChecker(
    private val basePoint: EllipticCurvePoint,
    private val secretPoint: EllipticCurvePoint,
    private val primeNumber: Long,
    private val n: Long,
    private val e: Long,
    private val a: Long,
    private val b: Long,
    private val verbose: Boolean
) {
    fun checkSignature(signa: Pair<Long, Long>): Boolean {
        if (signa.first < 1 || signa.first >= n || signa.second < 1 || signa.second >= n) return false

        val v = invMod(signa.second, n)
        val u1 = (e * v) % n
        val u2 = (signa.first * v) % n

        val p1 = multiplyPoint(basePoint, u1)
        val p2 = multiplyPoint(secretPoint, u2)
        val resPoint = pointAddition(p1, p2, primeNumber, a)
        println(String.format("(%d,%d)", resPoint.x, resPoint.y))
//
        val checkMultiplying = multiplyPoint(EllipticCurvePoint(384, 475, false), 7)
        println(String.format("checkMultiplying=(%d,%d)", checkMultiplying.x, checkMultiplying.y))

        return resPoint.x % n == signa.first
    }

    private fun gcdex(
        n1: Long,
        n2: Long,
    ): Pair<Long, Pair<Long, Long>> {
        if (n1 == 0L) {
            return Pair(n2, Pair(0L, 1L))
        }
        val d = gcdex(n2 % n1, n1)
        val x1 = d.second.first
        val y1 = d.second.second
        return Pair(d.first, Pair(y1 - (n2 / n1) * x1, x1))
    }

    private fun invMod(
        n1: Long,
        n2: Long
    ): Long {
        val g = gcdex(n1, n2)
        if (g.first != 1L && g.first != -1L) throw Exception("No solution for Diophantine")
        return (g.second.first % n2 + n2) % n2
    }

    private fun multiplyPoint(point: EllipticCurvePoint, m: Long): EllipticCurvePoint {
        var p = EllipticCurvePoint(0, 0, true)
        var bits = Integer.toBinaryString(m.toInt())
        var i = bits.length
        while (i > 0) {
            p = pointAddition(p, p, primeNumber, a)
            if (bits[bits.length - i] == '1') {
                p = pointAddition(p, point, primeNumber, a)
            }
            i--
        }
        return p
    }

    fun pointAddition(
        p1: EllipticCurvePoint,
        p2: EllipticCurvePoint,
        primeNumber: Long,
        a: Long
    ): EllipticCurvePoint {
        if (p2.pointAtInfinity) {
            return p1
        }
        if (p1.pointAtInfinity) {
            return p2
        }
        try {
            var lambda = if (p1.isEq(p2)) {
                (3 * (p1.x) * (p1.x) + a) * invMod(2 * p1.y, primeNumber)
            } else {
                (p2.y - p1.y) * invMod(p2.x - p1.x, primeNumber)
            }
            val x3 = (lambda * lambda - p1.x - p2.x)

            return EllipticCurvePoint(
                Math.floorMod(x3 , primeNumber),
                Math.floorMod(lambda * (p1.x - x3) - p1.y, primeNumber),
                false
            )
        } catch (e: Exception) {
//            println(String.format("bam"))
            return EllipticCurvePoint(0, 0, true)
        }
    }
}