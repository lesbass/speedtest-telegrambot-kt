package com.lesbass.telegram

import com.github.kittinunf.fuel.httpGet
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
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
            if (apiKey.isNotEmpty()) {
                "✔"
            } else {
                "❌"
            }
        }"
    )
    println(
        "logUrl: ${
            if (logUrl.isNotEmpty()) {
                "✔"
            } else {
                "❌"
            }
        }"
    )
    val bot = bot {
        token = apiKey
        dispatch {
            text {
                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text =
                when(message.text){
                    "/last_test" -> {
                        println("last_test command requested")
                        getLastTest(logUrl)
                    }
                    "/ciao" -> {
                        println("ciao command requested")
                        """Ciao ${message.from?.firstName}!"""
                    }
                    else -> {
                        println("free text " + message.text)
                        """Sorry, ${message.from?.firstName}, I still don't know this command!"""
                    }
                })
            }
        }
    }
    println("Polling...")
    bot.startPolling()
}