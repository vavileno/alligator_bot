package ru.mybots.alligator;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mybots.alligator.exception.AlligatorApplicationException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.List;

@Service
public class BotService {

    private static final Logger log = LoggerFactory.getLogger(BotService.class);

    private final String addlink = "https://t.me/alligator_vata_bot?startgroup=fromprivate";

    private final String COMMAND_STARTGAME = "startgame";
    private final String COMMAND_STARTGAME_FROM_GROUP = "startgame@alligator_vata_bot";
    private final String COMMAND_START = "start";

    private final String ILQ_SHOWWORD = "showword";
    private final String ILQ_CHANGEWORD = "changeword";
    private final String ILQ_WANNA_BE_LEADER = "wannabeleader";

    @Autowired
    private GameProcessor gameProcessor;

    @PostConstruct
    public void init() throws UnknownHostException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(InetAddress.getByName("185.223.95.118"), 1080)));
        java.net.Authenticator.setDefault(new MyAuthenticator());

        final TelegramBot bot = new TelegramBot.Builder("1169475233:AAFmCL-3TRZvT2D3XzvkfzUeKMzVl6gcn-Q").okHttpClient(builder.build()).build();



        // Register for updates
        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> updates) {

                if (updates.isEmpty()) {
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }

                for (Update update : updates) {
                    CallbackQuery cbq = update.callbackQuery();
                    boolean isCbq = cbq != null;
                    Message m = isCbq ? cbq.message() : update.message();

                    SendMessage request = null;


                    // Accept only text and inline queries
                    if (m == null && !isCbq) {
                        continue;
                    }
                    // Accept commands only from chats without current games
                    if(!gameProcessor.chatSet().contains(m.chat().id())) {
                        if (!isCbq && (m.text() == null || !m.text().startsWith("/"))) {
                            continue;
                        }
                    }

                    if (isCbq) {
                        AnswerCallbackQuery answer = processInlineQuery(cbq);
                        if(answer != null) {
                            bot.execute(answer);
                        }
                        continue;
                    }

                    if (m.text().startsWith("/")) {
                        request = processCommand(update);
                    } else {
                        request = processText(update);
                    }

                    if (request != null) {
                        bot.execute(request, new Callback<SendMessage, SendResponse>() {
                            @Override
                            public void onResponse(SendMessage request, SendResponse response) {
                            }
                            @Override
                            public void onFailure(SendMessage request, IOException e) {
                            }
                        });
                    }
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        });
    }


    private SendMessage processText(Update update) {
        try {
            Message m = update.message();
            if(gameProcessor.tryWord(m.chat().id(), m.from().id().longValue(), m.text())) {
                InlineKeyboardMarkup inlineKeyboard = null;
                String responseMsg = m.from().firstName() + " " + m.from().lastName() + " угадал слово " + m.text().trim().toLowerCase();
                inlineKeyboard = new InlineKeyboardMarkup(
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Загадать").callbackData(ILQ_WANNA_BE_LEADER),
                        });
                return new SendMessage(m.chat().id(), responseMsg)
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

    private SendMessage processCommand(Update update)  {
        Message m = update.message();
        Long chatId = m.chat().id();
        String command = m.text().substring(1);
        InlineKeyboardMarkup inlineKeyboard = null;
        String responseMsg = null;
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
                    gameProcessor.start(m.chat().id(), m.from().id().longValue());
                    responseMsg = m.from().firstName() + " " + m.from().lastName() + " загадывает слово";
                    inlineKeyboard = new InlineKeyboardMarkup(
                            new InlineKeyboardButton[]{
                                    new InlineKeyboardButton("Слово").callbackData(ILQ_SHOWWORD),
                                    new InlineKeyboardButton("Поменять").callbackData(ILQ_CHANGEWORD)
                            });
                }
                catch(AlligatorApplicationException ex) {
                    log.error(ex.getError().errmsg());
                }
                return new SendMessage(chatId, responseMsg)
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true)
                        .disableNotification(true)
                        .replyMarkup(inlineKeyboard)
                ;
        }
        return null;
    }

    private AnswerCallbackQuery processInlineQuery(CallbackQuery cbq) {
        try {
            String response = null;
            switch (cbq.data()) {
                case ILQ_SHOWWORD:
                    response = gameProcessor.showWord(cbq.message().chat().id(), cbq.from().id().longValue());
                    break;
                case ILQ_CHANGEWORD:
                    response = gameProcessor.nextWord(cbq.message().chat().id(), cbq.from().id().longValue());
                    break;
            }
            AnswerCallbackQuery answer = new AnswerCallbackQuery(cbq.id());
            answer.text(response);
            answer.showAlert(true);
            return answer;
        } catch (AlligatorApplicationException ex) {
            log.error(ex.getError().errmsg());
        }
        return null;
    }
}

