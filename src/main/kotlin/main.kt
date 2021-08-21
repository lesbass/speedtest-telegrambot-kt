import com.github.kittinunf.fuel.httpGet
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import io.github.cdimascio.dotenv.dotenv

val dotenv = dotenv()

private fun getLog(): String {
    val (request, response, result) = dotenv["LOG_URL"]
        .httpGet()
        .responseString()

    return if (result is com.github.kittinunf.result.Result.Success) {
        result.get()
    } else {
        "Errore"
    }
}

private fun getLastTest(): String {
    val data = getLog()
    return if (data === "Errore") {
        data
    } else {
        data.lines().filter { it != "" }.takeLast(4).joinToString("\n")
    }
}

fun main() {
    val bot = bot {
        token = dotenv["API_KEY"]
        dispatch {
            command("last_test") {
                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = getLastTest())
            }
        }
    }
    bot.startPolling()
}