package simple.javafx.webservice.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable{
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
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cards.add(card1); cards.add(card2); cards.add(card3); cards.add(card4);

        Map<String, String> listOfCards = new HashMap<>();
        new Thread(() -> {
            try {
                JSONArray jsonArray = getAllCardBacks("https://omgvamp-hearthstone-v1.p.mashape.com/cardbacks");
                for (int i = 0; i < jsonArray.length(); i++) {
                    choose_card_back.getItems().add(jsonArray.getJSONObject(i).getString("name"));
                    listOfCards.put(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("img"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        choose_card_back.valueProperty().addListener((ov, t, tt)-> {
            for (ImageView card : cards) {
                card.setImage(new Image(listOfCards.get(ov.getValue())));
            }
        });
        for (ImageView card : cards) {
            card.setOnMouseEntered(e->{
                double r = new Random().nextDouble();
                double g = new Random().nextDouble();
                double b = new Random().nextDouble();
                double a = new Random().nextDouble();
                card.setEffect(new DropShadow(BlurType.GAUSSIAN, new Color(r, g, b, a), 100, 0.5, 0, 0));
            });
        }
    }
    private JSONArray getAllCardBacks(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Mashape-Key", "Q0ZvyGgPZfmshIFUmF1yWuxhPOPop1ON2nqjsnogXDUcMQ9Cdh")
                .build();
        Response response = client.newCall(request).execute();
        JSONArray jsonArray = new JSONArray(response.body().string());
        return jsonArray;
    }
}
