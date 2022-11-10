package codes.spectrum.conf2022

import codes.spectrum.conf2022.doc_type.DocType
import codes.spectrum.conf2022.input.IDocParser
import codes.spectrum.conf2022.output.ExtractedDocument
import kotlin.random.Random

/**
 * Вот собственно и класс, который как участник вы должны реализовать
 *
 * контракт один - пустой конструктор и реализация [IDocParser]
 */
class UserDocParser : IDocParser {
    override fun parse(input: String): List<ExtractedDocument> {

        if (input == "5050 909 012") {
            println()
        }

        /**
         * Это пример чтобы пройти совсем первый базовый тест, хардкод, но понятно API,
         * просто посмотрите preparedSampleTests для примера
         */
        if (input.startsWith("BASE_SAMPLE1.")) {
            return preparedSampleTests(input)
        }
        /**
         * Это раздел квалификации - все инпуты начинаются с `@ `
         * призываем Вас НЕ хардкодить!!! хардкод проверим просто на ревью по этой функции,
         * надо честно реализовать спеки по DocType.T1 и DocType.T2
         * мы их будем проверять секретными тестами!!!
         */
        if (input.startsWith("@ ")) {
            return qualificationTests(input)
        }

        fun String.normalizeSpaces(): String {
            return replace(" ", "")
        }

        val inputWithoutSpaces = input.normalizeSpaces()

        fun String.myNormalizeSnils(): String {
            return replace(" ", "").replace("-", "")
        }

        fun String.normalizeResultSnils(): String {
            return "${substring(0..2)}-${substring(3..5)}-${substring(6..8)}-${substring(9..10)}"
        }

        return buildList {
            if (input.matches(DocType.INN_UL.normaliseRegex)) {

                fun controlSumIsValid(inn10: String): Boolean {
                    return inn10
                        .substring(0..8)
                        .mapIndexed { index, char ->
                            (char - '0') * INN_UL_CONTROL_FACTORS[index]
                        }
                        .sum() % 11 % 10 == (input.last() - '0')
                }

                val isValid = input
                    .let {
                        !it.startsWith("00") // остальные коды регионов - валидные или потенциально валидные
                                && controlSumIsValid(it)
                    }

                add(
                    element = ExtractedDocument(
                        docType = DocType.INN_UL,
                        value = input,
                        isValidSetup = true,
                        isValid = isValid,
                    ),
                )
            }

            if (input.matches(DocType.INN_FL.normaliseRegex)) {

                fun controlSumIsValid(inn12: String): Boolean {
                    return inn12
                        .substring(0..9)
                        .mapIndexed { index, char ->
                            (char - '0') * INN_FL_FIRST_CONTROL_FACTORS[index]
                        }
                        .sum() % 11 % 10 == (input[10] - '0')
                            &&
                            inn12
                                .substring(0..10)
                                .mapIndexed { index, char ->
                                    (char - '0') * INN_FL_SECOND_CONTROL_FACTORS[index]
                                }
                                .sum() % 11 % 10 == (input[11] - '0')
                }

                val isValid = input
                    .let {
                        !it.startsWith("00") // остальные коды регионов - валидные или потенциально валидные
                                && controlSumIsValid(it)
                    }

                add(
                    element = ExtractedDocument(
                        docType = DocType.INN_FL,
                        value = input,
                        isValidSetup = true,
                        isValid = isValid,
                    ),
                )
            }

            if (inputWithoutSpaces.matches(DocType.PASSPORT_RF.normaliseRegex)) {
                add(
                    element = ExtractedDocument(
                        docType = DocType.PASSPORT_RF,
                        value = inputWithoutSpaces,
                        isValidSetup = true,
                        isValid = true,
                    ),
                )
            }

            if (inputWithoutSpaces.matches(DocType.DRIVER_LICENSE.normaliseRegex)) {
                add(
                    element = ExtractedDocument(
                        docType = DocType.DRIVER_LICENSE,
                        value = inputWithoutSpaces,
                        isValidSetup = true,
                        isValid = true,
                    ),
                )
            }

            fun String.normalizeGrz(): String {
                return this
                    .uppercase()
                    .replace('A', 'А')
                    .replace('B', 'В')
                    .replace('E', 'Е')
                    .replace('K', 'К')
                    .replace('M', 'М')
                    .replace('H', 'Н')
                    .replace('O', 'О')
                    .replace('P', 'Р')
                    .replace('C', 'С')
                    .replace('T', 'Т')
                    .replace('Y', 'У')
                    .replace('X', 'Х')
            }

            if (inputWithoutSpaces.normalizeGrz().matches(DocType.GRZ.normaliseRegex)) {
                add(
                    element = ExtractedDocument(
                        docType = DocType.GRZ,
                        value = inputWithoutSpaces.normalizeGrz(),
                        isValidSetup = true,
                        isValid = inputWithoutSpaces
                            .let { input ->
                                input.substring(1..3).any { it != '0' }
                                        && input.substring(6..7).any { it != '0' }
                            },
                    ),
                )
            }

            if (inputWithoutSpaces.matches(DocType.VIN.normaliseRegex)) {
                add(
                    element = ExtractedDocument(
                        docType = DocType.VIN,
                        value = inputWithoutSpaces,
                        isValidSetup = true,
                        isValid = inputWithoutSpaces
                            .let { input ->
                                input.none { it == 'I' || it == 'O' || it == 'Q' }
                            },
                    ),
                )
            }

            if (inputWithoutSpaces.matches(DocType.OGRN.normaliseRegex)) {
                add(
                    element = ExtractedDocument(
                        docType = DocType.OGRN,
                        value = inputWithoutSpaces,
                        isValidSetup = true,
                        isValid = inputWithoutSpaces
                            .let { input ->
                                (input.substring(0..11).toLong() % 11 % 10).toInt() == (input[12] - '0')
                            },
                    ),
                )
            }

            if (inputWithoutSpaces.matches(DocType.OGRNIP.normaliseRegex)) {
                add(
                    element = ExtractedDocument(
                        docType = DocType.OGRNIP,
                        value = inputWithoutSpaces,
                        isValidSetup = true,
                        isValid = inputWithoutSpaces
                            .let { input ->
                                (input.substring(0..13).toLong() % 13 % 10).toInt() == (input[14] - '0')
                            },
                    ),
                )
            }

            val myNormalizedSnils = input.myNormalizeSnils()

            if (myNormalizedSnils.matches(SNILS_WITHOUT_SEPARATORS_REGEX)) {
                add(
                    element = ExtractedDocument(
                        docType = DocType.SNILS,
                        value = myNormalizedSnils.normalizeResultSnils(),
                        isValidSetup = true,
                        isValid = myNormalizedSnils
                            .let { input ->
                                input
                                    .substring(0..8)
                                    .mapIndexed { index, char ->
                                        (char - '0') * SNILS_CONTROL_FACTORS[index]
                                    }
                                    .sum()
                                    .let { sum ->
                                        when {
                                            sum < 100 -> {
                                                sum
                                            }
                                            sum == 100 -> {
                                                0
                                            }
                                            else -> {
                                                (sum % 101).let {
                                                    when (it) {
                                                        100 -> {
                                                            0
                                                        }
                                                        else -> {
                                                            it
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                            } == myNormalizedSnils.substring(9..10).toInt(),
                    ),
                )
            }
        }.let {
            it.ifEmpty {
                listOf(
                    element = ExtractedDocument(
                        docType = DocType.NOT_FOUND,
                    ),
                )
            }
        }
    }

    private fun qualificationTests(input: String): List<ExtractedDocument> {
        //TODO: вот тут надо пройти квалификацию по тестам из base.csv, которые начинаются на `@ BT...`

        val prefix = "@ "
        val value = input.removePrefix(prefix)
        val bttGarbageRegex = """[_-]""".toRegex()

        fun String.normalizeAsBtt(): String {
            return bttGarbageRegex.replace(this, "")
        }

        val candidates = buildList {
            val bttCandidate = value.normalizeAsBtt()

            if (bttCandidate.matches(DocType.T1.normaliseRegex)) {
                val isValid = bttCandidate
                    .let {
                        (it.length == 9) xor (it[4] == '5' && it[7] == '7')
                    }

                add(
                    element = ExtractedDocument(
                        docType = DocType.T1,
                        value = bttCandidate,
                        isValidSetup = true,
                        isValid = isValid,
                    ),
                )
            }

            if (bttCandidate.matches(DocType.T2.normaliseRegex)) {
                val isValid = bttCandidate
                    .substring(4..7)
                    .any {
                        it == '5'
                    }

                add(
                    element = ExtractedDocument(
                        docType = DocType.T2,
                        value = bttCandidate,
                        isValidSetup = true,
                        isValid = isValid,
                    ),
                )
            }
        }

        return candidates.sortedByDescending { it.isValid }
    }

    private fun preparedSampleTests(input: String): List<ExtractedDocument> {
        return when (input.split("BASE_SAMPLE1.")[1]) {
            "1" -> return listOf(ExtractedDocument(DocType.NOT_FOUND))
            "2" -> return listOf(
                // рандомы демонстрируют, что при условии INN_FL, PASSPORT_RF - проверяются только типы
                ExtractedDocument(
                    DocType.INN_FL,
                    isValidSetup = Random.nextBoolean(),
                    isValid = Random.nextBoolean(),
                    value = Random.nextInt().toString()
                ),
                ExtractedDocument(
                    DocType.PASSPORT_RF,
                    isValidSetup = Random.nextBoolean(),
                    isValid = Random.nextBoolean(),
                    value = Random.nextInt().toString()
                )
            )

            "3" -> return listOf(
                ExtractedDocument(
                    DocType.GRZ,
                    isValidSetup = true,
                    isValid = true,
                    value = Random.nextInt().toString()
                )
            )

            "4" -> return listOf(ExtractedDocument(DocType.INN_UL, value = "3456709873"))
            else -> emptyList()
        }
    }

    companion object {
        private val INN_UL_CONTROL_FACTORS = listOf(
            2, 4, 10, 3, 5, 9, 4, 6, 8,
        )

        private val SNILS_CONTROL_FACTORS = listOf(
            9, 8, 7, 6, 5, 4, 3, 2, 1,
        )

        private val SNILS_WITHOUT_SEPARATORS_REGEX = """^\d{11}$""".toRegex()

        private val INN_FL_FIRST_CONTROL_FACTORS = listOf(
            7, 2, 4, 10, 3, 5, 9, 4, 6, 8,
        )

        private val INN_FL_SECOND_CONTROL_FACTORS = listOf(
            3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8,
        )
    }
}
