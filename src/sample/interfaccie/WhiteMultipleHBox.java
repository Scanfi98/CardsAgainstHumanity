package sample.interfaccie;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Created by Giovanni on 24/04/2016.
 */
public class WhiteMultipleHBox extends HBox {

    public WhiteMultipleHBox(Font f, String all, double width, double heigh) {
        String[] cards = all.split("@@");

        this.setWidth(width*cards.length);
        this.setHeight(heigh);


        for (int i = 0; i < cards.length; i++){
            Label l = new Label(cards[i]);
            l.setPrefSize(width - 20, heigh - 20);
            l.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            l.setFont(f);
            l.setTextFill(Color.BLACK);
            l.setWrapText(true);
            l.setAlignment(Pos.TOP_LEFT);
            this.getChildren().add(l);
        }
    }


    public String getAllPhrase(){
        String s = "";
        for (int i = 0; i < this.getChildren().size(); i++){
            s = s + ((Label)this.getChildren().get(i)).getText() + "@@";
        }
        return s.substring(0, s.length()-2);
    }
}
