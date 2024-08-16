package org.example;

import net.thauvin.erik.crypto.CryptoPrice;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public class MyBot extends TelegramLongPollingBot {

    public MyBot() {
        super("7373536097:AAHJvcIAeazkKzAjYy6a_OsJpW1XxNo-XuA");
    }

    @Override
    public void onUpdateReceived(Update update) {
        var chatId = update.getMessage().getChatId();
        var text = update.getMessage().getText();

        try {
            if (text.equals("/start")) {
                sendMessage(chatId, "Hello!");
            } else if (text.equals("btc")) {
                sendPicture(chatId, "bitcoin-btc-logo.png");
                sendPrice(chatId, "BTC");
            } else if (text.equals("eth")) {
                sendPicture(chatId, "ethereum-eth-logo.png");
                sendPrice(chatId, "ETH");
            } else if (text.equals("doge")) {
                sendPicture(chatId, "dogecoin-doge-logo.png");
                sendPrice(chatId, "DOGE");
            } else if (text.equals("/all")) {
                sendPicture(chatId, "bitcoin-btc-logo.png");
                sendPrice(chatId, "BTC");
                sendPicture(chatId, "ethereum-eth-logo.png");
                sendPrice(chatId, "ETH");
                sendPicture(chatId, "dogecoin-doge-logo.png");
                sendPrice(chatId, "DOGE");
            } else if (text.matches("^(btc|eth|doge) \\d+(\\.\\d+)?$")) {
                String[] parts = text.split(" ");
                String crypto = parts[0].toUpperCase();
                double amount = Double.parseDouble(parts[1]);
                sendCryptoAmount(chatId, crypto, amount);
            } else {
                try {
                    double amount = Double.parseDouble(text);
                    sendCryptoAmountAll(chatId, amount);
                } catch (NumberFormatException e) {
                    sendMessage(chatId, "Unknown command!");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    void sendPrice(long chatId, String name) throws Exception {
        var price = CryptoPrice.spotPrice(name);
        sendMessage(chatId, name + " price: " + price.getAmount().doubleValue());
    }

    void sendCryptoAmount(long chatId, String crypto, double amount) throws Exception {
        var price = CryptoPrice.spotPrice(crypto).getAmount().doubleValue();
        var cryptoAmount = amount / price;
        sendMessage(chatId, String.format("For $%.2f you can buy %.6f %s", amount, cryptoAmount, crypto));
    }

            void sendCryptoAmountAll(long chatId, double amount) throws Exception {
                var btcPrice = CryptoPrice.spotPrice("BTC").getAmount().doubleValue();
                var ethPrice = CryptoPrice.spotPrice("ETH").getAmount().doubleValue();
                var dogePrice = CryptoPrice.spotPrice("DOGE").getAmount().doubleValue();

                var btcAmount = amount / btcPrice;
                var ethAmount = amount / ethPrice;
                var dogeAmount = amount / dogePrice;

                sendMessage(chatId, String.format("For $%.2f you can buy:\nBTC: %.6f\nETH: %.6f\nDOGE: %.6f", amount, btcAmount, ethAmount, dogeAmount));
            }

    void sendPicture(long chatId, String name) throws Exception {
        var photo = getClass().getClassLoader().getResourceAsStream(name);

        var message = new SendPhoto();
        message.setChatId(chatId);
        message.setPhoto(new InputFile(photo, name));
        execute(message);
    }

    void sendMessage(long chatId, String text) throws Exception {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        execute(message);
    }

    @Override
    public String getBotUsername() {
        return "java27072024_bot";
    }
}
