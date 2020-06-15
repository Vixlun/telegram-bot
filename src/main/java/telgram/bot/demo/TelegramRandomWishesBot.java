package telgram.bot.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import telgram.bot.items.AbstractTelegramBot;
import telgram.bot.items.TelegramBotCommand;
import telgram.bot.items.UserState;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@Qualifier("random-wishes")
public class TelegramRandomWishesBot extends AbstractTelegramBot {
    private final String START_COMMAND              = "/start";
    private final String WISH_COMMAND               = "/wish";
    private final String HELP_COMMAND               = "/help";
    private final String FINISH_COMMAND             = "/finish";

    private final Random random = new Random();
    private List<String> wishesPhotoList;

    public TelegramRandomWishesBot() {
        super();
        initWishesPhotoList();
    }

    private void initWishesPhotoList() {
        try (BufferedReader br = new BufferedReader(
                    new FileReader(ResourceUtils.getFile("classpath:wishes.txt")))) {
            String line;
            wishesPhotoList = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                wishesPhotoList.add(line);
            }
        } catch (Exception ex) {
            wishesPhotoList = Collections.emptyList();
            log.error("ex", ex);
        }
    }

    @Override
    public void initActionForUserText() {
        actionForTextRequest = (messageContext -> {
            SendMessage message = new SendMessage()
                    .setText(botResourceBundle.getString("bot.message.commandNotFound"))
                    .setReplyToMessageId(messageContext.getRequest().getMessage().getMessageId())
                    .setChatId(messageContext.getRequest().getMessage().getChatId());
            sendMessage(message);
        });
    }

    @Override
    public void configureBotCommandsMap() {
        commandsMap = Stream.of(
                TelegramBotCommand.builder()
                        .name(START_COMMAND)
                        .description(botResourceBundle.getString("bot.command.start.description"))
                        .action(msgContext -> {
                            log.debug("User = '{}' starting work with bot", msgContext.getUserLogin());
                            activeUser.put(msgContext.getUserId(), UserState.PLAYING);
                            SendMessage message = new SendMessage()
                                    .setText(botResourceBundle.getString("bot.command.start.message"))
                                    .setChatId(msgContext.getChatId())
                                    .setReplyMarkup(createReplyKeyBoard(Arrays.asList(WISH_COMMAND, FINISH_COMMAND, HELP_COMMAND),
                                            Boolean.FALSE));
                            sendMessage(message);
                        })
                        .build(),
                TelegramBotCommand.builder()
                        .name(HELP_COMMAND)
                        .description(botResourceBundle.getString("bot.command.help.description"))
                        .action(msgContext -> {
                            SendMessage message = new SendMessage()
                                    .setText(botResourceBundle.getString("bot.command.help.message"))
                                    .setChatId(msgContext.getChatId());
                            message.setReplyMarkup(createReplyKeyBoard(Collections.singletonList(WISH_COMMAND),
                                    Boolean.FALSE));
                            sendMessage(message);
                        })
                        .build(),
                TelegramBotCommand.builder()
                        .name(FINISH_COMMAND)
                        .description(botResourceBundle.getString("bot.command.finish.description"))
                        .action(msgContext -> {
                            SendMessage message = new SendMessage()
                                    .setText(botResourceBundle.getString("bot.command.finish.message"))
                                    .setChatId(msgContext.getChatId());
                            message.setReplyMarkup(createReplyKeyBoard(Arrays.asList(START_COMMAND, HELP_COMMAND),
                                    Boolean.FALSE));
                            sendMessage(message);
                        })
                        .build(),
                TelegramBotCommand.builder()
                        .name(WISH_COMMAND)
                        .description(botResourceBundle.getString("bot.command.wish.description"))
                        .action(msgContext -> {
                            log.debug("User = '{}' got random wish", msgContext.getUserLogin());

                            SendPhoto photoItem = new SendPhoto();
                            photoItem.setChatId(msgContext.getChatId())
                                    .setPhoto(wishesPhotoList.get(random.nextInt(wishesPhotoList.size())));
                            photoItem.setCaption(botResourceBundle.getString("bot.wishes.text"));
                            photoItem.setReplyMarkup(createReplyKeyBoard(Arrays.asList(WISH_COMMAND, FINISH_COMMAND),
                                    Boolean.FALSE));

                            sendPhoto(photoItem);
                        })
                        .build()).collect(Collectors.toMap(TelegramBotCommand::getName, Function.identity()));
    }

    @Override
    public void initBotResourceBundle() {
        botResourceBundle = ResourceBundle.getBundle("random-wish-bot");
    }

    @Override
    protected void sendMessage(SendMessage message) {
        super.sendMessage(message);
    }
}
