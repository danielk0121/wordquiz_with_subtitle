package danielk.wordquiz.hello

import org.junit.jupiter.api.Test
import java.io.File
import java.nio.charset.Charset

class FileReadTest {

    @Test
    fun `상대경로에서 파일 읽기`() {
        // 프로젝트 루트 기준 경로 (src/main/resources 폴더 안에 파일이 있는 경우)
        val file = File("src/test/resources/sample.file")

        if (!file.exists()) {
            println("파일을 찾을 수 없습니다: ${file.absolutePath}")
            return
        }

        val content = file.readText(Charset.forName("UTF-8"))

        println("== file read start, ${file.absolutePath}")
        println(content.take(20))
        println("== file read end")
    }

    @Test
    fun `클래스패스에서 파일 읽기`() {
        // 파일명 앞에 /를 붙여 리소스를 가져옵니다.
        val resourceName = "/sample.file"
        val inputStream = this.javaClass.getResourceAsStream(resourceName)

        if (inputStream == null) {
            println("리소스를 찾을 수 없습니다.")
            return
        }

        val content = inputStream.bufferedReader(Charset.forName("UTF-8")).use { it.readText() }

        println("== file read start, $resourceName")
        println(content.take(20))
        println("== file read end")
    }

    @Test
    fun `smi 파일 읽기 테스트`() {
        val localPath = "/Users/user/ws/smi/iron.man.3.2013.01.kor.smi"
        val file = File(localPath)

        if(!file.exists()) {
            println("file not found")
            return
        }

        val content = file.readText(Charset.forName("UTF-8"))
        println(content.take(500))
    }

    @Test
    fun `SMI 파일 파싱 테스트`() {
        val koFilePath = "/Users/user/ws/smi/iron.man.3.2013.01.kor.smi"
        val enFilePath = "/Users/user/ws/smi/iron.man.3.2013.02.eng.smi"
        val koContent = File(koFilePath).readText(Charset.forName("UTF-8"))
        val enContent = File(enFilePath).readText(Charset.forName("UTF-8"))

        val koList = parseContent(koContent)
        val enList = parseContent(enContent)

        // 시간 슬롯 생성
        val allTimeSlots = (koList.map { it.first } + enList.map { it.first }).distinct().sorted()

        // 시간 슬롯 기준으로 합체
        val koMap = koList.toMap()
        val enMap = enList.toMap()
        val mergedList = allTimeSlots.map { time ->
            SubtitleDouble(
                    startTime = time,
                    koText = koMap[time] ?: "",
                    enText = enMap[time] ?: ""
            )
        }

        // 출력
        mergedList.take(50).forEachIndexed { idx, item ->
            println("%5d | %8d | %s | %s".format(idx, item.startTime, item.enText, item.koText))
        }
    }

    @Test
    fun `smi 단일 파일 테스트`() {
        val filePath = "/Users/user/ws/smi/iron.man.3.2013.02.eng.smi"
        val content = File(filePath).readText(Charset.forName("UTF-8"))

        // 데이터 정제
        val list = parseContent(content)
        val allTimeSlots = (list.map { it.first }).distinct().sorted()
        val timeMap = list.toMap()

        // 시간 슬롯 기준으로 머지
        val mergedList = allTimeSlots.map { time ->
            SubtitleSingle(
                startTime = time,
                text = timeMap[time] ?: "",
            )
        }

        // 출력
        mergedList.take(5000).forEachIndexed { idx, item ->
            println("%5d | %8d | %s".format(idx, item.startTime, item.text))
        }
    }

    private fun parseContent(content: String): List<Pair<Long, String>> {
        // SYNC 태그와 그 뒤의 내용을 찾는 정규식
        // <SYNC Start=(\d+)> : 시간값 캡처
        // (?:<P[^>]*>)? : <P> 태그는 옵션으로 매칭 (캡처 안함)
        // (.*?) : 다음 <SYNC 태그 전까지의 텍스트 캡처
        val regex = Regex("""<SYNC Start=(\d+)>[^>]*>(?:<P[^>]*>)?([\s\S]*?)(?=<SYNC|[\s]*$|[\s]*<\/BODY>)""", RegexOption.IGNORE_CASE)

        return regex.findAll(content).map { match ->
            val startTime = match.groupValues[1].toLong()
            val text = match.groupValues[2]
                    .replace(Regex("(?i)<br\\s*/?>"), " ") //<br> 또는 <br/> 태그를 공백(" ")으로 치환
                    .replace(Regex("<[^>]*>"), "") // 나머지 HTML 태그 제거
                    .replace("&nbsp;", " ")        // HTML 공백 문자 변환
                    .replace(Regex("\\s+"), " ") //연속된 공백을 한 칸으로 줄이고 앞뒤 공백 제거
                    .trim()
            startTime to text
        }.filter { it.second.isNotBlank() } // 내용이 없는 싱크(자막 종료용)는 제외
            .toList()
    }

    private fun printFormatted(list: List<SubtitleDouble>) {
        list
            .take(300)
            .forEachIndexed { index, item ->
                val lineNumber = "%5d".format(index)
                val formattedTime = "%8d".format(item.startTime)
                println("$lineNumber | $formattedTime | ${item.koText} | ${item.enText} ")
            }
    }
}