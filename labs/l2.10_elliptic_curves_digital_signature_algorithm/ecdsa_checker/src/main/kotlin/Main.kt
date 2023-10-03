fun main(args: Array<String>) {
    doMain(args)
}

fun doMain(args: Array<String>){
    val ecdsa = SignatureChecker(
        EllipticCurvePoint(384,475, false),
        EllipticCurvePoint(384,276,false),
        751,
        13,
        12,
        -1,
        1,
        false
    )
    ecdsa.checkSignature(Pair(11,9))
}