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

        /**
         * Вот тут уже можете начинать свою реализацию боевого кода
         */

        return emptyList()
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
}
