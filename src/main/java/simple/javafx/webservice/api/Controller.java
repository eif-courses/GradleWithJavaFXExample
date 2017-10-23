package simple.javafx.webservice.api;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.rmi.runtime.Log;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class Controller implements Initializable {
    @FXML
    private Label score;
    @FXML
    private ImageView card1;

    @FXML
    private ImageView card2;
    @FXML
    private ImageView card3;

    @FXML
    private Button shuffleButton;
    @FXML
    private ComboBox<String> choose_card_back;
    @FXML
    private ImageView card4;
    private List<ImageView> cards = new ArrayList<>();
    Map<String, String> listOfCards = new HashMap<>();
    Map<String, String> randomCards = new HashMap<>();
    //List<String> randomCardNames = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        try {
            getCardsByClassName("Shaman");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            JSONArray jsonArray = getAllCardBacks();

            for (int i = 0; i < jsonArray.length(); i++) {
                choose_card_back.getItems().add(jsonArray.getJSONObject(i).getString("name"));
                listOfCards.put(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("img"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        choose_card_back.valueProperty().addListener((ov, t, tt) -> {
            for (ImageView card : cards) {
                card.setImage(new Image(listOfCards.get(ov.getValue())));
            }
        });
        for (ImageView card : cards) {
            card.setOnMouseEntered(e -> {
                double r = new Random().nextDouble();
                double g = new Random().nextDouble();
                double b = new Random().nextDouble();
                double a = new Random().nextDouble();
                card.setEffect(new DropShadow(BlurType.GAUSSIAN, new Color(r, g, b, a), 100, 0.5, 0, 0));
            });
            card.setOnMouseClicked(event -> {
                String randomName = (String) randomCards.keySet().toArray()[new Random().nextInt(randomCards.keySet().toArray().length)];
                card.setImage(new Image(randomCards.get(randomName)));
            });

        }
        shuffleButton.setOnMouseClicked(e->{
            for (ImageView card : cards) {
                String randomName = (String) randomCards.keySet().toArray()[new Random().nextInt(randomCards.keySet().toArray().length)];
                card.setImage(new Image(randomCards.get(randomName)));
            } });
    }

    private JSONArray getAllCardBacks() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://omgvamp-hearthstone-v1.p.mashape.com/cardbacks")
                .addHeader("X-Mashape-Key", "Q0ZvyGgPZfmshIFUmF1yWuxhPOPop1ON2nqjsnogXDUcMQ9Cdh")
                .build();
        Response response = client.newCall(request).execute();
        return new JSONArray(response.body().string());
    }

    private void getRequestForAllCardsFromResponse() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://omgvamp-hearthstone-v1.p.mashape.com/cards")
                .addHeader("X-Mashape-Key", "Q0ZvyGgPZfmshIFUmF1yWuxhPOPop1ON2nqjsnogXDUcMQ9Cdh")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    JSONObject jsonObject = getAllCards(responseBody.string());

                    for (int i = 0; i < jsonObject.length(); i++) {
                        //  randomCardNames.add(jsonObject.getJSONArray("Basic").getJSONObject(i).getString("name"));

                    }
                    //   for (String item: randomCardNames) {
                    //      System.out.println(item);
                    // }

                }
            }
        });
    }

    private void getCardsByClassName(String name) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://omgvamp-hearthstone-v1.p.mashape.com/cards/classes/Mage")
                .addHeader("X-Mashape-Key", "Q0ZvyGgPZfmshIFUmF1yWuxhPOPop1ON2nqjsnogXDUcMQ9Cdh")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    JSONArray jsonArray = new JSONArray(responseBody.string());
                    for (int i = 40; i < jsonArray.length(); i++) {
                        randomCards.put(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("img"));
                        //System.out.println(jsonArray.getJSONObject(i).getString("img"));
                    }

                }
            }
        });
    }


    private JSONObject getAllCards(String responseData) throws IOException {
        return new JSONObject(responseData);
    }

    private void getSingleCardByName(String name) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://omgvamp-hearthstone-v1.p.mashape.com/cards/" + name)
                .addHeader("X-Mashape-Key", "Q0ZvyGgPZfmshIFUmF1yWuxhPOPop1ON2nqjsnogXDUcMQ9Cdh")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                JOptionPane.showMessageDialog(null, "API PROBLEMS TRY AGAIN");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //response.getBody().getArray().getJSONObject(0).getString("img");
                JSONArray jsonArray = new JSONArray(response.body().string());
                // for (int i = 0; i < jsonArray.length(); i++) {
                System.out.println(jsonArray.getJSONObject(0).getString("img"));
                //}
                // card1.setImage(new Image(response.body().string()));
            }
        });

    }


}
