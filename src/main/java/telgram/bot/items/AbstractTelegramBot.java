package telgram.bot.items;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telgram.bot.error.CommandNotFound;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
public abstract class AbstractTelegramBot extends TelegramLongPollingBot implements BotInitializer {
    @NonNull
    private String botToken;
    @NonNull
    private String botUserName;
    private final String COMMAND_PREFIX = "/";

    protected Consumer<MessageContext> actionForTextRequest;
    protected Consumer<BadResponse> actionForBadRequest;
    protected Map<String, TelegramBotCommand> commandsMap;
    protected Map<Integer, UserState> activeUser; //may be create DB for it
    protected ResourceBundle botResourceBundle;

    public AbstractTelegramBot() {
        activeUser = new HashMap<>();

        initBotResourceBundle();
        initActionForUserText();
        initActionForBadRequest();
        configureBotCommandsMap();
    }

    private void executeCommand(Update request) throws CommandNotFound {
        String commandName = request.getMessage().getText().split("\\s+")[0];
        if (commandsMap.containsKey(commandName)) {
            log.debug("Execute command: {}", commandsMap.get(commandName));
            commandsMap.get(commandName).getAction().accept(new MessageContext(
                    request.getMessage().getChatId(),
                    request.getMessage().getFrom().getId(),
                    request.getMessage().getChat().getUserName(),
                    request));
        } else {
            throw new CommandNotFound("Can't find command with name: " + commandName);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Received message: {}", update.getMessage());
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                log.debug("Start parsing message");
                if (update.getMessage().isCommand()) {
                    executeCommand(update);
                } else {
                    if(update.getMessage().getText().startsWith(COMMAND_PREFIX)) throw new CommandNotFound("Can't find command");
                    log.debug("User = '{}' wrote text: {}", update.getMessage().getChat().getUserName(), update.getMessage().getText());
                    actionForTextRequest.accept(new MessageContext(
                            update.getMessage().getChatId(),
                            update.getMessage().getFrom().getId(),
                            update.getMessage().getChat().getUserName(),
                            update));
                }
            } else {
                log.warn("Message is empty");
            }
        } catch (CommandNotFound cnf) {
            log.error("", cnf);
            actionForBadRequest.accept(new BadResponse(update, ErrorType.COMMAND_NOT_FOUND));
        } catch (Exception ex) {
            log.error("", ex);
            actionForBadRequest.accept(new BadResponse(update, ErrorType.INTERNAL_ERROR));
        }
    }

    protected void sendMessage(SendMessage message) {
        try {
            log.debug("Sending message = '{}' for chatId = '{}'", message);
            execute(message);
            log.debug("Successfully send message for chatId = '{}", message.getChatId());
        } catch (TelegramApiException e) {
            log.error("", e);
        }
    }

    protected ReplyKeyboardMarkup createReplyKeyBoard(List<String> commandList, Boolean hideAfterAction, Boolean resizeKeyBoard)
    {
        ReplyKeyboardMarkup keyBoard = new ReplyKeyboardMarkup();
        keyBoard.setOneTimeKeyboard(hideAfterAction);
        keyBoard.setResizeKeyboard(resizeKeyBoard);

        //TODO think about order, may be use sort function
        keyBoard.setKeyboard(commandList.stream().map((rawCommandName) -> {
                                KeyboardRow row = new KeyboardRow();
                                row.add(rawCommandName);
                                return row;
                            }).collect(Collectors.toList()));
        return keyBoard;
    }

    @Override
    public void initBotResourceBundle() {
        botResourceBundle = ResourceBundle.getBundle("telegram-bot");
    }

    @Override
    public void initActionForBadRequest() {
        actionForBadRequest = msg -> log.warn("Logic wasn't implemented");
    }

    @Override
    public void initActionForUserText() {
        actionForTextRequest = msg -> log.warn("Logic wasn't implemented");
    }

    @Override
    public void configureBotCommandsMap() {
        commandsMap = Collections.emptyMap();
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }
}