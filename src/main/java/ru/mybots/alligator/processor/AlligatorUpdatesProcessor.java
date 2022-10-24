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
import ru.mybots.alligator.Commands;
import ru.mybots.alligator.ILQ;
import ru.mybots.alligator.StartGameResult;
import ru.mybots.alligator.StringConstants;
import ru.mybots.alligator.dao.obj.Game;
import ru.mybots.alligator.exception.AlligatorApplicationException;

@Service
public class AlligatorUpdatesProcessor  {

    private static final Logger log = LoggerFactory.getLogger(AlligatorUpdatesProcessor.class);

    @Autowired
    private Alligator alligator;

    private final String BOT_NAME = "gator1_bot";
    private final String AT_SYMBOL = "@";
    private final String ADD_LINK = "https://t.me/" + BOT_NAME + "?startgroup=fromprivate";

    public ProcessResult process(Update update) {
        SendMessage request = null;
        Message m = update.callbackQuery() != null ? update.callbackQuery().message() : update.message();
        if(m == null) {
            return new ProcessResult(ProcessResult.NOOP, null);
        }
        if(update.callbackQuery() != null) {
            return processInlineQuery(update.callbackQuery());
        }
        if(m.text() == null) {
            return new ProcessResult(ProcessResult.NOOP, null);
        }
        if (m.text().startsWith("/")) {
            Object result = processCommand(update);
            return new ProcessResult(ProcessResult.MESSAGE, result);
        } else {
            Object result = processText(update);
            return new ProcessResult(ProcessResult.MESSAGE, result);
        }
    }

    private SendMessage processCommand(Update update) {
        Message m = update.message();
        Long chatId = m.chat().id();
        String command = m.text().substring(1, m.text().indexOf(AT_SYMBOL));
        InlineKeyboardMarkup inlineKeyboard = null;
        switch (command) {
            case Commands.COMMAND_START:
                StartGameResult result = null;
                try {
                    result = alligator.startGame(m.chat(), m.from());
                    inlineKeyboard = new InlineKeyboardMarkup(
                            new InlineKeyboardButton(StringConstants.WORD).callbackData(ILQ.SHOWWORD),
                            new InlineKeyboardButton(StringConstants.CHANGE).callbackData(ILQ.CHANGEWORD));
                }
                catch(AlligatorApplicationException ex) {
                    log.error(ex.getError().errmsg());
                    result = new StartGameResult(false, StringConstants.SOMETHINGS_GONE_WRONG);
                    return new SendMessage(chatId, result.getResultMsg());
                }
                return new SendMessage(chatId, result.getResultMsg())
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true)
                        .disableNotification(true)
                        .replyMarkup(inlineKeyboard);
            case Commands.COMMAND_STATUS:
                try {
                    GameStatus response = alligator.statusMessage(chatId);
                    if(response.isGameActive()) {
                        inlineKeyboard = new InlineKeyboardMarkup(
                                new InlineKeyboardButton(StringConstants.WORD).callbackData(ILQ.SHOWWORD),
                                new InlineKeyboardButton(StringConstants.CHANGE).callbackData(ILQ.CHANGEWORD));
                        return new SendMessage(chatId, response.getStatusMsg())
                                .parseMode(ParseMode.HTML)
                                .disableWebPagePreview(true)
                                .disableNotification(true)
                                .replyMarkup(inlineKeyboard);
                    }
                    else {
                        return new SendMessage(chatId, response.getStatusMsg());
                    }
                } catch (AlligatorApplicationException e) {
                    return new SendMessage(chatId, StringConstants.SOMETHINGS_GONE_WRONG);
                }
        }
        return null;
    }

    private ProcessResult processInlineQuery(CallbackQuery cbq) {
        try {
            String response = null;
            switch (cbq.data()) {
                case ILQ.WANNA_BE_LEADER:
                    boolean result = alligator.wannaBeLeader(cbq.message().chat(), cbq.from());
                    if(result) {
                        String responseMsg = alligator.somebodyProposeWord(cbq.from());
                        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                                new InlineKeyboardButton(StringConstants.WORD).callbackData(ILQ.SHOWWORD),
                                new InlineKeyboardButton(StringConstants.CHANGE).callbackData(ILQ.CHANGEWORD));
                        SendMessage message = new SendMessage(cbq.message().chat().id(), responseMsg.toString())
                                .parseMode(ParseMode.HTML)
                                .disableWebPagePreview(true)
                                .disableNotification(true)
                                .replyMarkup(inlineKeyboard);
                        return new ProcessResult(ProcessResult.MESSAGE, message);
                    }
                    else {
                        response = Game.Messages.ALREADY_ACTIVE;
                    }
                    break;
                case ILQ.SHOWWORD:
                    response = alligator.showWord(cbq.message().chat().id(), cbq.from().id());
                    break;
                case ILQ.CHANGEWORD:
                    response = alligator.nextWord(cbq.message().chat(), cbq.from().id());
                    break;
            }
            return new ProcessResult(ProcessResult.ANSWER_CALLBACK_QUERY, answerCallbackQuery(cbq, response));
        } catch (AlligatorApplicationException ex) {
            log.error(ex.getError().errmsg());
        }
        return null;
    }

    private SendMessage processText(Update update) {
        try {
            Message m = update.message();
            if(alligator.hasActiveGame(m.chat().id())
                    && alligator.tryWord(m.chat(), m.from(), m.text())) {

                String responseMsg = String.format(StringConstants.SOMEBODY_GUESSED_RIGHT,
                        alligator.getUserTitle(m.from()), "\"" + m.text().trim().toLowerCase() + "\"");

                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                        new InlineKeyboardButton(StringConstants.WANNA_BE_LEADER).callbackData(ILQ.WANNA_BE_LEADER));
                return new SendMessage(m.chat().id(), responseMsg)
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true)
                        .disableNotification(true)
                        .replyMarkup(inlineKeyboard);
            }
        } catch (AlligatorApplicationException e) {
            log.error(e.getError().errmsg());
        }
        return null;
    }

    private AnswerCallbackQuery answerCallbackQuery(CallbackQuery cbq, String text) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery(cbq.id());
        answer.text(text);
        answer.showAlert(true);
        return answer;
    }
}

//                return new SendMessage(chatId, "Добавить бота в чат: " + addlink)
//                        .parseMode(ParseMode.HTML)
//                        .disableWebPagePreview(true)
//                        .disableNotification(true)
//                        ;