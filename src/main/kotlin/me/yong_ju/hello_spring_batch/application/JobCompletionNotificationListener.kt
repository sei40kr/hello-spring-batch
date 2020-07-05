package me.yong_ju.hello_spring_batch.application

import me.yong_ju.hello_spring_batch.domain.model.Person
import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.listener.JobExecutionListenerSupport
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class JobCompletionNotificationListener(private val jdbcTemplate: JdbcTemplate) : JobExecutionListenerSupport() {
    companion object {
        private val log = LoggerFactory.getLogger(JobCompletionNotificationListener::class.java)
    }

    override fun afterJob(jobExecution: JobExecution) {
        if (jobExecution.status == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results")

            jdbcTemplate.query("SELECT first_name, last_name FROM people") { rs, _ ->
                Person(rs.getString(1), rs.getString(2))
            }
                    .forEach { log.info("Found <$it> in the database.") }
        }
    }
}