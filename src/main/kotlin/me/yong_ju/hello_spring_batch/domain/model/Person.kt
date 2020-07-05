package me.yong_ju.hello_spring_batch.domain.model

data class Person(var firstName: String?, var lastName: String?) {
    constructor() : this(null, null)
}