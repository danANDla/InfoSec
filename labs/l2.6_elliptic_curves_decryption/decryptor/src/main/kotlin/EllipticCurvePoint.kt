data class EllipticCurvePoint (val x: Long, var y:Long, val pointAtInfinity: Boolean){
    fun isEq(p: EllipticCurvePoint): Boolean{
        return p.x == x && p.y == y
    }
}