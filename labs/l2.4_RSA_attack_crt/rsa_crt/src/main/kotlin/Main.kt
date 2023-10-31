import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext


fun main(args: Array<String>) {

    val text = File(args[0]).readText().split("\n")
    val blocks_n = text[0].toInt()

    val n1: BigInteger = BigInteger.valueOf(text[1].toLong())
    val n2: BigInteger = BigInteger.valueOf(text[2].toLong())
    val n3: BigInteger = BigInteger.valueOf(text[3].toLong())


    var offset: Int = 4
    val msg1: ArrayList<BigInteger> = ArrayList()
    val msg2: ArrayList<BigInteger> = ArrayList()
    val msg3: ArrayList<BigInteger> = ArrayList()

    for (i in 0..<blocks_n) {
        val e = text[offset + i].toLong()
        msg1.add(BigInteger.valueOf(e))
    }

    offset += blocks_n
    for (i in 0..<blocks_n) {
        val e = text[offset + i].toLong()
        msg2.add(BigInteger.valueOf(e))
    }

    offset += blocks_n
    for (i in 0..<blocks_n) {
        val e = text[offset + i].toLong()
        msg3.add(BigInteger.valueOf(e))
    }

    val r = getUncrypted(msg1[0], msg2[0], msg3[0], n1, n2, n3)

    for (i in 0..<blocks_n) {
        val r = getUncrypted(msg1[i], msg2[i], msg3[i], n1, n2, n3)
        println(r)

    }
}

fun getUncrypted(
    C1: BigInteger,
    C2: BigInteger,
    C3: BigInteger,
    N1: BigInteger,
    N2: BigInteger,
    N3: BigInteger
): BigInteger {

    val M0: BigInteger = N1.multiply(N2).multiply(N3)
    val m1: BigInteger = N2.multiply(N3)
    val m2: BigInteger = N1.multiply(N3)
    val m3: BigInteger = N1.multiply(N2)
    val n1: BigInteger = m1.modInverse(N1)
    val n2: BigInteger = m2.modInverse(N2)
    val n3: BigInteger = m3.modInverse(N3)


    val s1: BigInteger = C1.multiply(n1).multiply(m1)
    val s2: BigInteger = C2.multiply(n2).multiply(m2)
    val s3: BigInteger = C3.multiply(n3).multiply(m3)

    val s: BigInteger = (s1.add(s2).add(s3)).mod(M0)
    val res: BigDecimal = cuberoot(s.toBigDecimal())
    return res.toBigInteger()
}

fun cuberoot(b: BigDecimal?): BigDecimal {
    val mc = MathContext(40)
    var x = BigDecimal("1", mc)

    for (i in 0 until 1000) {
        x = x.subtract(
            x.pow(3, mc)
                .subtract(b, mc)
                .divide(
                    BigDecimal("3", mc).multiply(
                        x.pow(2, mc), mc
                    ), mc
                ), mc
        )
    }
    return x
}