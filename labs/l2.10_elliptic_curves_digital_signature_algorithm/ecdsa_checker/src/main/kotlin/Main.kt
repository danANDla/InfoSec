import java.io.File
import kotlin.math.sign

fun main(args: Array<String>) {
    doMain(args)
}

fun doMain(args: Array<String>) {
    val signaText = File(args[0]).readText().split("\n")
    val e = signaText[0].toLong()
    val publicPointStr = signaText[1].split(",")
    val signaStr = signaText[2].split(",")

    val signa: Pair<Long, Long> = Pair(signaStr[0].toLong(), signaStr[1].toLong())
    val publicPoint = EllipticCurvePoint(publicPointStr[0].toLong(), publicPointStr[1].toLong(), false)
    println(String.format("e=%d, Q=(%d,%d)", e, publicPoint.x, publicPoint.y))


    val ecdsa = SignatureChecker(
        EllipticCurvePoint(562, 89, false),
        publicPoint,
        751,
        13,
        e,
        -1,
        1,
        true
    )

    var ans = "incorrect"
    if(ecdsa.checkSignature(signa)) ans = "CORRECT"
    println(String.format("signature (%d,%d) is %s", signa.first, signa.second, ans))
}