class EllipticAlphabet {
    var alphabet: HashMap<Pair<Int, Int>, String> = HashMap()

    fun readAlphabetFromFile(filename: String) {
        val fileContent = this::class.java.getResource(filename)?.readText()

        val number_regex = Regex("\\d+")
        val letter_regex = Regex("[а-я]")

        fileContent?.split("\n")?.forEach {
            val numbers = number_regex.findAll(it)
            letter_regex.findAll(it).elementAt(0).value
            alphabet[Pair(numbers.elementAt(0).value.toInt(), numbers.elementAt(1).value.toInt())] =
                letter_regex.findAll(it).elementAt(0).value
        }
    }

    fun getLetterFromCode(code: Pair<Int, Int>): String {
        return alphabet[code].orEmpty()
    }
}