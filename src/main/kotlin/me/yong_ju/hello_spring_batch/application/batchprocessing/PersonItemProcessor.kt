package me.yong_ju.hello_spring_batch.application.batchprocessing

import me.yong_ju.hello_spring_batch.domain.model.Person
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor

class PersonItemProcessor : ItemProcessor<Person, Person> {
    companion object {
        val log = LoggerFactory.getLogger(PersonItemProcessor::class.java)
    }

    override fun process(person: Person): Person? {
        val firstName = person.firstName?.toUpperCase()
        val lastName = person.lastName?.toUpperCase()

        val transformedPerson = Person(firstName, lastName)

        log.info("Converting ($person) into ($transformedPerson)")

        return transformedPerson
    }
}