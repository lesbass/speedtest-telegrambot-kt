package com.lesbass.telegram

import com.github.kittinunf.fuel.httpGet
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.natpryce.konfig.*

private fun getLog(logUrl: String): String {
    val (_, _, result) = logUrl
        .httpGet()
        .responseString()

    return if (result is com.github.kittinunf.result.Result.Success) {
        result.get()
    } else {
        "Errore"
    }
}

private fun getLastTest(logUrl: String): String {
    val data = getLog(logUrl)
    return if (data === "Errore") {
        data
    } else {
        data.lines().filter { it != "" }.takeLast(4).joinToString("\n")
    }
}

fun main() {
    val config = EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("defaults.properties")
    val apiKey = config[Key("API_KEY", stringType)]
    val logUrl = config[Key("LOG_URL", stringType)]
    println("Telegram Bot started! ")
    println(
        "apiKey: ${
            if (apiKey.isEmpty()) {
                "✔"
            } else {
                "❌"
            }
        }"
    )
    println("logUrl: ${
        if (logUrl.isEmpty()) {
            "✔"
        } else {
            "❌"
        }
    }")
    val bot = bot {
        token = apiKey
        dispatch {
            command("last_test") {
                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = getLastTest(logUrl))
            }
        }
    }
    println("Polling...")
    bot.startPolling()
}