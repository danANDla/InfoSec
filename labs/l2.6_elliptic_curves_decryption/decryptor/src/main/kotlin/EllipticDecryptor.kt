import java.math.BigInteger

class EllipticDecryptor(
    alphabetFile: String,
    private val basePoint: EllipticCurvePoint,
    private val secretKey: Long,
    private val primeNumber: Long,
    private val a: Long,
    private val verbose: Boolean
) {
    val alphabet = EllipticAlphabet()

    init {
        alphabet.readAlphabetFromFile(alphabetFile)
    }

    fun getPointMultiplier(point: EllipticCurvePoint): Long {
        var p = basePoint
        var k: Long = 2
        while (!p.isEq(point) && k <= primeNumber) {
            p = pointAddition(basePoint, p, primeNumber, a)
            if (verbose) println(String.format("[%d] (%d,%d)", k, p.x, p.y))
            k++
        }
        return if (p.isEq(point)) k - 1 else k
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

    private fun subPoints(p1: EllipticCurvePoint, p2: EllipticCurvePoint): EllipticCurvePoint {
        return pointSub(p1, p2, primeNumber, a)
    }

    fun decrypt(x1: Long, y1: Long, x2: Long, y2: Long) {
        val p = EllipticCurvePoint(x1, y1, false)
        val q = EllipticCurvePoint(x2, y2, false)

        val np = multiplyPoint(p, secretKey)
        if (verbose) println(
            String.format(
                "%d * (%d,%d) = (%d,%d), isPointAtInfinity=%s",
                secretKey,
                p.x,
                p.y,
                np.x,
                np.y,
                if (np.pointAtInfinity) "true" else "false"
            )
        )
        val s = subPoints(q, np)
        if (verbose) println(
            String.format(
                "(%d,%d) - (%d,%d) = (%d,%d), isPointAtInfinity=%s",
                q.x,
                q.y,
                np.x,
                np.y,
                s.x,
                s.y,
                if (s.pointAtInfinity) "true" else "false"
            )
        )
        if (verbose) {
            println(
                String.format(
                    "Result point: (%d,%d), isInfinityPoint=%s",
                    s.x,
                    s.y,
                    if (s.pointAtInfinity) "true" else "false"
                )
            )
            println("-------------------------")
        }
        println(String.format("(%3d,%3d) | %s", s.x, s.y, alphabet.getLetterFromCode(Pair(s.x.toInt(), s.y.toInt()))))
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