class MyAuthenticator extends java.net.Authenticator {

    protected PasswordAuthentication getPasswordAuthentication() {
        String promptString = getRequestingPrompt();
        System.out.println(promptString);
        String hostname = getRequestingHost();
        System.out.println(hostname);
        InetAddress ipaddr = getRequestingSite();
        System.out.println(ipaddr);
        int port = getRequestingPort();
        final String pUser = "u7354";
        final String pH = "sa6koruM7";
        return new PasswordAuthentication(pUser, pH.toCharArray());
    }
}


//
//                SendMessage request = new SendMessage(chatId, "Бузя")
//                        .parseMode(ParseMode.HTML)
//                        .disableWebPagePreview(true)
//                        .disableNotification(true)
//                        .replyMarkup(inlineKeyboard)
//                        ;
////                        .replyToMessageId(1)
//                        .replyMarkup(new ForceReply());

//    InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
//            new InlineKeyboardButton[]{
//                    new InlineKeyboardButton("url").url("https://www.ya.ru"),
//                    new InlineKeyboardButton("callback_data").callbackData("callback_data"),
//                    new InlineKeyboardButton("Switch!").switchInlineQuery("switch_inline_query")
//            });

// sync
//                SendResponse sendResponse = bot.execute(request);
//                boolean ok = sendResponse.isOk();
//                Message message = sendResponse.message();

// Send messages
//        long chatId = update.message().chat().id();
//        SendResponse response = bot.execute(new SendMessage(chatId, "Hello!"));



//    Authenticator proxyAuthenticator = new Authenticator() {
//        @Override public Request authenticate(Route route, Response response) throws IOException {
//            if (response.request().header("Proxy-Authorization") != null) {
//                return null; // Give up, we've already failed to authenticate.
//            }
//
//            String credential = Credentials.basic(pUser, pH);
//            return response.request().newBuilder()
//                    .header("Proxy-Authorization", credential)
//                    .build();
//        }
//    };

//    Keyboard replyKeyboardMarkup = new ReplyKeyboardMarkup(
//            new String[]{"first row button1", "first row button2"},
//            new String[]{"second row button1", "second row button2"})
//            .oneTimeKeyboard(true)   // optional
//            .resizeKeyboard(true)    // optional
//            .selective(true);
//    Keyboard keyboard = new ReplyKeyboardMarkup(
//            new KeyboardButton[]{
//                    new KeyboardButton("text"),
//                    new KeyboardButton("contact").requestContact(true),
//                    new KeyboardButton("location").requestLocation(true)
//            }
//    );

//                                new InlineKeyboardButton("url").url("www.google.com"),
//                                        new InlineKeyboardButton("url1").switchInlineQueryCurrentChat("RARARA"),
//                                        new InlineKeyboardButton("url2").callbackGame("GAME"),
//                                        new InlineKeyboardButton("url3").pay(),
//                                        new InlineKeyboardButton("callback_data").callbackData("callback_data"),
//                                        new InlineKeyboardButton("Switch!").switchInlineQuery("switch_inline_query")