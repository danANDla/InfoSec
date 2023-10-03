import java.io.File
import java.io.InputStream

fun main(args: Array<String>) {
    doMain(args)
}

fun doMain(args: Array<String>){

    val d = EllipticDecryptor("letters_points", EllipticCurvePoint(-1L, 1L, false), 51, 751, -1, false)
    val r = Regex("\\d+")

    val crypted = File(args[0]).readText()

    val linesList = crypted.split("}")
    linesList.forEach{
        if(it.isNotBlank()){
           d.decrypt(
                (r.findAll(it).elementAt(0).value).toLong(),
                (r.findAll(it).elementAt(1).value).toLong(),
                (r.findAll(it).elementAt(2).value).toLong(),
                (r.findAll(it).elementAt(3).value).toLong(),
            )
        }
    }

}

fun allPoints(){
    val d = EllipticDecryptor("letters_points", EllipticCurvePoint(-1L, 1L, false), 44, 751, -1, true)
    d.getPointMultiplier(EllipticCurvePoint(-10000,0, true))
}