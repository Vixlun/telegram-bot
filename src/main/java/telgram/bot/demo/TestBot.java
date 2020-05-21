package telgram.bot.demo;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class TestBot extends TelegramLongPollingBot {

    private String botUserName;
    private String botToken;

    public TestBot(String userName, String userToken) {
        this.botUserName = userName;
        this.botToken = userToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Received message: {}", update.getMessage());

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText("Why?");
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                log.error("", e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
