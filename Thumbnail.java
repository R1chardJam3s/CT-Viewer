import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import javafx.application.Application;
import javafx.beans.value.ChangeListener; 
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;  
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.*;

public class Thumbnail {

    private static short[][][] cthead;
    private static short min = -1117;
    private static short max = 2248;

    private static WritableImage[] frontView = new WritableImage[256];
    private static WritableImage[] sideView = new WritableImage[256];
    private static WritableImage[] topView = new WritableImage[113];

    private static ImageView[] display = new ImageView[256];

    private static ObservableList<String> options = 
        FXCollections.observableArrayList(
            "Side View",
            "Top View",
            "Front View"
        );
    private static final ComboBox choice = new ComboBox(options);

    public static void setData(short[][][] d) {
        cthead = d;
        generateThumbnails();
    }

    private static void generateThumbnails() {
        WritableImage w;
        PixelWriter pw;
        System.out.println(min + " " + max);
        //top view
        for(int i = 0; i < 113; i++) {
            w = new WritableImage(64,64);
            pw = w.getPixelWriter();
            float col;
            short datum;
            for (int y = 0; y < 256; y = y + 4) {
                for (int x = 0; x < 256; x = x + 4) {
                    datum=cthead[i][x][y];
				    col=(((float)datum-(float)min)/((float)(max-min)));
                    for (int c = 0; c < 3; c++) {
                        pw.setColor((x / 4), (y / 4), Color.color(col,col,col, 1.0));
                    }
                } 
            }
            topView[i] = w;
        }

        //side view
        for(int i = 0; i < 256; i++) {
            w = new WritableImage(64,30);
            pw = w.getPixelWriter();
            float col;
            short datum;
            for (int y = 0; y < 113; y = y + 4) {
                for (int x = 0; x < 256; x = x + 4) {
                    datum=cthead[y][x][i];
				    col=(((float)datum-(float)min)/((float)(max-min)));
                    for (int c = 0; c < 3; c++) {
                        pw.setColor((x / 4), (y / 4), Color.color(col,col,col, 1.0));
                    }
                } 
            }
            sideView[i] = w;
        }

        //front view
        for(int i = 0; i < 256; i++) {
            w = new WritableImage(64,30);
            pw = w.getPixelWriter();
            float col;
            short datum;
            for (int y = 0; y < 113; y = y + 4) {
                for (int x = 0; x < 256; x = x + 4) {
                    datum=cthead[y][i][x];
				    col=(((float)datum-(float)min)/((float)(max-min)));
                    for (int c = 0; c < 3; c++) {
                        pw.setColor((x / 4), (y / 4), Color.color(col,col,col, 1.0));
                    }
                } 
            }
            frontView[i] = w;
        }
    }

    public static void Display(Stage newStage) {
        newStage.setTitle("Thumbnails");

        choice.getSelectionModel().selectFirst();

        ScrollPane sp = new ScrollPane();
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        FlowPane root = new FlowPane();
        root.setVgap(8);
        root.setHgap(4);
        root.getChildren().add(choice);
        for(int i = 0; i < 256; i++) {
            display[i] = new ImageView(sideView[i]);
            root.getChildren().add(display[i]);
        }
        sp.setContent(root);

        // Create action event 
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) { 
                System.out.println(choice.getValue() + " selected");
                if(choice.getValue().equals("Top View")) {
                    for(int i = 0; i < 113; i++) {
                        display[i].setImage(topView[i]);
                    }
                    for(int j = 113; j < 256; j++) {
                        display[j].setImage(null);
                    }
                } else if(choice.getValue().equals("Side View")) {
                    for(int i = 0; i < 256; i++) {
                        display[i].setImage(sideView[i]);
                    }
                } else if(choice.getValue().equals("Front View")) {
                    for(int i = 0; i < 256; i++) {
                        display[i].setImage(frontView[i]);
                    }
                }
                
            } 
        }; 
  
        // Set on action 
        choice.setOnAction(event); 

        Scene scene = new Scene(sp, 600, 300);
        newStage.setScene(scene);
        newStage.show();
    }
}