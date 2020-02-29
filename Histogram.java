import javafx.stage.Stage;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;

public class Histogram {
    
    static int[] histogram = new int[3366];
    static int[] t = new int[3366];
    static float[] mapping = new float[3366];

    private static short[][][] cthead;
    private static short min = -1117;
    private static short max = 2248;

    public static void create(short[][][] d) {
        cthead = d;
        for(int z = 0; z < 256; z++) {
            for(int y = 0; y < 113; y++) {
                for(int x = 0; x < 256; x++) {
                    histogram[((int) cthead[y][z][x]) + 1117]++;
                }
            }
        }
        t[0] = histogram[0];
        mapping[0] = 255.0f*(t[0]/7405568);
        for(int i = 1; i < histogram.length; i++) {
            t[i] = histogram[i] + t[i-1];
            mapping[i] = 255.0f * (t[i]/7405568f);
        }
    }

    public static void print() {
        for(int i = 0; i < histogram.length; i++) {
            System.out.println("Value: " + (i - 1117) + ", n=" + histogram[i]);
        }
    }

    public static void printT() {
        System.out.println(t[3355]);
        System.out.println(t[3356]);
        System.out.println(t[3357]);
        System.out.println(t[3358]);
        System.out.println(t[3359]);
        System.out.println(t[3360]);
        System.out.println(t[3361]);
        System.out.println(t[3362]);
        System.out.println(t[3363]);
        System.out.println(t[3364]);
        System.out.println(t[3365]);
    }

    public static void Display(Stage stage) {
        stage.setTitle("Equalised Image");

        int widthTop = 256;
        int heightTop = 256;
		WritableImage medical_image_top = new WritableImage(widthTop, heightTop);
		ImageView imageView_top = new ImageView(medical_image_top);

		int widthSide = 256;
        int heightSide = 113;
		WritableImage medical_image_side = new WritableImage(widthSide, heightSide);
		ImageView imageView_side = new ImageView(medical_image_side);

		int widthFront = 256;
        int heightFront = 113;
		WritableImage medical_image_front = new WritableImage(widthFront, heightFront);
		ImageView imageView_front = new ImageView(medical_image_front);

		//sliders to step through the slices (z and y directions) (remember 113 slices in z direction 0-112)
		Slider zslider = new Slider(0, 112, 0);
		Slider yslider = new Slider(0, 255, 0);
        Slider xslider = new Slider(0, 255, 0);
        
        zslider.valueProperty().addListener( 
            new ChangeListener<Number>() { 
				public void changed(ObservableValue <? extends Number >  
					observable, Number oldValue, Number newValue) 
            { 
				DisplayImageZ(medical_image_top, zslider);
            } 
		});
		
		yslider.valueProperty().addListener( 
            new ChangeListener<Number>() { 
				public void changed(ObservableValue <? extends Number >  
					observable, Number oldValue, Number newValue) 
            { 
				DisplayImageY(medical_image_front, yslider);
            } 
		});
		
		xslider.valueProperty().addListener( 
            new ChangeListener<Number>() { 
				public void changed(ObservableValue <? extends Number >  
					observable, Number oldValue, Number newValue) 
            { 
				DisplayImageX(medical_image_side, xslider);
            } 
        }); 

        FlowPane root = new FlowPane();
		root.setVgap(8);
        root.setHgap(4);

		root.getChildren().addAll(imageView_top, imageView_front, imageView_side, zslider, yslider, xslider);

        Scene scene = new Scene(root, 800, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void DisplayImageZ(WritableImage image, Slider s) {
		int w=(int) image.getWidth(), h=(int) image.getHeight(), i, j, c, k;
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		short datum;
		for (j=0; j<h; j++) {
				for (i=0; i<w; i++) {
                        datum=cthead[(int) s.getValue()][j][i];
                        col=mapping[datum-min]/255;
						for (c=0; c<3; c++) {
							image_writer.setColor(i, j, Color.color(col,col,col, 1.0));
						} // colour loop
				} // column loop
		} // row loop
	}

	public static void DisplayImageY(WritableImage image, Slider s) {
		int w=(int) image.getWidth(), h=(int) image.getHeight(), i, j, c, k;
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		short datum;
		for (j=0; j<h; j++) {
				for (i=0; i<w; i++) {
						datum=cthead[j][(int) s.getValue()][i];
						col=mapping[datum-min]/255;
						for (c=0; c<3; c++) {
							image_writer.setColor(i, j, Color.color(col,col,col, 1.0));
						} // colour loop
				} // column loop
		} // row loop
	}

	public static void DisplayImageX(WritableImage image, Slider s) {
		int w=(int) image.getWidth(), h=(int) image.getHeight(), i, j, c, k;
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		short datum;
		for (j=0; j<h; j++) {
				for (i=0; i<w; i++) {
						datum=cthead[j][i][(int) s.getValue()];
						col=mapping[datum-min]/255;
						for (c=0; c<3; c++) {
							image_writer.setColor(i, j, Color.color(col,col,col, 1.0));
						} // colour loop
				} // column loop
		} // row loop
	}
}