package project

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.config.EnableMongoAuditing

@EnableMongoAuditing
@SpringBootApplication
class DataApplication

fun main(args: Array<String>) {
	runApplication<DataApplication>(*args)
}