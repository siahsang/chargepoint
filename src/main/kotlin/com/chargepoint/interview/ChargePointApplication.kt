package com.chargepoint.interview

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChargePointApplication

fun main(args: Array<String>) {
	runApplication<ChargePointApplication>(*args)
}
