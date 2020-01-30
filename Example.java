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

// OK this is not best practice - maybe you'd like to create
// a volume data class?
// I won't give extra marks for that though.

public class Example extends Application {
	short cthead[][][]; //store the 3D volume data set
	short min, max; //min/max value in the 3D volume data set
	
	
    @Override
    public void start(Stage stage) throws FileNotFoundException, IOException {
		stage.setTitle("CThead Viewer");
		

		ReadData();


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


		Button mip_button_top = new Button("MIP Top"); //an example button to switch to MIP mode
		Button mip_button_side = new Button("MIP Side"); //an example button to switch to MIP mode
		Button mip_button_front = new Button("MIP Front"); //an example button to switch to MIP mode
		//sliders to step through the slices (z and y directions) (remember 113 slices in z direction 0-112)
		Slider zslider = new Slider(0, 112, 0);
		Slider yslider = new Slider(0, 255, 0);
		Slider xslider = new Slider(0, 255, 0);
	
		mip_button_top.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
				MIP_Top(medical_image_top);
            }
		});
		
		mip_button_front.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
				MIP_Front(medical_image_front);
            }
		});
		
		mip_button_side.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
				MIP_Side(medical_image_side);
            }
        });
		
		zslider.valueProperty().addListener( 
            new ChangeListener<Number>() { 
				public void changed(ObservableValue <? extends Number >  
					observable, Number oldValue, Number newValue) 
            { 
				System.out.println(newValue.intValue());
				DisplayImageZ(medical_image_top, zslider);
            } 
		});
		
		yslider.valueProperty().addListener( 
            new ChangeListener<Number>() { 
				public void changed(ObservableValue <? extends Number >  
					observable, Number oldValue, Number newValue) 
            { 
				System.out.println(newValue.intValue());
				DisplayImageY(medical_image_front, yslider);
            } 
		});
		
		xslider.valueProperty().addListener( 
            new ChangeListener<Number>() { 
				public void changed(ObservableValue <? extends Number >  
					observable, Number oldValue, Number newValue) 
            { 
				System.out.println(newValue.intValue());
				DisplayImageX(medical_image_side, xslider);
            } 
        }); 
		
		FlowPane root = new FlowPane();
		root.setVgap(8);
        root.setHgap(4);
