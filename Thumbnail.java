import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import javafx.application.Application;
import javafx.beans.value.ChangeListener; 
import javafx.beans.value.ObservableValue; 
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
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
    private static short min = Short.MIN_VALUE;
    private static short max = Short.MAX_VALUE;

    private static WritableImage[] frontView = new WritableImage[256];
    private static WritableImage[] sideView = new WritableImage[256];
    private static WritableImage[] topView = new WritableImage[113];

    public static void setData(short[][][] d) {
        cthead = d;
    }

    public static void generateThumbnails() {
        WritableImage w;
        PixelWriter pw;

        //top view
        for(int i = 0; i < 113; i++) {
            w = new WritableImage(32,32);
            pw = w.getPixelWriter();
            float col;
            short datum;
            for (int y = 0; y < 256; y = y + 8) {
                for (int x = 0; x < 256; x = x + 8) {
                    datum=cthead[i][x][y];
				    col=(((float)datum-(float)min)/((float)(max-min)));
                    for (int c = 0; c < 3; c++) {
                        pw.setColor(x, y, Color.color(col,col,col, 1.0));
                    }
                } 
            }
            topView[i] = w;
        }

        //side view
        for(int i = 0; i < 256; i++) {
            w = new WritableImage(32,15);
            pw = w.getPixelWriter();
            float col;
            short datum;
            for (int y = 0; y < 113; y = y + 8) {
                for (int x = 0; x < 256; x = x + 8) {
                    datum=cthead[y][x][i];
				    col=(((float)datum-(float)min)/((float)(max-min)));
                    for (int c = 0; c < 3; c++) {
                        pw.setColor(x, y, Color.color(col,col,col, 1.0));
                    }
                } 
            }
            sideView[i] = w;
        }

        //front view
        for(int i = 0; i < 256; i++) {
            w = new WritableImage(32,15);
            pw = w.getPixelWriter();
            float col;
            short datum;
            for (int y = 0; y < 113; y = y + 8) {
                for (int x = 0; x < 256; x = x + 8) {
                    datum=cthead[y][i][x];
				    col=(((float)datum-(float)min)/((float)(max-min)));
                    for (int c = 0; c < 3; c++) {
                        pw.setColor(x, y, Color.color(col,col,col, 1.0));
                    }
                } 
            }
            frontView[i] = w;
        }
    }
}