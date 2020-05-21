package telgram.bot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import telgram.bot.demo.TestBot;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class BotConfiguration {
    @Value("${main.telegram.id}")
    private Long mainTelegramId;
    @Value("${bot.username}")
    private String botUserName;
    @Value("${bot.token}")
    private String botToken;

    @PostConstruct
    void initMethod() {
        log.info("Start registering telegram api");
        TelegramBotsApi telegramApi = new TelegramBotsApi();
        try {
            telegramApi.registerBot(new TestBot(botUserName, botToken));
        } catch (Exception ex) {
            log.error("", ex);
        }
    }
}
