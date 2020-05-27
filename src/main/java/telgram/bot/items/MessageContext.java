package telgram.bot.items;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
public class MessageContext {
    private UserState state;
    private final Long chatId;
    private final Integer userId;
    private final String userLogin;
    private final Update request;

    public MessageContext(Long chatId, Integer userId, String userLogin, Update request) {
        this.chatId = chatId;
        this.userId = userId;
        this.userLogin = userLogin;
        this.request = request;
    }
}
