import java.math.BigInteger

class SignatureChecker(
    private val basePoint: EllipticCurvePoint,
    private val publicPoint: EllipticCurvePoint,
    private val primeNumber: Long,
    private val curveFactor: Long,
    private val e: Long,
    private val a: Long,
    private val b: Long,
    private val verbose: Boolean
) {
    fun checkSignature(signa: Pair<Long, Long>): Boolean {
        if(verbose) println("\n---------------------------------")
        if (signa.first < 1 || signa.first >= curveFactor || signa.second < 1 || signa.second >= curveFactor) return false

        val v = invMod(signa.second, curveFactor)
        val u1 = e % curveFactor * invMod(signa.second, curveFactor)
        val u2 = signa.first * invMod(signa.second, curveFactor)

        val p1 = multiplyPoint(basePoint, u1)
        if (verbose)
            println(String.format("(%d,%d) * %d = (%d, %d)", basePoint.x, basePoint.y, u1, p1.x, p1.y))

        val p2 = multiplyPoint(publicPoint, u2)
        if (verbose)
            println(String.format("(%d,%d) * %d = (%d, %d)", publicPoint.x, publicPoint.y, u2, p2.x, p2.y))

        val resPoint = pointAddition(p2, p1, primeNumber, a)
        if (verbose)
            println(String.format("(%d,%d) + (%d,%d) = (%d,%d)", p1.x, p1.y, p2.x, p2.y, resPoint.x, resPoint.y))

        if(verbose) println("---------------------------------\n")
        return resPoint.x % curveFactor == signa.first
    }

    private fun multiplyPoint(point: EllipticCurvePoint, m: Long): EllipticCurvePoint {
        var p = EllipticCurvePoint(0, 0, true)
        var bits = Integer.toBinaryString(m.toInt())
        var i = bits.length
        while (i > 0) {
            p = pointAddition(p, p, primeNumber, a)
            if (bits[bits.length - i] == '1') {
                p = pointAddition(point, p, primeNumber, a)
            }
            i--
        }
        return p
    }

    companion object {
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
            val lambda: Long
            if (mod(p1.x - p2.x, primeNumber) == 0L) {
                if (mod(p1.y - p2.y, primeNumber) == 0L) {
                    lambda = (3 * (p1.x) * (p1.x) + a) * invMod(2 * p1.y, primeNumber)
                } else {
                    return EllipticCurvePoint(0, 0, true)
                }
            } else {
                lambda = mod((p2.y - p1.y), primeNumber) * invMod(p2.x - p1.x, primeNumber)
            }

            val x3 = mod(lambda * lambda - p1.x - p2.x, primeNumber)
            val y3 = mod((lambda * (p1.x - x3) - p1.y), primeNumber)
            return EllipticCurvePoint(x3, y3, false)
        }
        fun pointSub(
            p1: EllipticCurvePoint,
            p2: EllipticCurvePoint,
            primeNumber: Long,
            a: Long
        ): EllipticCurvePoint{
            if (p1.isEq(p2)) return EllipticCurvePoint(0, 0, true)
            return pointAddition(EllipticCurvePoint(p2.x, -p2.y, false), p1, primeNumber, a)
        }

        private fun mod(dividend: Long, divisor: Long): Long {
            var nB1: BigInteger = BigInteger.valueOf(dividend)
            var nB2: BigInteger = BigInteger.valueOf(divisor)
            return nB1.mod(nB2).longValueExact()
        }
        private fun invMod(
            n1: Long,
            n2: Long
        ): Long {
            var nB1: BigInteger = BigInteger.valueOf(n1)
            var nB2: BigInteger = BigInteger.valueOf(n2)
            return nB1.modInverse(nB2).longValueExact()
        }
    }
}