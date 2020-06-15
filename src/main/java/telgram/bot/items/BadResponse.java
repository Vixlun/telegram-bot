package telgram.bot.items;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
public class BadResponse {
    private Update request;
    private ErrorType errorType;

    public BadResponse(Update userRequest, ErrorType errorType) {
        this.request = userRequest;
        this.errorType = errorType;
    }
}