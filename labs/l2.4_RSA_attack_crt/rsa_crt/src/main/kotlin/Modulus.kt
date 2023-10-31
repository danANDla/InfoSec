import java.math.BigInteger

class Modulus {
    companion object {
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