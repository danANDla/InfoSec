import java.io.File
import java.math.BigInteger

fun main(args: Array<String>) {
    val text = File(args[0]).readText().split("\n")
    val blocks_n = text[0].toInt()

    val N: BigInteger = BigInteger.valueOf(text[1].toLong())
    val e: BigInteger = BigInteger.valueOf(text[2].toLong())

    val verbose = false

    val secretD = hack(N,e, verbose)

    for(i in 0..<blocks_n){
        val C: BigInteger = BigInteger.valueOf(text[3 + i].toLong())

        if(verbose){
            println("--------------------------------")
            println(String.format("block [%2d] %d", i + 1, C.toLong()))
        }
        val msg = C.modPow(secretD, N)
        if(verbose)
            println(String.format("msg = %d\n--------------------------------", msg.toLong()))
        else
            println(msg.toLong())
    }

}


fun hack(N: BigInteger, e: BigInteger, verbose: Boolean): BigInteger {
    val p = fermaFactorize(N).second
    val q = N.divide(p)

    if(verbose)
    println(String.format("N Factorization: N = p*q =  %d * %d", p.toInt(), q.toInt()))

    val phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE))
    if(verbose)
    println(String.format("φ(N)=φ(p)*φ(q)=(p-1)(q-1): φ(N) = %d", phi.toLong()))

    val secret_d = e.modInverse(phi)
    if(verbose)
    println(String.format("d=invmod_phi(e): d = %d", secret_d.toLong()))

    return secret_d
}

fun fermaFactorize(N: BigInteger): Pair<Boolean, BigInteger> {
    var x = N.sqrt() + BigInteger.ONE

    var y = BigInteger.ZERO
    var r = ((x.multiply(x)).subtract(y.multiply(y))).subtract(N)

    while (true) {
        val cmp = r.compareTo(BigInteger.ZERO)
        if (cmp == 0){
            return if (x.compareTo(y) != 0) Pair(true, x.subtract(y)) else Pair(true, x.add(y))
        } else if(cmp > 0) {
            r = r.subtract(y).subtract(y).subtract(BigInteger.ONE)
            y = y.add(BigInteger.ONE)
        } else{
            r = r.add(x).add(x).add(BigInteger.ONE)
            x = x.add(BigInteger.ONE)
        }
    }
}