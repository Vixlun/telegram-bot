package telgram.bot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import telgram.bot.demo.TelegramWhyBot;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class BotConfiguration {
    private final TelegramWhyBot telegramBot;

    @Autowired
    public BotConfiguration(TelegramWhyBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    void initMethod() {
        log.info("Start registering telegram api");
        TelegramBotsApi telegramApi = new TelegramBotsApi();
        try {
            telegramApi.registerBot(telegramBot);
        } catch (Exception ex) {
            log.error("", ex);
        }
    }
}
