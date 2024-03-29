package ru.mybots.alligator;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mybots.alligator.filter.UpdatesFilter;
import ru.mybots.alligator.processor.ProcessResult;
import ru.mybots.alligator.processor.AlligatorUpdatesProcessor;

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

    @Autowired
    private Alligator alligator;

    @Autowired
    private AlligatorUpdatesProcessor updatesProcessor;
    @Autowired
    private UpdatesFilter updatesFilter;

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

    @PostConstruct
    public void init() throws UnknownHostException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(InetAddress.getByName("45.159.189.36"), 1080)));
        java.net.Authenticator.setDefault(new MyAuthenticator());

        final TelegramBot bot = new TelegramBot.Builder("5119841589:AAGLodZA3B3vC9FGIz2Ho9ES-A3Th0XeGmw").okHttpClient(builder.build()).build();
        // Register for updates
        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> updates) {
                if (updates.isEmpty()) {
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }
                for (Update update : updates) {
                    try {
                        if (!updatesFilter.filter(update)) {
                            continue;
                        }

                        ProcessResult result = updatesProcessor.process(update);

                        switch (result.getResultType()) {
                            case ProcessResult.NOOP:
                                continue;
                            case ProcessResult.MESSAGE:
                                SendMessage request = (SendMessage) result.getContent();
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
                                break;
                            case ProcessResult.ANSWER_CALLBACK_QUERY:
                                AnswerCallbackQuery answerCallbackQuery = (AnswerCallbackQuery) result.getContent();
                                if (answerCallbackQuery != null) {
                                    bot.execute(answerCallbackQuery);
                                }
                                break;
                        }
                    }
                    catch(Exception ex) {
                        ProcessResult result = new ProcessResult(ProcessResult.MESSAGE,
                                new SendMessage(update.message().chat().id(), StringConstants.SOMETHINGS_GONE_WRONG)
                        );
                        SendMessage request = (SendMessage) result.getContent();
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
}



