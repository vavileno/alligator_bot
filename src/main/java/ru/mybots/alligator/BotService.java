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
import org.springframework.stereotype.Service;

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

                if(updates.isEmpty()) {
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }

                Message m = updates.get(0).message();
                CallbackQuery cbq = updates.get(0).callbackQuery();

                if(cbq != null) {
                    AnswerCallbackQuery answer = new AnswerCallbackQuery(cbq.id());
                    answer.text("Ай да ту сабулу буды");
                    answer.showAlert(true);

                    bot.execute(answer);
                }

                if(m == null) {
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }


                Long chatId = updates.get(0).message().chat().id();

                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("url").url("https://www.ya.ru"),
                                new InlineKeyboardButton("callback_data").callbackData("callback_data"),
                                new InlineKeyboardButton("Switch!").switchInlineQuery("switch_inline_query")
                        });

                SendMessage request = new SendMessage(chatId, "Бузя")
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true)
                        .disableNotification(true)
                        .replyMarkup(inlineKeyboard)
                        ;
////                        .replyToMessageId(1)
//                        .replyMarkup(new ForceReply());

                // async
                bot.execute(request, new Callback<SendMessage, SendResponse>() {
                    @Override
                    public void onResponse(SendMessage request, SendResponse response) {

                    }

                    @Override
                    public void onFailure(SendMessage request, IOException e) {

                    }
                });
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        });
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