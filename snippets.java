
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