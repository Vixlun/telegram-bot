package telgram.bot.items;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
public class TelegramBotCommand {
    private String name;
    private String description;
    private Consumer<MessageContext> action;
    
    TelegramBotCommand(String name, String description, Consumer<MessageContext> action) {
        //TODO create check for command name. That they should stat at '/'
        //TODO create check for empty name
        this.name = name;
        this.description = description;
        this.action = action;
    }

    @Override
    public String toString() {
        return "TelegramBotCommand{" + "name =" + name +", description=" + description + "}";
    }

    public static TelegramBotCommand.CommandBuilder builder() {
        return new TelegramBotCommand.CommandBuilder();
    }

    public static class CommandBuilder {
        private String name;
        private String description;
        private Consumer<MessageContext> action;

        public CommandBuilder name(String commandName) {
            this.name = commandName;
            return this;
        }

        public CommandBuilder description(String commandInformation) {
            this.description = commandInformation;
            return this;
        }

        public CommandBuilder action(Consumer<MessageContext> commandAction) {
            this.action = commandAction;
            return this;
        }

        public TelegramBotCommand build() {
            return new TelegramBotCommand(this.name, this.description, this.action);
        }
    }

}
