package danielk.wordquiz

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
class WordquizApplication {
    @GetMapping("/status")
    fun status() = "this is " + WordquizApplication::class.simpleName
}

fun main(args: Array<String>) {
    runApplication<WordquizApplication>(*args)
}