//https://examples.javacodegeeks.com/desktop-java/javafx/scene/image-scene/javafx-image-example/

		root.getChildren().addAll(imageView_top, imageView_front, imageView_side, mip_button_top, mip_button_front, mip_button_side, zslider, yslider, xslider);

        Scene scene = new Scene(root, 800, 480);
        stage.setScene(scene);
        stage.show();
    }
	
	//Function to read in the cthead data set
	public void ReadData() throws IOException {
		//File name is hardcoded here - much nicer to have a dialog to select it and capture the size from the user
		File file = new File("CThead");
		//Read the data quickly via a buffer (in C++ you can just do a single fread - I couldn't find if there is an equivalent in Java)
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		
		int i, j, k; //loop through the 3D data set
		
		min=Short.MAX_VALUE; max=Short.MIN_VALUE; //set to extreme values
		short read; //value read in
		int b1, b2; //data is wrong Endian (check wikipedia) for Java so we need to swap the bytes around
		
		cthead = new short[113][256][256]; //allocate the memory - note this is fixed for this data set
		//loop through the data reading it in
		for (k=0; k<113; k++) {
			for (j=0; j<256; j++) {
				for (i=0; i<256; i++) {
					//because the Endianess is wrong, it needs to be read byte at a time and swapped
					b1=((int)in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types
					b2=((int)in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types
					read=(short)((b2<<8) | b1); //and swizzle the bytes around
					if (read<min) min=read; //update the minimum
					if (read>max) max=read; //update the maximum
					cthead[k][j][i]=read; //put the short into memory (in C++ you can replace all this code with one fread)
				}
			}
		}
		System.out.println(min+" "+max); //diagnostic - for CThead this should be -1117, 2248
		//(i.e. there are 3366 levels of grey (we are trying to display on 256 levels of grey)
		//therefore histogram equalization would be a good thing
	}

	
	 /*
        This function shows how to carry out an operation on an image.
        It obtains the dimensions of the image, and then loops through
        the image carrying out the copying of a slice of data into the
		image.
    */
    public void MIP_Top(WritableImage image) {
			//Get image dimensions, and declare loop variables
            int w=(int) image.getWidth(), h=(int) image.getHeight(), i, j, c, k;
			PixelWriter image_writer = image.getPixelWriter();
			
			float col;
			short maximum;
            //Shows how to loop through each pixel and colour
            //Try to always use j for loops in y, and i for loops in x
            //as this makes the code more readable
            for (j=0; j<h; j++) {
                    for (i=0; i<w; i++) {
							maximum = Short.MIN_VALUE;
							for(k=0; k<113; k++) {
								if(cthead[k][j][i] > maximum) {
									maximum = cthead[k][j][i];
								}
							}
							//calculate the colour by performing a mapping from [min,max] -> [0,255]
							col=(((float)maximum-(float)min)/((float)(max-min)));
                            for (c=0; c<3; c++) {
								//and now we are looping through the bgr components of the pixel
								//set the colour component c of pixel (i,j)
								image_writer.setColor(i, j, Color.color(col,col,col, 1.0));
			//					data[c+3*i+3*j*w]=(byte) col;
                            } // colour loop
                    } // column loop
			} // row loop
	}

	public void MIP_Front(WritableImage image) {
		//Get image dimensions, and declare loop variables
		int w=(int) image.getWidth(), h=(int) image.getHeight(), i, j, c, k;
		PixelWriter image_writer = image.getPixelWriter();
		
		float col;
		short maximum;
		//Shows how to loop through each pixel and colour
		//Try to always use j for loops in y, and i for loops in x
		//as this makes the code more readable
		for (j=0; j<h; j++) {
				for (i=0; i<w; i++) {
						maximum = Short.MIN_VALUE;
						for(k=0; k<256; k++) {
							if(cthead[j][k][i] > maximum) {
								maximum = cthead[j][k][i];
							}
						}
						//calculate the colour by performing a mapping from [min,max] -> [0,255]
						col=(((float)maximum-(float)min)/((float)(max-min)));
						for (c=0; c<3; c++) {
							//and now we are looping through the bgr components of the pixel
							//set the colour component c of pixel (i,j)
							image_writer.setColor(i, j, Color.color(col,col,col, 1.0));
		//					data[c+3*i+3*j*w]=(byte) col;
						} // colour loop
				} // column loop
		} // row loop
	}

	public void MIP_Side(WritableImage image) {
		//Get image dimensions, and declare loop variables
		int w=(int) image.getWidth(), h=(int) image.getHeight(), i, j, c, k;
		PixelWriter image_writer = image.getPixelWriter();
		
		float col;
		short maximum;
		//Shows how to loop through each pixel and colour
		//Try to always use j for loops in y, and i for loops in x
		//as this makes the code more readable
		for (j=0; j<h; j++) {
				for (i=0; i<w; i++) {
						maximum = Short.MIN_VALUE;
						for(k=0; k<256; k++) {
							if(cthead[j][i][k] > maximum) {
								maximum = cthead[j][i][k];
							}
						}
						//calculate the colour by performing a mapping from [min,max] -> [0,255]
						col=(((float)maximum-(float)min)/((float)(max-min)));
						for (c=0; c<3; c++) {
							//and now we are looping through the bgr components of the pixel
							//set the colour component c of pixel (i,j)
							image_writer.setColor(i, j, Color.color(col,col,col, 1.0));
		//					data[c+3*i+3*j*w]=(byte) col;
						} // colour loop
				} // column loop
		} // row loop
	}
	
	public void DisplayImageZ(WritableImage image, Slider s) {
		int w=(int) image.getWidth(), h=(int) image.getHeight(), i, j, c, k;
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		short datum;
		for (j=0; j<h; j++) {
				for (i=0; i<w; i++) {
						datum=cthead[(int) s.getValue()][j][i];
						col=(((float)datum-(float)min)/((float)(max-min)));
						for (c=0; c<3; c++) {
							image_writer.setColor(i, j, Color.color(col,col,col, 1.0));
						} // colour loop
				} // column loop
		} // row loop
	}

	public void DisplayImageY(WritableImage image, Slider s) {
		int w=(int) image.getWidth(), h=(int) image.getHeight(), i, j, c, k;
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		short datum;
		for (j=0; j<h; j++) {
				for (i=0; i<w; i++) {
						datum=cthead[j][(int) s.getValue()][i];
						col=(((float)datum-(float)min)/((float)(max-min)));
						for (c=0; c<3; c++) {
							image_writer.setColor(i, j, Color.color(col,col,col, 1.0));
						} // colour loop
				} // column loop
		} // row loop
	}

	public void DisplayImageX(WritableImage image, Slider s) {
		int w=(int) image.getWidth(), h=(int) image.getHeight(), i, j, c, k;
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		short datum;
		for (j=0; j<h; j++) {
				for (i=0; i<w; i++) {
						datum=cthead[j][i][(int) s.getValue()];
						col=(((float)datum-(float)min)/((float)(max-min)));
						for (c=0; c<3; c++) {
							image_writer.setColor(i, j, Color.color(col,col,col, 1.0));
						} // colour loop
				} // column loop
		} // row loop
	}

    public static void main(String[] args) {
        launch();
    }
}