package telgram.bot.error;

public class CommandNotFound extends Exception{
    public CommandNotFound(String message) {
        super(message);
    }
}
