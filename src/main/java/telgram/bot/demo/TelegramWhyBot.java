package telgram.bot.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import telgram.bot.items.AbstractTelegramBot;
import telgram.bot.items.TelegramBotCommand;
import telgram.bot.items.UserState;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class TelegramWhyBot extends AbstractTelegramBot {
    private final String START_COMMAND              = "/start";
    private final String NEW_REQUEST_COMMAND        = "/newRequest";
    private final String FINISH_COMMAND             = "/finish";
    private final String HELP_COMMAND               = "/help";

    public TelegramWhyBot() {
        super();
    }

    @Override
    public void initActionForBadRequest() {
        actionForBadRequest = (error -> {
            switch (error.getErrorType()) {
                case COMMAND_NOT_FOUND:
                    SendMessage message = new SendMessage()
                            .setText(botResourceBundle.getString("bot.message.commandNotFound"))
                            .setReplyToMessageId(error.getRequest().getMessage().getMessageId())
                            .setChatId(error.getRequest().getMessage().getChatId());
                    sendMessage(message);
                    break;
                default:
                     log.debug("Internal error. Nothing to send");
            }
        });
    }

    @Override
    public void initActionForUserText() {
        actionForTextRequest = (messageContext -> {
            SendMessage message = new SendMessage();
            if(activeUser.get(messageContext.getUserLogin()) == UserState.PLAYING) {
                    message.setText(botResourceBundle.getString("bot.massage.why"))
                        .setReplyToMessageId(messageContext.getRequest().getMessage().getMessageId());
            } else {
                message.setText(botResourceBundle.getString("bot.massage.notActive"));
                message.setReplyMarkup(createReplyKeyBoard(Arrays.asList(NEW_REQUEST_COMMAND, FINISH_COMMAND, HELP_COMMAND),
                                        Boolean.TRUE, Boolean.TRUE));
            }
            message.setChatId(messageContext.getChatId());
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
                    activeUser.put(msgContext.getUserLogin(), UserState.NOT_PLAYING);
                    SendMessage message = new SendMessage()
                            .setText(botResourceBundle.getString("bot.command.start.message"))
                            .setChatId(msgContext.getChatId())
                            .setReplyMarkup(createReplyKeyBoard(Arrays.asList(NEW_REQUEST_COMMAND, FINISH_COMMAND, HELP_COMMAND),
                                                Boolean.TRUE, Boolean.TRUE));
                    sendMessage(message);
                })
                .build(),
        TelegramBotCommand.builder()
                .name(NEW_REQUEST_COMMAND)
                .description(botResourceBundle.getString("bot.command.newRequest.description"))
                .action(msgContext -> {
                    log.debug("User = '{}' playing with bot", msgContext.getUserLogin());
                    activeUser.put(msgContext.getUserLogin(), UserState.PLAYING);
                    SendMessage message = new SendMessage()
                            .setText(botResourceBundle.getString("bot.command.newRequest.message"))
                            .setChatId(msgContext.getChatId());
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
                    message.setReplyMarkup(createReplyKeyBoard(Arrays.asList(NEW_REQUEST_COMMAND, FINISH_COMMAND, HELP_COMMAND),
                                                Boolean.TRUE, Boolean.TRUE));
                    sendMessage(message);
                })
                .build(),
        TelegramBotCommand.builder()
                .name(FINISH_COMMAND)
                .description(botResourceBundle.getString("bot.command.end.description"))
                .action(msgContext -> {
                    log.debug("User = '{}' finishing work with bot", msgContext.getUserLogin());
                    activeUser.remove(msgContext.getUserLogin());
                    SendMessage message = new SendMessage()
                            .setText(botResourceBundle.getString("bot.command.end.message"))
                            .setChatId(msgContext.getChatId())
                            .setReplyMarkup(createReplyKeyBoard(Arrays.asList(NEW_REQUEST_COMMAND, FINISH_COMMAND, HELP_COMMAND),
                                                Boolean.TRUE, Boolean.TRUE));
                    sendMessage(message);
                })
        .build()).collect(Collectors.toMap(TelegramBotCommand::getName, Function.identity()));
    }

    @Override
    public void initBotResourceBundle() {
        botResourceBundle = ResourceBundle.getBundle("why-bot");
    }

    @Override
    protected void sendMessage(SendMessage message) {
        super.sendMessage(message);
    }
}
