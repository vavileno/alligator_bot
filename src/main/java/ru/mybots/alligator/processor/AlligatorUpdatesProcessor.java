package ru.mybots.alligator.processor;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mybots.alligator.Alligator;
import ru.mybots.alligator.dao.obj.Game;
import ru.mybots.alligator.exception.AlligatorApplicationException;

@Service
public class AlligatorUpdatesProcessor implements UpdatesProcessor {

    private static final Logger log = LoggerFactory.getLogger(AlligatorUpdatesProcessor.class);

    @Autowired
    private Alligator alligator;

    private final String addlink = "https://t.me/alligator_vata_bot?startgroup=fromprivate";

    private final String COMMAND_STARTGAME = "startgame";
    private final String COMMAND_STARTGAME_FROM_GROUP = "startgame@alligator_vata_bot";
    private final String COMMAND_START = "start";

    private final String ILQ_SHOWWORD = "showword";
    private final String ILQ_CHANGEWORD = "changeword";
    private final String ILQ_WANNA_BE_LEADER = "wannabeleader";

    @Override
    public ProcessResult process(Update update) {
        SendMessage request = null;
        Message m = update.callbackQuery() != null ? update.callbackQuery().message() : update.message();
        if(m == null) {
            return null;
        }

        if(update.callbackQuery() != null) {
            return processInlineQuery(update.callbackQuery());
        }
        if (m.text().startsWith("/")) {
            Object result = processCommand(update);
            return new ProcessResult(ProcessResult.MESSAGE, result);
        } else {
            Object result = processText(update);
            return new ProcessResult(ProcessResult.MESSAGE, result);
        }
    }

    private SendMessage processCommand(Update update)  {
        Message m = update.message();
        Long chatId = m.chat().id();
        String command = m.text().substring(1);
        InlineKeyboardMarkup inlineKeyboard = null;
        StringBuilder responseMsg = new StringBuilder( StringUtils.defaultString(m.from().firstName()) );
        switch (command) {
            case COMMAND_START:
                return new SendMessage(chatId, "Добавить бота в чат: " + addlink)
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true)
                        .disableNotification(true)
                        ;
            case COMMAND_STARTGAME:
            case COMMAND_STARTGAME_FROM_GROUP:
                try {
                    alligator.start(m.chat().id(), m.from().id());
                    responseMsg.append(" ")
                            .append(StringUtils.defaultString(m.from().lastName()))
                            .append(" загадывает слово");
                    inlineKeyboard = new InlineKeyboardMarkup(
                            new InlineKeyboardButton("Слово").callbackData(ILQ_SHOWWORD),
                            new InlineKeyboardButton("Поменять").callbackData(ILQ_CHANGEWORD));
                }
                catch(AlligatorApplicationException ex) {
                    log.error(ex.getError().errmsg());
                }
                return new SendMessage(chatId, responseMsg.toString())
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true)
                        .disableNotification(true)
                        .replyMarkup(inlineKeyboard)
                        ;
        }
        return null;
    }

    private ProcessResult processInlineQuery(CallbackQuery cbq) {
        try {
            String response = null;
            StringBuilder responseMsg = new StringBuilder( StringUtils.defaultString(cbq.message().from().firstName()) );
            switch (cbq.data()) {
                case ILQ_WANNA_BE_LEADER:
                    boolean result = alligator.wannaBeLeader(cbq.message().chat().id(), cbq.from().id());
                    if(result) {
                        responseMsg.append(" ")
                                .append(StringUtils.defaultString(cbq.message().from().lastName()))
                                .append(" загадывает слово");
                        InlineKeyboardMarkup inlineKeyboard = inlineKeyboard = new InlineKeyboardMarkup(
                                new InlineKeyboardButton("Слово").callbackData(ILQ_SHOWWORD),
                                new InlineKeyboardButton("Поменять").callbackData(ILQ_CHANGEWORD));
                        SendMessage message = new SendMessage(cbq.message().chat().id(), responseMsg.toString())
                                .parseMode(ParseMode.HTML)
                                .disableWebPagePreview(true)
                                .disableNotification(true)
                                .replyMarkup(inlineKeyboard);
                        return new ProcessResult(ProcessResult.MESSAGE, message);
                    }
                    else {
                        response = Game.ALREADY_ACTIVE;
                    }
                    break;
                case ILQ_SHOWWORD:
                    response = alligator.showWord(cbq.message().chat().id(), cbq.from().id());
                    break;
                case ILQ_CHANGEWORD:
                    response = alligator.nextWord(cbq.message().chat().id(), cbq.from().id());
                    break;

            }
            AnswerCallbackQuery answer = new AnswerCallbackQuery(cbq.id());
            answer.text(response);
            answer.showAlert(true);
            return new ProcessResult(ProcessResult.ANSWER_CALLBACK_QUERY, answer);
        } catch (AlligatorApplicationException ex) {
            log.error(ex.getError().errmsg());
        }
        return null;
    }

    private SendMessage processText(Update update) {
        try {
            Message m = update.message();
            if(alligator.gameActive(m.chat().id()) && alligator.tryWord(m.chat().id(), m.from().id(), m.text())) {
                InlineKeyboardMarkup inlineKeyboard = null;
                StringBuilder responseMsg = new StringBuilder(StringUtils.defaultString(m.from().firstName()));
                responseMsg.append(" ")
                        .append(StringUtils.defaultString(m.from().lastName())).append(" угадал слово ")
                        .append(m.text().trim().toLowerCase());
                inlineKeyboard = new InlineKeyboardMarkup(
                        new InlineKeyboardButton("Хочу загадать").callbackData(ILQ_WANNA_BE_LEADER));
                return new SendMessage(m.chat().id(), responseMsg.toString())
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true)
                        .disableNotification(true)
                        .replyMarkup(inlineKeyboard)
                        ;
            }
        } catch (AlligatorApplicationException e) {
            log.error(e.getError().errmsg());
        }
        return null;
    }

}
