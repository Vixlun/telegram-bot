package telgram.bot.items;

public interface BotInitializer {
    void initActionForBadRequest(); // logic for what bot should do when user sent bad-request
    void initActionForUserText(); // logic for what bot should do when user send text-request
    void initBotResourceBundle(); // initialize resource bundle for your bot
    void configureBotCommandsMap(); // in this method you should init map with bot commands
}
