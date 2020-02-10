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

public class Resize {
    
    private static short min = -1117;
    private static short max = 2248;

    private static WritableImage[] images = new WritableImage[3];

    private static ObservableList<String> options = 
        FXCollections.observableArrayList(
            "Side Image",
            "Top Image",
            "Front Image"
        );
    private static final ComboBox choice = new ComboBox(options);

    public static void setData(WritableImage front, WritableImage side, WritableImage top) {
        images[0] = front;
        images[1] = side;
        images[2] = top;
    }

    public static void DisplayChoice(Stage stage) {
        stage.setTitle("Resize");

        Button continue_button = new Button("Continue");

        FlowPane root = new FlowPane();
        root.setVgap(8);
        root.setHgap(4);
        root.getChildren().addAll(choice, continue_button);

        // Create action event 
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) {
                Stage newStage = new Stage();
                if(choice.getValue().equals("Top Image")) {
                    DisplayResize(newStage, images[2]);
                    stage.close();
                } else if(choice.getValue().equals("Side Image")) {
                    DisplayResize(newStage, images[1]);
                    stage.close();
                } else if(choice.getValue().equals("Front Image")) {
                    DisplayResize(newStage, images[0]);
                    stage.close();
                }
            } 
        }; 
  
        // Set on action 
        continue_button.setOnAction(event); 

        Scene scene = new Scene(root, 200, 100);
        stage.setScene(scene);
        stage.show();
    }

    public static void DisplayResize(Stage stage, WritableImage baseImage) {
        stage.setTitle("Resize");

        ImageView original = new ImageView(baseImage);

        //Slider to scale image from 0.5 to 2 (50% decrease to increase)
        //Button to confirm and opens image in new window
        //Second button for second mode

        FlowPane root = new FlowPane();
        root.setVgap(8);
        root.setHgap(4);
        root.getChildren().addAll(original);

        Scene scene = new Scene(root, 600, 300);
        stage.setScene(scene);
        stage.show();
    }
}