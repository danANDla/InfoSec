class EllipticDecryptor(
    private val alphabetFile: String,
    private val basePoint: EllipticCurvePoint,
    private val secretKey: Long,
    private val primeNumber: Long,
    private val a: Long,
    private val verbose: Boolean
) {
    val alphabet = EllipticAlphabet()

    init {
        alphabet.readAlphabetFromFile("letters_points")
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
        var i = m - 1
        var p = point
        while (i > 0) {
            i--
            p = pointAddition(point, p, primeNumber, a)
        }
        return p
    }

    private fun subPoints(p1: EllipticCurvePoint, p2: EllipticCurvePoint): EllipticCurvePoint {
        val points =  pointSub(p1, p2, primeNumber, a)
        if(alphabet.getLetterFromCode(Pair(points.first.x.toInt(), points.first.y.toInt())) != ""){
            return points.first
        }
        return points.second
    }

    fun decrypt(x1: Long, y1: Long, x2: Long, y2: Long) {
        val p = EllipticCurvePoint(x1, y1, false)
        val q = EllipticCurvePoint(x2, y2, false)
        val m = getPointMultiplier(p)
        if (verbose) println(String.format("k=%d", m))

        val np = multiplyPoint(basePoint, secretKey * m)
        if (verbose) println(
            String.format(
                "%d * (%d,%d) = (%d,%d), isPointAtInfinity=%s",
                secretKey * m,
                0,
                1,
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
        println(String.format("(%3d,%3d) | %s", s.x,s.y,alphabet.getLetterFromCode(Pair(s.x.toInt(), s.y.toInt()))))
    }

    companion object {
        fun gcd(
            n1: Long,
            n2: Long
        ): Long {
            var a = n1
            var b = n2
            while (b > 0) {
                a %= b
                a = b.also { b = a }
            }
            return a
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

        fun pointAddition(
            p1: EllipticCurvePoint,
            p2: EllipticCurvePoint,
            primeNumber: Long,
            a: Long
        ): EllipticCurvePoint {
            if (p2.pointAtInfinity) return p1
            try {
                var lambda = if (p1.isEq(p2)) {
                    (3 * (p1.x) * (p1.x) + a) * invMod(2 * p1.y, primeNumber)
                } else {
                    (p2.y - p1.y) * invMod(p2.x - p1.x, primeNumber)
                }
                lambda = Math.floorMod(lambda, primeNumber)
                val x3 = Math.floorMod((lambda * lambda - p1.x - p2.x), primeNumber)

                return EllipticCurvePoint(
                    x3,
                    Math.floorMod((lambda * (p1.x - x3) - p1.y), primeNumber),
                    false
                )
            } catch (e: Exception) {
                return EllipticCurvePoint(0, 0, true)
            }
        }

        fun pointSub(
            p1: EllipticCurvePoint,
            p2: EllipticCurvePoint,
            primeNumber: Long,
            a: Long
        ): Pair<EllipticCurvePoint, EllipticCurvePoint> {
            if (p1.isEq(p2)) return Pair(EllipticCurvePoint(0, 0, true), EllipticCurvePoint(0, 0, true))
            return Pair(
                pointAddition(EllipticCurvePoint(p2.x, -p2.y, false), p1, primeNumber, a),
                pointAddition(p1, EllipticCurvePoint(p2.x, -p2.y, false), primeNumber, a)
            )
        }
    }
}