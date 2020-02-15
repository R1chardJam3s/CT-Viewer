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
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;

public class Resize {
    
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

        Slider size_slider = new Slider(1, 765, 255);
        Button confirm_button = new Button("Confirm");

        confirm_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage newStage = new Stage();
                double ratio = size_slider.getValue() / 255;
                WritableImage newImageNN = new WritableImage((int) size_slider.getValue(), (int) Math.floor((baseImage.getHeight() - 1) * ratio));
                WritableImage newImageBI = new WritableImage((int) size_slider.getValue(), (int) Math.floor((baseImage.getHeight() - 1) * ratio));
                System.out.println("Width,Height = " + newImageBI.getWidth() + "," + newImageBI.getHeight());
                PixelWriter pw = newImageNN.getPixelWriter();
                for(int y = 0; y < newImageNN.getHeight(); y++) {
                    for(int x = 0; x < newImageNN.getWidth(); x++) {
                        int newX = (int) Math.round(x / ratio);
                        int newY = (int) Math.floor(y / ratio);
                        pw.setColor(x, y, baseImage.getPixelReader().getColor(newX, newY));
                    }
                }
                pw = newImageBI.getPixelWriter();
                int oldY = 0;
                for(int y = 0; y < baseImage.getHeight(); y++) {
                    
                    int oldX = 0;

                    int newY = (int) Math.floor(y * ratio);
                    if(newY >= newImageBI.getHeight()) {
                        newY--;
                    }

                    newImageBI = bilinear_interpolation(oldY, newY, newImageBI);
                    oldY = newY;

                    for(int x = 0; x < baseImage.getWidth(); x++) {
                        int newX = (int) Math.floor(x * ratio);
                        if(newX >= newImageBI.getWidth()) {
                            newX--;
                        }
                        
                        pw.setColor(newX, newY, baseImage.getPixelReader().getColor(x, y));
                        newImageBI = linear_interpolation(oldX, newX, newY, newImageBI);
                        oldX = newX;
                    }
                }

                newStage.initModality(Modality.WINDOW_MODAL);
				newStage.initOwner(stage);
                DisplayNew(newStage, newImageNN, newImageBI);
            }
		});

        FlowPane root = new FlowPane();
        root.setVgap(8);
        root.setHgap(4);
        root.getChildren().addAll(original, size_slider, confirm_button);

        Scene scene = new Scene(root, 600, 300);
        stage.setScene(scene);
        stage.show();
    }

    public static void DisplayNew(Stage stage, WritableImage imageNN, WritableImage imageBI) {
        stage.setTitle("Resized Image");
        ImageView imageview = new ImageView(imageNN);
        ImageView imageview2 = new ImageView(imageBI);

        FlowPane root = new FlowPane();
        root.setVgap(8);
        root.setHgap(4);
        root.getChildren().addAll(imageview, imageview2);

        Scene scene = new Scene(root, (imageNN.getWidth() * 2) + 20, imageNN.getHeight() + 10);
        stage.setScene(scene);
        stage.show();
    }

    public static WritableImage linear_interpolation(int x1, int x2, int y, WritableImage newImage) {
        int x1C = newImage.getPixelReader().getArgb(x1, y);
        int x2C = newImage.getPixelReader().getArgb(x2, y);

        for(int i = x1 + 1; i < x2; i++) {
            int newColor = x1C + ((x2C - x1C)*((i - x1) / (x2 - x1)));
            newImage.getPixelWriter().setArgb(i, y, newColor);
        }

        return newImage;
    }

    public static WritableImage bilinear_interpolation(int y1, int y2, WritableImage newImage) {
        
        for(int x = 0; x < newImage.getWidth(); x++) {

            int y1C = newImage.getPixelReader().getArgb(x, y1);
            int y2C = newImage.getPixelReader().getArgb(x, y2);

            for(int i = y1 + 1; i < y2; i++) {
                int newColor = y1C + ((y2C - y1C)*((i - y1) / (y2 - y1)));
                newImage.getPixelWriter().setArgb(x, i, newColor);
            }
        }

        return newImage;
    }
}