package me.yong_ju.hello_spring_batch.application

import me.yong_ju.hello_spring_batch.application.batchprocessing.PersonItemProcessor
import me.yong_ju.hello_spring_batch.domain.model.Person
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import javax.sql.DataSource

@SpringBootApplication
@EnableBatchProcessing
class BatchProcessingApplication {
    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun reader(): FlatFileItemReader<Person> = FlatFileItemReaderBuilder<Person>()
            .name("personItemReader")
            .resource(ClassPathResource("sample-data.csv"))
            .delimited()
            .names("firstName", "lastName")
            .fieldSetMapper(BeanWrapperFieldSetMapper<Person>().apply {
                setTargetType(Person::class.java)
            })
            .build()

    @Bean
    fun processor() = PersonItemProcessor()

    @Bean
    fun writer(dataSource: DataSource) = JdbcBatchItemWriterBuilder<Person>()
            .itemSqlParameterSourceProvider(BeanPropertyItemSqlParameterSourceProvider())
            .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
            .dataSource(dataSource)
            .build()

    @Bean
    fun importUserJob(listener: JobCompletionNotificationListener, step1: Step) = jobBuilderFactory
            .get("importUserJob")
            .incrementer(RunIdIncrementer())
            .listener(listener)
            .flow(step1)
            .end()
            .build()

    @Bean
    fun step1(writer: JdbcBatchItemWriter<Person>) = stepBuilderFactory.get("step1")
            .chunk<Person, Person>(10)
            .reader(reader())
            .processor(processor())
            .writer(writer)
            .build()
}

fun main(args: Array<String>) {
    runApplication<BatchProcessingApplication>(*args)
}

