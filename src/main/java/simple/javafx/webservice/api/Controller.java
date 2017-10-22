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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

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
    Map<String, String> listOfCards = new HashMap<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cards.add(card1); cards.add(card2); cards.add(card3); cards.add(card4);

        new Thread(() -> {
            try {
                JSONArray jsonArray = getAllCardBacks();
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
            card.setOnMouseClicked(e -> {

//                Thread thread = new Thread(()->{
//                    Timeline timeLine = new Timeline();
//                    Collection<KeyFrame> frames = timeLine.getKeyFrames();
//                    Duration frameGap = Duration.millis(10);
//                    Duration frameTime = Duration.ZERO;
//                    for (ImageView img : cards) {
//                        frameTime = frameTime.add(frameGap);
//                        frames.add(new KeyFrame(frameTime, eee -> {
//                            List<String> valuesList = new ArrayList<>(listOfCards.values());
//                            Collections.shuffle( valuesList );
//                            card1.setImage(new Image(valuesList.get(0)));
//                        }));
//                    }
//                    timeLine.setCycleCount(10);
//                    timeLine.play();
//                });
//                thread.start();

                try {
                    JSONObject jsonObject = getAllCards();
                    for (int i = 0; i < jsonObject.length() ; i++) {
                        //System.out.println(jsonObject.getJSONArray("Basic").getJSONObject(i).getString("name"));
                       String jsonArray = getSingleCardByName("Ysera").getJSONObject(i).getString("img");
                       // TODO read from arrayBySingleCard
                     //   System.out.println(jsonArray);
                    }
                   } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    getAllCards();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
        }
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
    private JSONObject getAllCards() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://omgvamp-hearthstone-v1.p.mashape.com/cards")
                .addHeader("X-Mashape-Key", "Q0ZvyGgPZfmshIFUmF1yWuxhPOPop1ON2nqjsnogXDUcMQ9Cdh")
                .build();
        Response response = client.newCall(request).execute();
        //System.out.println(response.body().string());
        return new JSONObject(response.body().string());
    }
    private JSONArray getSingleCardByName(String name) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://omgvamp-hearthstone-v1.p.mashape.com/cards/"+name)
                .addHeader("X-Mashape-Key", "Q0ZvyGgPZfmshIFUmF1yWuxhPOPop1ON2nqjsnogXDUcMQ9Cdh")
                .build();
        Response response = client.newCall(request).execute();
        //System.out.println(response.body().string());
        return new JSONArray(response.body().string());
    }


}
