import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.io.File; 
import java.util.Arrays; 
import java.awt.image.BufferedImage; 
import javax.imageio.plugins.jpeg.*; 
import javax.imageio.*; 
import javax.imageio.stream.*; 
import ddf.minim.*; 
import ddf.minim.effects.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class visInterface extends PApplet {

//  ---------------- digital spaces
//  ---------------- data interface
//  ---------------- visualising data from wifi signals and electro magnetic fields
//  ................ data examples provided as recorded during feb - july 2019 using several versions of the developed device
//  ---------------- october 2019
//  ---------------- andrés villa torres
//  ---------------- ndr3s -v -t
//  ---------------- villatorres@collegium.ethz.ch
//  ---------------- andres.villa_torres@zhdk.ch
//  ---------------- andresvillatorres@gmail.com
//  ................ https://github.com/andresvillatorres/Digital_Signals_Recorder




// ---------------- loading libraries necessary for doing the jpeg compression







PImage imgExample;
PImage contextImage;
Float [] points;
Float [] yPoints;

Float [] liquidX;
Float [] liquidZ;

float scrollUpDown=0;
float rotaryUpDown=0;
float mon1Scale=1.0f;
float mon2Scale=1.0f;
float scaleAllFactor=0.75f;

PFont font144,font136,font128,font111,font96,font72,font48,font36,font24,font18,font16,font14,font12,font10,font8;
PVector [] pointPos;
PVector [] prevPointPos;
int amountSamples=0;

//  ---------------- main initialization

public void setup(){

	
	noCursor();
	pathGraph = createGraphics(width,height,P2D);
	wifiSignalGraphic = createGraphics(width,height,P2D);
	satellitesGraphic = createGraphics(width,height,P2D);
	gpsMapGraphic = createGraphics(width,height,P2D);
	emfGraphic = createGraphics(width,height,P2D);
	postPosXwiFi=width;
	posXwiFi=width;
	controlsPx=width;
	oControlsPx=width;
	loadAllFonts();
	loadMapImg();
	
}

//  ---------------- main draw loop

int countBeforeLoad=0;
public void draw(){
	background(0);
	allDrawingFunctions();

}


// ----------------- all drawing functions
public void allDrawingFunctions(){
	displayMapImg();
	
	// ---------- displaying notifications when data is not loaded yet

	if(!dataLoaded){

		fill(255);
		textFont(font16,16);
		text("Please wait till data is loaded ...",25,70);

		text("Press 'f' to select data base manually", 25, 130);
		text("Press 'w' to switch wifi tracked labels on/off", 25,160);
		text("Press 'p' to switch pointer on/off",25,190);
		text("Press 'd' to switch debugging on/off",25,220 );
		text("Press 'l' or double click mouse to go to Location on google maps ",25,250 );
		text("Press 'c' to switch controls on/off ",25,280 );
		text("Scroll up and down to go through recorded points",25,310 );

		text(idxComp + " images from " + amountSamples + " compressed and loaded ",25,370 );
		// text("Thanks for your patience, this only happens once per data base",25,400);

			if(!initialLoad){
				setupDataPath();
				String dummyImage="foto.jpg";
				asynchronousImage = requestImage(dummyImage);
				lastAsynchImage=asynchronousImage;
				initPointInCase();
				setupMap();
				updatePlaces();
				updateMap();
				imgExample=loadImage("foto.jpg");
				contextImage=imgExample;
				imgExample.resize(2592/8,1944/8);
				setWiFiText();
				setupSatellite();

			}else{
				countBeforeLoad=0;
				String dummyImage="foto.jpg";
				asynchronousImage = requestImage(dummyImage);
				lastAsynchImage=asynchronousImage;
				initPointInCase();
				setupMap();
				updatePlaces();
				updateMap();
				imgExample=loadImage("foto.jpg");
				contextImage=imgExample;
				imgExample.resize(2592/8,1944/8);
				setWiFiText();
				setupSatellite();
			}



	}
	// ------------------ displaying the rest once the data was loaded
	else{
		if(lastWhich!=which){
			updateGraphics();	
		}
		displayContextImage();
		audioPlay(which);
		audioGetVal();
		displaySingleSignals();
		displaySatellites();
		displayMap();	
		displayContextImageLabel();
		updateWiFiTexts();
		drawPath();
		displayEMFInformation();
		displayGPSInfoText();
		displaySatText();
		displayTimeStampText();
		audioDisplay(width - 720*0.5f - 25,150);
		displayControls();	
		displayCurrentMarker();
	}
	displayDebuggingText();
	displayPointer();
}


//  ---------------- loading fonts
public void loadAllFonts(){


	font8=loadFont("fonts/Menlo-Regular-8.vlw");
	font10=loadFont("fonts/Menlo-Regular-10.vlw");
	font12=loadFont("fonts/Menlo-Regular-12.vlw");
	font14=loadFont("fonts/Menlo-Regular-14.vlw");
	font16=loadFont("fonts/Menlo-Regular-16.vlw");
	font18=loadFont("fonts/Menlo-Regular-18.vlw");
	font24=loadFont("fonts/Menlo-Regular-24.vlw");
	font36=loadFont("fonts/Menlo-Regular-36.vlw");
	font48=loadFont("fonts/Menlo-Regular-48.vlw");
	font72=loadFont("fonts/Menlo-Regular-72.vlw");
	font96=loadFont("fonts/Menlo-Regular-96.vlw");
	font111=loadFont("fonts/Menlo-Regular-111.vlw");
	font128=loadFont("fonts/Menlo-Regular-128.vlw");
	font136=loadFont("fonts/Menlo-Regular-136.vlw");
	font144=loadFont("fonts/Menlo-Regular-144.vlw");


}

//  ---------------- timeStamp information

String timeStamp="2019.02.19,15:03:34";
public void displayTimeStampText(){
	fill(255);
	textFont(font72,72);
	textAlign(LEFT,TOP);
	text(extractTime,25-7,25);
	textAlign(LEFT,TOP);
	text("# " + which,1055, height-96);
}



//  ---------------- update image and image information

public void updateContextImage(){
	contextImage= imgExample;
}


// ----------------- display image and image information

boolean imgRequested=false;
int lastReqWhich=0;
PImage lastAsynchImage;
public void displayContextImage(){
	if(!dataLoaded){

	}	else{		
		if(which!= lastReqWhich ){

			asynchronousImage= requestImage(imgData[which]);
			lastReqWhich=which;

		}
		if(asynchronousImage.width>0){
			image(asynchronousImage,width - 720*0.5f - 25,25,720*0.5f,540*0.5f);
			lastAsynchImage=asynchronousImage;
		}else{
			if(lastAsynchImage.width>0){
				image(lastAsynchImage,width - 720*0.5f - 25,25,720*0.5f,540*0.5f);
			}
		}
	}

}

//  --------------- changing the label of the city according to the region in case

String locationLabel="Xilitla";
int oldWhichCity=-1;
public void displayContextImageLabel(){
	if(oldWhichCity!=whichCity){
		switch(whichCity) {
		  	case 0: 
		    // println("cdmx");  // Does not execute
		    locationLabel="Mexico City";
		    break;
		  	case 1: 
		    // println("zurich");  // Prints "One"
		    locationLabel="Zurich";
		    break;
		    case 2: 
		    // println("xilitla");  // Prints "One"
		    locationLabel="Xilitla";
		    break;
		    case 3: 
		    // println("hidalgo");  // Prints "One"
		    locationLabel="Hidalgo";
		    break;
		    case 4: 
		    // println("milano");  // Prints "One"
		    locationLabel="Milano";
		    break;
		    case 5: 
		    // println("schoenenwerd");  // Prints "One"
		    locationLabel="Schoenenwerd";
		    break;
		}
	}
	fill(255);
	textFont(font72,72);
	textAlign(LEFT,TOP);
	text(locationLabel, 25,120);
	oldWhichCity=whichCity;
	
}



//  ---------------- display wifi Information 

int wifiAmount = 17;
String [] wifiID;
int [] wifiStrength;

public void setWiFiText(){
	wifiID= new String[wifiAmount];
	wifiStrength = new int[wifiAmount];

	for(int i = 0; i < wifiAmount; i ++){
		wifiID[i] = "sdfñkaj" + i ;
		wifiStrength[i] = PApplet.parseInt(random(-90,-30));
	}
}


public void displayWiFiText(){

	fill(255);
	textFont(font12,12);
	textAlign(LEFT,TOP);
	for(int i= 0; i< wifiAmount; i ++){
		text("SSID : " + wifiID[i],25,160 + (15*i));
		text(wifiStrength[i] + " dBm",325,160 + (15*i));
	}
	
}

//  ---------------- debugging information

public void displayDebuggingText(){
	rectMode(CORNER);
	if(debug){
		float _x = 0;
		float _y = height-400;
		fill(255,0,0); // RED
		textFont(font12,12);
		textAlign(LEFT,TOP);
		text("frameRate : " + frameRate, _x + 25, _y + 100);
		text("scroll value : " + scrollUpDown, _x + 25, _y + 115);
		text("mouse x : " + mouseX + " , mouse y : " + mouseY, _x + 25, _y + 130);
	  	text("rec ID : " + which, _x+25, _y + 145);
  		text("signal sum : " + (wifiSum) + " mW ", _x + 25, _y + 160);
  		text("signal sum map : " + wifiSumNormal + " mW normal ", _x + 25, _y + 175);
  		text("mapped long : " + actualGPS.x +  "mapped lat : " +  actualGPS.y, _x + 25, _y + 190);

  		noFill();
  		stroke(255,0,0);
  		strokeWeight(0.5f);
  		line(mouseX,0,mouseX,height);
  		line(0,mouseY,width,mouseY);

  		textFont(font12,12);
  		String locationText = "Lat : " + mapYtoLat(mouseY) + " , Long : " + mapXtoLong(mouseX ) ;
  		text(locationText, mouseX+5,mouseY-25);
	}
}

//  ---------------- interactions through mousewheel

public void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  scrollUpDown=scrollUpDown+e;
  which = which + PApplet.parseInt(e);
  which = constrain(which, 0, amountSamples-1);

}

//  ---------------- display pointer
public void displayPointer(){
	if(showPointer && !debug){
		noFill();
  		stroke(67, 90, 137);
  		strokeWeight(0.5f);
  		line(mouseX,0,mouseX,height);
  		line(0,mouseY,width,mouseY);
  		
  		fill(67,90,137);
  		textFont(font12,12);
  		String locationText = "Lat : " + mapYtoLat(mouseY) + " , Long : " + mapXtoLong(mouseX ) ;
  		text(locationText, mouseX+5,mouseY-25);
	}
}

// -------------- display controls
float controlsPx=0;
float oControlsPx=0;
public void displayControls(){
		oControlsPx=controlsPx*0.25f + oControlsPx*0.75f;
		pushMatrix();
		translate(oControlsPx,0);
		noStroke();
		fill(0,240);
		rect(0,0,width,height);
		fill(255);
		textFont(font16,16);
		text("Controls : ",25,70);
		text("Press 'f' to select data base manually", 25, 130);
		text("Press 'w' to switch wifi tracked labels on/off", 25,160);
		text("Press 'p' to switch pointer on/off",25,190);
		text("Press 'd' to switch debugging on/off",25,220 );
		text("Press 'l' or double click mouse to go to Location on google maps ",25,250 );
		text("Press 'c' to switch controls on/off ",25,280 );
		text("Scroll up and down to go through recorded points",25,310 );
		popMatrix();
		
		
}


//  ---------------- mouse pressed, mouse clicked and keypressed

boolean showEMF=false;
boolean debug=false;
boolean showWiFi=false;
boolean showAudio=true;
boolean showPointer = false;
boolean showControls = false;
public void keyPressed(){

	if (key == 'd' || key == 'D') {
		debug=!debug;
	}

	if (key == 'w' || key == 'W') {
		showWiFi=!showWiFi;
		if(!showWiFi){
			postPosXwiFi=width;
		}else{
			postPosXwiFi=0;
		}
	}

	if(key == 'a' || key =='A'){
		showAudio=!showAudio;
	}
	if(key == 'p' || key =='P'){
		showPointer=!showPointer;
	}
	String whichDir ="";
  	if (key == CODED) {
    	if (keyCode == UP) {
     		whichDir="UP";
     		if(which<amountSamples-1){
     			which++;
     		}
   		}
    	if (keyCode == DOWN) {
      		whichDir="DOWN";
      		if(which>0){
      			which--;
      		}
    	}
    	if (keyCode == LEFT) {
      		whichDir="LEFT";
      		if(which>0){
      			which--;
      		}
    	} 
    	if (keyCode == RIGHT) {
      		whichDir="RIGHT";
      		if(which<amountSamples-1){
     			which++;
     		}
    	} 
  	} 
  	if(key == 'l' || key == 'L'){
  		String goToLat= str(mapYtoLat(mouseY));
  		String goToLong= str(mapXtoLong(mouseX));
  		link("https://www.google.com/maps/place/"+goToLat+","+goToLong+"/z9");
  	}
  	if(key == 'f' || key == 'F'){
  		selectFolder("Select a folder to process:", "folderSelected");
  	}	
  	if(key == 'c' || key == 'C'){
  		showControls=!showControls;
  		if(!showControls){
			controlsPx=width;
		}else{
			controlsPx=0;
		}
  	}
}


public void mouseClicked(MouseEvent evt) {
  if (evt.getCount() == 2)doubleClicked();
}

public void doubleClicked(){
	String goToLat= str(mapYtoLat(mouseY));
  	String goToLong= str(mapXtoLong(mouseX));
	link("https://www.google.com/maps/place/"+goToLat+","+goToLong+"/z9");
}

//  ---------------- digital spaces
//  ---------------- data interface
//  ---------------- visualising data from wifi signals and electro magnetic fields
//  ................ data examples provided as recorded during feb - july 2019 using several versions of the developed device
//  ---------------- october 2019
//  ---------------- andrés villa torres
//  ---------------- ndr3s -v -t
//  ---------------- villatorres@collegium.ethz.ch
//  ---------------- andres.villa_torres@zhdk.ch
//  ---------------- andresvillatorres@gmail.com
//  ................ https://github.com/andresvillatorres/Digital_Signals_Recorder

boolean dataPathProvided=false;
boolean dataLoaded=false;
float szPD=180;
float szPDO=180;

PImage [] images;
String [] wifiIDsData;
String [] wifiStrngthData;
PVector [] gpsPoints;
String [] gpsData;
String [] audioData;
Minim [] soundFile;
AudioPlayer [] soundPlayer;
String [] folderNames;
String [] imgData;
String [] emfData;

int which=1;
int lastWhich=0;
PImage asynchronousImage;
boolean initialLoad=false;

// ---------------- setting up the data path to load the data

public void setupDataPath(){
	// loading the data path directly
	dataPathProvided=true;

	// uncomment this to select data path at start
	// selectFolder("Select a folder to process:", "folderSelected");
	
	// --------- provide your data path or select by start
// println(sketchPath());
	updateDataPath(sketchPath()+"/data/sampleData/zurich17Oct2019");
	// selectFolder("Select a folder to process:", "folderSelected");
	// updateDataPath("/Users/avillato/Desktop/digital_spaces_final_interface/backup_mac_mini/final_data/ORIGINAL/zurich_short");
	initialLoad=true;

}


// //////////////////////////////////////////////
// //////////////////////////////////////////////
// //////////////////////////////////////////////


//  ---------------- function for updating the location of data base

public void updateDataPath(String selectedDataPath){

	if(dataPathProvided){
		pathDrawn=false;
		emfOneTime=false;

		idxComp=0;
		which=1;
		lastWhich=0;
		amountSamples=0;
		java.io.File folder = new java.io.File(dataPath(selectedDataPath));// "" ----> stands for default data folder
	
		// list the files in the data folder
		String[] filenames = folder.list();
		println("amount of rec moments : " + filenames.length);
		Arrays.sort(filenames);


		for (int i=0; i< filenames.length; i++) {
			if (filenames[i].equals(".DS_Store") ==false) {
			  amountSamples=amountSamples+1;
			}else{
				println( "this wont be included    " + " index :  " + i+ " , filename : ' " + filenames[i] + " ' ");
			}
		}
	
		println("amount of images to be loaded :  " + amountSamples );
		fixedAudio = new String[amountSamples];
		images  = new PImage[amountSamples];
		emfData = new String[amountSamples];
		gpsData = new String[amountSamples];
		wifiIDsData = new String[amountSamples];
		wifiStrngthData = new String[amountSamples];
		folderNames = new String[amountSamples];
		audioData = new String[amountSamples];
		imgData = new String[amountSamples];
		gpsPoints = new PVector[amountSamples];
		soundFile = new Minim[amountSamples];
	    soundPlayer = new AudioPlayer[amountSamples];

		for(int i=0; i < gpsPoints.length;i++){
			gpsPoints[i]=new PVector(0.0f,0.0f,0.0f);
		}


		for (int i=0; i<amountSamples+1; i++) { 
		String ifCompFExists= selectedDataPath+"/"+filenames[i]+"/fotoCompressed.jpg";
		String compPath=selectedDataPath+"/"+filenames[i];
		String emfPath = selectedDataPath + "/" + filenames[i]+"/Spectrum_EMF_FFT.csv";
		//  ------ swap commenting imgPath if you'd prefer to load uncompressed version of the foto -->
		// String imgPath=selectedDataPath+"/"+filenames[i]+"/foto.jpg";
		String imgPath=selectedDataPath+"/"+filenames[i]+"/fotoCompressed.jpg";
		
		String gpsPath=selectedDataPath+"/"+filenames[i]+"/GPSData.csv";
		String wifiIDsPath=selectedDataPath+"/"+filenames[i]+"/WiFi_Ids.csv";
		String wifiStrngthPath=selectedDataPath+"/"+filenames[i]+"/WiFi_Sig_Power.csv";
		String folderPath = filenames[i];
		String audioPath=selectedDataPath+"/"+filenames[i] + "/audioR_5_sec.wav";


			if (filenames[i].equals(".DS_Store") ==false) {
				File ffTest = new File(ifCompFExists);

				// this loop compresses the images in case they haven't been yet...
				// it takes about 200 milliseconds per image 
				// after that accessing the data is very quick
				if(  ffTest.exists() ) {
					// ---- uncomment both lines if you want to delete the compressed image -->
					// ---- println(" deleted : " + ffTest);
					// deleteFile(ffTest);					
				}else{
					// ---- comment if you don't want to perform the compression -->
			  		compressImage(compPath);
				}
				folderNames[i-1]= folderPath;
				gpsData[i-1] = dataPath(gpsPath);
				wifiIDsData[i-1]=dataPath(wifiIDsPath);
				wifiStrngthData[i-1]=dataPath(wifiStrngthPath);
				audioData[i-1]=dataPath(audioPath);
				audioLoad(i-1, audioData[i-1]);
				imgData[i-1]= imgPath;
				emfData[i-1]=emfPath;

			 
			}
		}
		dataLoaded=true;
		asynchronousImage= requestImage(imgData[which]);

		
	}


}

//  ---------------- function for opening specific location for data base

public void folderSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
  	dataLoaded=false;

    println("User selected : " + selection.getAbsolutePath());
    updateDataPath(selection.getAbsolutePath());
  }
}


//  ---------------- functions for compressing and reducing size from jpeg images

float compressionLevel = 0.5f;
String o_path_comp="";
int idxComp=0;

// code part referenced from
// jeff thompson
// https://gist.github.com/jeffThompson/ea54b5ea40482ec896d1e6f9f266c731
// edited by andrés villa torres /\ ndr3s -v -t



//  ---------------- actual compression function
int timeCompressionMillis=0;
int sizeFactor=8;
public void compressImage(String path){
  timeCompressionMillis=millis();
  String imageFile = path + "/foto.jpg";   
  // String outputFilename = sketchPath("") + "compressed.jpg";
  String outputFilename =path+ "/fotoCompressed.jpg";
   try {
      // setup JPG output
      JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
      jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      jpegParams.setCompressionQuality(compressionLevel);
      final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
      writer.setOutput(new FileImageOutputStream(new File(outputFilename)));
      // load image to compress
      // alternatively, you could access the sketch's pixel array and save it
      PImage img = loadImage(imageFile);
      // resizing image to make it really small
      img.resize(img.width/sizeFactor,img.height/sizeFactor);
      img.loadPixels();
      // output to BufferedImage object
      // create the object, then write the pixel data to it
      BufferedImage out = new BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB);
      for (int i=0; i<img.pixels.length; i++) {
        out.setRGB(i%img.width, i/img.width, img.pixels[i]);
      }
      // save it!
      writer.write(null, new IIOImage(out, null, null), jpegParams);
      println("compressed image Saved as : " + outputFilename  + " , index : " + idxComp);
    }
    catch (Exception e) {
      println("Problem saving... :(");
      println(e);
    }
    timeCompressionMillis=( millis()- timeCompressionMillis);
    println("time till saved : " + timeCompressionMillis  );
    idxComp++;
}


//  ---------------- function for deleting files

 public void deleteFile(File toDelete){
	toDelete.delete();
}


//  ---------------- digital spaces
//  ---------------- data interface
//  ---------------- visualising data from wifi signals and electro magnetic fields
//  ................ data examples provided as recorded during feb - july 2019 using several versions of the developed device
//  ---------------- october 2019
//  ---------------- andrés villa torres
//  ---------------- ndr3s -v -t
//  ---------------- villatorres@collegium.ethz.ch
//  ---------------- andres.villa_torres@zhdk.ch
//  ---------------- andresvillatorres@gmail.com
//  ................ https://github.com/andresvillatorres/Digital_Signals_Recorder

PGraphics gpsMapGraphic;
float difx1, difx2, difx3, dify1, dify2, dify3;
float compXA, compYA, compXB, compYB;
float refAx,refAy,refBx,refBy;
float pointALat;
float pointALong;
float pointBLat;
float pointBLong;
float north;
float south;
float west;
float east;

PVector mapPointA,mapPointB;
PVector actualGPS;

// -------- initializing values related to the relative lower-left (pointA) and upper-right(pointB) corners from the screen
// -------- in relation to coordinate regions
// -------- these variate dinamically according to the recorded data
// -------- in this version the regions must be manually identified with the help of 
// -------- cartographic data software such as google maps or open street map

public void setupMap(){

	actualGPS = new PVector(0,0);
	pointALat = 19.423453f;
	pointALong= -99.166108f;
	pointBLat=19.413945f;
	pointBLong=-99.135857f;

}



// -------- dynaic regions for readjusting the map corners according to six regions mwhich have been
// -------- manually identified by andrés villa torres
// -------- these six regions correspond to the sample data bodies provided by andrés which have been recorded using diverse
// -------- versions of the signal recording device

int whichCity=0;
public void dynamicMapRegion(){
	//CDMX
	if(latitude>18.0f && latitude < 19.98f){
		
		whichCity=0;
		pointALat = 19.424401f;
		pointALong = -99.166304f;

	// 19.424401, -99.166304

		// 19.424425, -99.166238 calle londres / eje 2
		// pointALat =19.423453;
		// pointALong=-99.166108;
		// 19.427024, -99.167676 - Angel de la independencia
		pointBLat = 19.414634f;
		pointBLong = -99.148072f;
 
		// 19.414634, -99.148072

		// pointBLat=19.413945;
		// pointBLong=-99.135857;
		// 19.417371, -99.149230 Dr Jimenez 140, Doctores, 06720 Ciudad de México, CDMX, Mexico
	}
		
	//ZURICH
	if(latitude>44.0f && latitude < 50.0f && longitude>8.5f){
		whichCity=1;
		// pointALat = 47.35; 	// yA
		pointALat = 47.341358f;
		pointALong = 8.508f; // xA
		pointBLat = 47.41f; 	// yB	
		pointBLong = 8.57f; 	// xB
	}
	//XILITLA
	if(latitude>21.0f && latitude < 23.0f){
		whichCity=2;
		pointALat=21.6f;
		// pointALong=-98.999;
		pointBLat=21.35f;
		// pointBLong=-98.979;
		pointALong=-98.999176667f;
		pointBLong=-98.902991667f;
	}
	//PACHUCA
	if(latitude>19.98f && latitude < 21.0f){
		whichCity=3;
		pointALat=20.155703f;
		pointALong=-98.895405f;
		pointBLat=19.98f;
		pointBLong=-98.685398f;
	}
	// MILANO
	if(latitude>45.483f && latitude < 45.49f){
		whichCity=4;
		pointALat=45.473540f;
		pointALong=9.155545f; 
		pointBLat=45.498775f;
		pointBLong=9.220816f;  
	}

	// SCHOENENWERD
	if(latitude>47.36f && latitude < 47.38f && longitude<8.1f){
		whichCity=5;
		pointALat=47.364746f;
		pointALong=7.981519f; 
		pointBLat=47.383415f;
		pointBLong=8.030357f;  
	}

}

// -------- this function is adapting the  region orientations ( -/+ degrees) which variate according to the positioning in the meridian and equator
// -------- these regions have been manually identified by andrés villa torres
// -------- these six regions correspond to the sample data bodies provided by andrés which have been recorded using diverse
// -------- versions of the signal recording device while developing and testing

public void updateGPSOrientation(){

	if(whichCity==0){ //CDMX
		float actualGPSx=map(longitude,pointALong, pointBLong,0+gpsMarginsWidth,gpsMapGraphic.width-gpsMarginsWidth);
		float actualGPSy=map(latitude,pointALat, pointBLat,0+gpsMarginsHeight,gpsMapGraphic.height-gpsMarginsHeight);
		actualGPS = new PVector(actualGPSx,actualGPSy);

	}
	if(whichCity==1){ //Zurich
		float actualGPSx=map(longitude,pointALong, pointBLong, 0+gpsMarginsWidth,gpsMapGraphic.width-gpsMarginsWidth);
		float actualGPSy=map(latitude,pointALat, pointBLat, gpsMapGraphic.height-gpsMarginsHeight,0+gpsMarginsHeight);
		actualGPS = new PVector(actualGPSx,actualGPSy);
	}
	if(whichCity==2){ //Xilitla
		float actualGPSx=map(longitude,pointALong, pointBLong, 0+gpsMarginsWidth,gpsMapGraphic.width-gpsMarginsWidth);
		float actualGPSy=map(latitude,pointALat, pointBLat,0+gpsMarginsHeight,gpsMapGraphic.height-gpsMarginsHeight);
		actualGPS = new PVector(actualGPSx,actualGPSy);
	}
	if(whichCity==3){ //Pachuca
		float actualGPSx=map(longitude,pointALong, pointBLong, 0+gpsMarginsWidth,gpsMapGraphic.width-gpsMarginsWidth);
		float actualGPSy=map(latitude,pointALat, pointBLat,0+gpsMarginsHeight,gpsMapGraphic.height-gpsMarginsHeight);
		actualGPS = new PVector(actualGPSx,actualGPSy);
	}
	if(whichCity==4){ //Milano
		float actualGPSx=map(longitude,pointALong, pointBLong, 0+gpsMarginsWidth,gpsMapGraphic.width-gpsMarginsWidth);
		float actualGPSy=map(latitude,pointALat, pointBLat, gpsMapGraphic.height-gpsMarginsHeight,0+gpsMarginsHeight);
		actualGPS = new PVector(actualGPSx,actualGPSy);
	}
	if(whichCity==5){ //Schoenenwerd
		float actualGPSx=map(longitude,pointALong, pointBLong, 0+gpsMarginsWidth,gpsMapGraphic.width-gpsMarginsWidth);
		float actualGPSy=map(latitude,pointALat, pointBLat, gpsMapGraphic.height-gpsMarginsHeight,0+gpsMarginsHeight);
		actualGPS = new PVector(actualGPSx,actualGPSy);
	}

}

// ---------------- this function gets the location points from distinctive places and arrange them in the map
// ---------------- the coordinates from the interest points is extracted from the "places.csv" provided inside the data folder 
// ---------------- in the processing sketch

public void updateMap(){

	gpsMapGraphic.beginDraw();
	gpsMapGraphic.background(0);
	gpsMapGraphic.noFill();
	gpsMapGraphic.stroke(255,80);
	gpsMapGraphic.strokeWeight(1);
	// gpsMapGraphic.line(mapPointA.x,mapPointA.y,mapPointB.x,mapPointB.y);

	// drawing the city gps ref points in the map
	
	for (int i=0; i < places.length;i++){

		gpsMapGraphic.fill(67, 90, 137);
		gpsMapGraphic.noStroke();
		gpsMapGraphic.ellipse(places[i].x, places[i].y,8,8);
		gpsMapGraphic.textFont(font12,12);
		gpsMapGraphic.text(placesLabel[i], places[i].x+12, places[i].y+4 );
	}
	
	
	gpsMapGraphic.endDraw();
}

// ---------------- showing current location marker

float smoothGPSx=0;
float smoothGPSy=0;
public void displayCurrentMarker(){


	smoothGPSx=smoothGPSx*0.85f + actualGPS.x*0.15f;
	smoothGPSy=smoothGPSy*0.85f + actualGPS.y*0.15f;
	fill(0, 3, 20);
	stroke(255);
	strokeWeight(2);
	ellipse(smoothGPSx,smoothGPSy,16,8);

	stroke(255);
	strokeWeight(2);
	line(smoothGPSx,smoothGPSy-75,smoothGPSx,smoothGPSy);

	fill(0, 3, 20);
	stroke(255);
	strokeWeight(2);
	ellipse(smoothGPSx,smoothGPSy-75,35,35);
	

	// --- uncomment and comment the upper one to display marker without smoothing position

	// fill(255, 66, 132);
	// noStroke();
	// ellipse(actualGPS.x,actualGPS.y,12,6);
	// ellipse(actualGPS.x,actualGPS.y-75,35,35);
	// stroke(255, 66, 132);
	// strokeWeight(1);
	// line(actualGPS.x,actualGPS.y-75,actualGPS.x,actualGPS.y);



}

// ----------------  drawing the map 

public void displayMap(){
	blendMode(ADD);
	image(gpsMapGraphic,0,0,width,height);
	blendMode(BLEND);
}



PVector [] places;
String [] placesLabel;

// ---------------- updating the list of places

public void updatePlaces(){

	String [] lines = loadStrings("placesList/places.csv");
	places= new PVector[lines.length];
	placesLabel= new String[lines.length];
	int _indx=0;
    for (String line : lines) {
          String[] pieces = split(line, ',');
         
         	for(int i = 0; i< pieces.length;i++){
         		if(i==0){
         			placesLabel[_indx]=pieces[i];
         			// places[_indx] = new PVector(200,200);
         			places[_indx] = new PVector( mapLongToGPSMap(PApplet.parseFloat(pieces[i+2])), mapLatToGPSMap(PApplet.parseFloat(pieces[i+1])) );
         		}
         	}
         	_indx++;
    }
}


PGraphics pathGraph;
int  path_G_Which=0;
boolean pathDrawn = false;
PVector oldPathPos;
float oldFLong=0;
float oldFLat=0;

//  ---------------- draw recorded path
public void drawPath(){

	if(!pathDrawn){
		pathDrawn=true;
		pathGraph.beginDraw();
		pathGraph.clear();
		pathGraph.noFill();
		pathGraph.stroke(255,100,100);
		pathGraph.strokeWeight(5);
		for(int h=0; h< amountSamples;h++){
			String [] lines = loadStrings(gpsData[h]);
			float fLong=0,fLat=0;
			for (int i = 0; i < lines.length; i++) {
				String[] list = split(lines[i], ',');
				
				// float oldFLong=0, oldFLat=0;
				for (int j= 0; j< list.length; j++) {
			       
					// extracting longitude
					if (j==2) {
						String[] lst = split(list[j], ':');
						for (int k=0; k<lst.length; k++) {
						 	fLong=(PApplet.parseFloat(lst[lst.length-1]));
						}
					}  
					// extracting latitude
					if (j==3) {
						String[] lst = split(list[j], ':');
						for (int k=0; k<lst.length; k++) {
					  		fLat=(PApplet.parseFloat(lst[lst.length-1]));
						}
					}      
				}
				pathGraph.stroke(255,100,100);
				pathGraph.strokeWeight(5);
				pathGraph.noFill();
				pathGraph.point(mapLongToGPSMap(fLong),mapLatToGPSMap(fLat));

					
			}
			if(h>0){
				pathGraph.stroke(255,100,100,150);
				pathGraph.strokeWeight(2);
				
				// --------- checking that there are not NULL or NaN calculations or coordinate values beyond the screen, this happens when GPS data is missing

				if(Float.isNaN(mapLongToGPSMap(fLong)) || Float.isNaN(mapLatToGPSMap(fLat)) || mapLongToGPSMap(fLong)<0 || mapLongToGPSMap(fLong)> width || mapLatToGPSMap(fLat)<0 || mapLatToGPSMap(fLat)>height){

					// pathGraph.line(mapLongToGPSMap(fLong),mapLatToGPSMap(fLat),oldPathPos.x,oldPathPos.y);
				}else{
					
					//  ------- printing x and y values for the path coordinates to check in case something looks weird
					println( h + " , x : " + oldPathPos.x + " , y : " + oldPathPos.y);

					pathGraph.line(mapLongToGPSMap(fLong),mapLatToGPSMap(fLat),oldPathPos.x,oldPathPos.y);
				}
				
			}
			// --------- checking that there are not NULL or NaN calculations or coordinate values beyond the screen, this happens when GPS data is missing
			if(Float.isNaN(mapLongToGPSMap(fLong)) || Float.isNaN(mapLatToGPSMap(fLat)) || mapLongToGPSMap(fLong)<0 || mapLongToGPSMap(fLong)> width || mapLatToGPSMap(fLat)<0 || mapLatToGPSMap(fLat)>height){
				// ---- in case there is something strange in the coordinate calculations will access the previous ones
				oldPathPos= new PVector(mapLongToGPSMap(oldFLong),mapLatToGPSMap(oldFLat));
			}else{
				oldPathPos= new PVector(mapLongToGPSMap(fLong),mapLatToGPSMap(fLat));
				oldFLong=fLong;
				oldFLat=fLat;
			}
				

		}
		pathGraph.endDraw();
		println("path drawn");
	}else{
		image(pathGraph,0,0,width,height);
	}
	path_G_Which=which;
}







//  ---------------- functions for mapping longitude and lattitude coordinates to viewer coordinates

float gpsMarginsHeight=0.0f;
float gpsMarginsWidth=0.0f;
public float mapLatToGPSMap(float _val){
	if(whichCity==0){ // mexico city
		_val = map(_val,pointALat, pointBLat,0+gpsMarginsHeight, gpsMapGraphic.height-gpsMarginsHeight);
	}
	if(whichCity==1){ // zurich
		_val = map(_val,pointALat, pointBLat, gpsMapGraphic.height-gpsMarginsHeight,0+gpsMarginsHeight);
	}
	if(whichCity==2){ // xilitla
		_val = map(_val,pointALat, pointBLat,0+gpsMarginsHeight, gpsMapGraphic.height-gpsMarginsHeight);
	}
	if(whichCity==3){ // pachuca
		_val = map(_val,pointALat, pointBLat,0+gpsMarginsHeight, gpsMapGraphic.height-gpsMarginsHeight);
	}

	if(whichCity==4){ // milano
		_val = map(_val,pointALat, pointBLat, gpsMapGraphic.height-gpsMarginsHeight,0+gpsMarginsHeight);
	}
	if(whichCity==5){ // schoenenwerd
		_val = map(_val,pointALat, pointBLat, gpsMapGraphic.height-gpsMarginsHeight,0+gpsMarginsHeight);
	}
	return _val;
}
public float mapLongToGPSMap(float _val){
	_val = map(_val,pointALong, pointBLong, gpsMarginsWidth,gpsMapGraphic.width -gpsMarginsWidth);
	return _val;

}

public float mapXtoLong(float _val){
	_val = map(_val,gpsMarginsWidth,gpsMapGraphic.width -gpsMarginsWidth,pointALong, pointBLong );
	return _val;
}

public float mapYtoLat(float _val){
	if(whichCity==0){ // mexico city
		_val = map(_val,0+gpsMarginsHeight, gpsMapGraphic.height-gpsMarginsHeight,pointALat, pointBLat);
	}

	if(whichCity==1){ // zurich
		_val = map(_val, gpsMapGraphic.height-gpsMarginsHeight,0+gpsMarginsHeight,pointALat, pointBLat);
	}
	
	if(whichCity==2){ // xilitla
		_val = map(_val,0+gpsMarginsHeight, gpsMapGraphic.height-gpsMarginsHeight,pointALat, pointBLat);
	}
	if(whichCity==3){ // pachuca
		_val = map(_val,0+gpsMarginsHeight, gpsMapGraphic.height-gpsMarginsHeight,pointALat, pointBLat);
	}

	if(whichCity==4){ // milano
		_val = map(_val, gpsMapGraphic.height-gpsMarginsHeight,0+gpsMarginsHeight,pointALat, pointBLat);
	}
	if(whichCity==5){ // schoenenwerd
		_val = map(_val, gpsMapGraphic.height-gpsMarginsHeight,0+gpsMarginsHeight,pointALat, pointBLat);
	}
	return _val;
}



//  ---------------- digital spaces
//  ---------------- data interface
//  ---------------- visualising data from wifi signals and electro magnetic fields
//  ................ data examples provided as recorded during feb - july 2019 using several versions of the developed device
//  ---------------- october 2019
//  ---------------- andrés villa torres
//  ---------------- ndr3s -v -t
//  ---------------- villatorres@collegium.ethz.ch
//  ---------------- andres.villa_torres@zhdk.ch
//  ---------------- andresvillatorres@gmail.com
//  ................ https://github.com/andresvillatorres/Digital_Signals_Recorder

float satAmount = 23;
String satLabelInfo="Satellites tracking : ";

float longitude=0;
float latitude=0;
float altitude=0;
String extractTime="";

String gpsLabelInfo="Latitude: 3.4333, Longitude: -18.094, Altitude: 1123.334";
String currLat="3.433"; 
String currLong="-18.548";
String currAlt="1123.52";

PGraphics satellitesGraphic;

// ---------------- updating graphics

public void updateGraphics(){

  updateSatelliteGPSInformation();
  updateSatellites();
  updateSatelliteGraph(actualGPS.x,actualGPS.y,0);
  updateSingleSignals(actualGPS.x,actualGPS.y,0);
  updateMap();  
  lastWhich=which;
}



//  ---------------- display Satellites


public void displaySatText(){

	fill(67, 90, 137);
	textFont(font12,12);
	textAlign(LEFT,TOP);
	satLabelInfo = "Satellites tracking : " + PApplet.parseInt(satAmount);
	text(satLabelInfo,25,height-60);
}

//  ---------------- display GPS information


public void displayGPSInfoText(){

	fill(67, 90, 137);
	textFont(font12,12);
	textAlign(LEFT,TOP);
	gpsLabelInfo = "Lat : " + currLat + " , Long : " + currLong  + " , Alt : " + currAlt;
	text(gpsLabelInfo,25,height-35);
}

//  ---------------- update Satellite and GPS 

public void updateSatelliteGPSInformation(){
	String [] lines = loadStrings(gpsData[which]);
	for (int i = 0; i < lines.length; i++) {
		String[] list = split(lines[i], ',');
		for (int j= 0; j < list.length; j++) {

	        //extracting number of satellites
	        if (j==0) {
	          String[] lst = split(list[j], ':');
	          for (int k=0; k<lst.length; k++) {
	            satAmount=PApplet.parseFloat(lst[lst.length-1]);
	          }
	        }
	        if (j==1) {
	          String[] lst = split(list[j], ':');
	          for (int k=0; k<lst.length; k++) {
	            if(lst.length>3){
		            String [] seconds = split(lst[lst.length-1],	 ".");
		            String [] hourAndDate= split(lst[lst.length-3],"T");
		            String minutes = lst[lst.length-2];
		            String [] dDate = split(hourAndDate[hourAndDate.length-2], "-");
		            String hHour = hourAndDate[hourAndDate.length-1];
		            String year = dDate[dDate.length-3];
		            String month = dDate[dDate.length-2];
		            String day = dDate[dDate.length-1];
		            year=trim(year);
		            month=trim(month);
		            day=trim(day);
		            extractTime = year+"."+month+"."+day+"," + hHour + ":" + minutes + ":" + seconds[seconds.length-2];
		            extractTime=trim(extractTime);
	        	}

	          }
	        } 
			  // extracting longitude
			  if (j==2) {
			    String[] lst = split(list[j], ':');
			    for (int k=0; k<lst.length; k++) {
			      longitude=PApplet.parseFloat(lst[lst.length-1]);
			    }
			  }  

		  // extracting latitude
		  if (j==3) {
		    String[] lst = split(list[j], ':');
		    for (int k=0; k<lst.length; k++) {
		      latitude=PApplet.parseFloat(lst[lst.length-1]);
		    }
		  }      

		  // extracting altitude
		  if (j==4) {
		    String[] lst = split(list[j], ':');
		    for (int k=0; k<lst.length; k++) {
		      altitude=PApplet.parseFloat(lst[lst.length-1]);
		    }
		  }
		}
	}
	currLat= str(latitude);
	currLong= str(longitude);
	currAlt= str(altitude);
	// println("timestamp: "+extractTime +  " long : " + currLong + " , lat: " + currLat);
}

//  ---------------- update satellites

PVector pointInCase;
int satelliteAmount=50;

public void initPointInCase(){

	pointInCase=new PVector(0,0,0);

}

public void updateSatellites(){

	dynamicMapRegion();
	updateGPSOrientation();
	updatePlaces();

}

// ------------- generating satellite graphics

public void updateSatelliteGraph(float x, float y, float z){

    satellitesGraphic.beginDraw();
    satellitesGraphic.background(0);
    satellitesGraphic.noStroke();
    satellitesGraphic.fill(67, 90, 137,150);
    for (int i=0; i < satAmount; i++) {
      float r=300.0f;
      float a=(360.0f/satAmount)*i;
      float radialX = r*cos(a*PI/180);
      float radialY = r*sin(a*PI/180);
      satellitesGraphic.pushMatrix();      
      satellitesGraphic.translate(x + radialX  -(75/2), y + radialY - (50/2));
      satellitesGraphic.rotate((a+90)*PI/180);
      satellitesGraphic.shape(satelliteSVG,0,0 , 35, 25);
      satellitesGraphic.popMatrix();
    }
    satellitesGraphic.endDraw();

}

// ------------- drawing satellite graphic

public void displaySatellites(){
  blendMode(ADD);
  image(satellitesGraphic,0,0,width,height);
  blendMode(BLEND);
}

// ------------- loading satellite icons

PShape satelliteSVG ;
public void setupSatellite(){
	satelliteSVG = loadShape("../data/icons/satellite.svg");
	satelliteSVG.disableStyle();
}

// ------------- reading the emf points

PGraphics emfGraphic;
boolean emfOneTime=false;
int emfWhich=0;
int emfResStep=1;
float oldemfVal=50;
public void displayEMFInformation(){

	if(which != emfWhich){
		float emfVal=0.0f;
		float x =0.0f;
		float oX=0.0f;
		float y=0.0f;
		float oY=0.0f;
		emfGraphic.beginDraw();
		emfGraphic.clear();
		emfGraphic.noFill();
		emfGraphic.stroke(120,120,255);
		
		File checkFile = new File(emfData[which]);
		if(checkFile.exists()){
		String [] lines = loadStrings(emfData[which]);
			if(lines!=null){
				for (int i = 0; i < lines.length - emfResStep; i=i+emfResStep) {
					String[] list = split(lines[i], ',');
					
					for (int j= 0; j < list.length; j++) {
						emfVal=PApplet.parseFloat(list[1]);
					}
					emfGraphic.stroke(120,120,255);
					emfGraphic.strokeWeight(1.5f);
					emfGraphic.point(map(i,0,lines.length,0,width),map(emfVal,30,60,height-50,height-150));
					if(i>0){
						x=map(i,0,lines.length,0,width);
						oX=map(i-1,0,lines.length,0,width);
						y=map(emfVal,30,60,height-50,height-150);
						oY=map(oldemfVal,30,60,height-50,height-150);
						emfGraphic.strokeWeight(1);
						emfGraphic.stroke(120,120,255,100);
						emfGraphic.line(x,y,oX,oY);
					}
					
					oldemfVal=emfVal;
				}
			}
		}else{
			for(int i =0; i<2000;i++){
				emfGraphic.stroke(120,120,255);
				emfGraphic.strokeWeight(1.5f);
				emfGraphic.point(map(i,0,2000,0,width),height-50-random(100));
			}
		}
		emfGraphic.endDraw();
		image(emfGraphic,0,0,width,height);
	}else{
		image(emfGraphic,0,0,width,height);
	}
	emfWhich=which;
}

// ----------------  loading maps images

PImage zurichMap;
PImage schoenenwerdMap;
PImage milanoMap;
PImage cdmxMap;

public void loadMapImg(){
	zurichMap=loadImage("maps/zurich_map.png");
	schoenenwerdMap= loadImage("maps/schoenenwerd_map.png");
	milanoMap = loadImage("maps/milano_map.png");
	cdmxMap = loadImage("maps/cdmx_roma_map.png");
}

// ----------------  drawing maps images

public void displayMapImg(){

	rectMode(CORNER);
	if(whichCity==0){
		image(cdmxMap,gpsMarginsWidth,gpsMarginsHeight,width- (gpsMarginsWidth*2),height-(gpsMarginsHeight*2));
	}
	if(whichCity==1){
		image(zurichMap,gpsMarginsWidth,gpsMarginsHeight,width- (gpsMarginsWidth*2),height-(gpsMarginsHeight*2));
	}
	if(whichCity==4){
		image(milanoMap,gpsMarginsWidth,gpsMarginsHeight,width- (gpsMarginsWidth*2),height-(gpsMarginsHeight*2));
	}

	if(whichCity==5){
		image(schoenenwerdMap,gpsMarginsWidth,gpsMarginsHeight,width- (gpsMarginsWidth*2),height-(gpsMarginsHeight*2));
	}

}

// WEITER ZEICHNEN! VIEL SPASS


//  ---------------- digital spaces
//  ---------------- data interface
//  ---------------- visualising data from wifi signals and electro magnetic fields
//  ................ data examples provided as recorded during feb - july 2019 using several versions of the developed device
//  ---------------- october 2019
//  ---------------- andrés villa torres
//  ---------------- ndr3s -v -t
//  ---------------- villatorres@collegium.ethz.ch
//  ---------------- andres.villa_torres@zhdk.ch
//  ---------------- andresvillatorres@gmail.com
//  ................ https://github.com/andresvillatorres/Digital_Signals_Recorder


// ------------ functions for audio





String [] fixedAudio;
Minim fixedSoundFile;
AudioPlayer fixedPlayer;

public void audioLoad(int audioIndex,String globalAudioPath)
{

  fixedAudio[audioIndex] = globalAudioPath;

}

int oldWwhich=0;
boolean audioPlaying=false;
public void audioPlay(int wwhich){

    if(oldWwhich!=wwhich && !audioPlaying){
      audioPlaying=true;
      
      
      oldWwhich=wwhich;
      fixedSoundFile = new Minim(this);
      fixedPlayer = fixedSoundFile.loadFile(fixedAudio[wwhich]);
      fixedPlayer.loop();
    }

    if(audioPlaying==true && oldWwhich!=wwhich){
      fixedPlayer.pause();
      fixedPlayer.close();
      audioPlaying=false;
    }
}


float audioMapRespValA=0;
float audioMapRespValB=0;

public void audioGetVal(){
  float audioSumA=0;
  float audioSumB=0;

}

// ----- for visually displaying the audio wave - default is on
public void audioDisplay(float audioX,float audioY)
{
  if(showAudio){
    stroke(255);
    strokeWeight(1);
    for(int i = 0; i < fixedPlayer.bufferSize() - 1; i++)
    {
      float waveWidth= map(i,0,fixedPlayer.bufferSize(),0,width/4);
      line(audioX+waveWidth, audioY+25  + fixedPlayer.left.get(i)*50,  audioX+waveWidth+1, audioY+25  + fixedPlayer.left.get(i+1)*50);
      line(audioX+waveWidth, audioY+75 + fixedPlayer.right.get(i)*50, audioX+waveWidth+1, audioY+75 + fixedPlayer.right.get(i+1)*50);
    }
  }
}


//  ---------------- digital spaces
//  ---------------- data interface
//  ---------------- visualising data from wifi signals and electro magnetic fields
//  ................ data examples provided as recorded during feb - july 2019 using several versions of the developed device
//  ---------------- october 2019
//  ---------------- andrés villa torres
//  ---------------- ndr3s -v -t
//  ---------------- villatorres@collegium.ethz.ch
//  ---------------- andres.villa_torres@zhdk.ch
//  ---------------- andresvillatorres@gmail.com
//  ................ https://github.com/andresvillatorres/Digital_Signals_Recorder

PGraphics wifiSignalGraphic;
float wifiSum=0.0f;
float wifiSumNormal=0.0f;

//  ---------------- function for updating single signals reading from the data base

public void updateSingleSignals(float x, float y, float z){

    String [] lines2 = loadStrings(wifiStrngthData[which]);

    wifiSignalGraphic.beginDraw();
    // wifiSignalGraphic.smooth(8);
    wifiSignalGraphic.background(0);
    wifiSignalGraphic.fill(67, 90, 137,10);
    wifiSignalGraphic.stroke(67, 90, 137,80);
    wifiSignalGraphic.strokeWeight(2);
    float _wifiSum=0;
    for (int i=0; i < lines2.length; i++) {
      // fill(255, 0, 0);
      String local = lines2[i].replace("dBm", "");
      local = local.replace("signal:","");
      
      float locStr=map(PApplet.parseFloat(local), -100.0f,-80.0f,10,150);
      float dBm= PApplet.parseFloat (local);
      float mW = pow(10,dBm/10);
      _wifiSum=_wifiSum+ mW;
      float r=50.0f;
      float a=55.0f*i;
      float radialX = r*cos(a*PI/180);
      float radialY = r*sin(a*PI/180);

      wifiSignalGraphic.ellipse(x + radialX, y + radialY , locStr,locStr );


    }
    wifiSignalGraphic.endDraw();
    noStroke();
    wifiSum=_wifiSum;
    wifiSumNormal=map(wifiSum, 0.0000001f,0.000005f,0,1);
}

//  ---------------- function for displaying single wifi signals in the map as blobs

public void displaySingleSignals(){
  blendMode(ADD);
  image(wifiSignalGraphic,0,0,width,height);
  blendMode(BLEND);
}


//  ---------------- function for updating position from the displayed wifi labels

int countWiFiShow=0;
float postPosXwiFi=0.0f;
float posXwiFi=0.0f;
public void updateWiFiTextPos(){
  posXwiFi=postPosXwiFi*0.25f + posXwiFi*0.75f;
  if(posXwiFi< 0.5f || posXwiFi > width - 0.5f){
     if(posXwiFi<0.5f){
      posXwiFi=0;
    }
    if(posXwiFi>width-0.5f){
      posXwiFi=width;
    }
  }
  if(debug){
    textFont(font12,12);
    textAlign(LEFT,TOP);
    fill(255,0,0);
    text(posXwiFi, 25,100);
  }
}

//  ---------------- function for updating wifi information extracted from data base

public void updateWiFiTexts(){


      String [] lines2 = loadStrings(wifiIDsData[which]);
      rectMode(CORNER);
      fill(0,210);
      noStroke();
      rect(posXwiFi,0,width,height);
      fill(255);
      textFont(font12,12);
      text("WiFi ID", posXwiFi+25, 120);
      for (int i=0; i < lines2.length; i++) {
        String local = lines2[i].replace("SSID:","");
        local = trim(local);

        text(local,posXwiFi + 25, 160 + (i*15));
      }

      String [] lines3 = loadStrings(wifiStrngthData[which]);
      text("Signal Strength", posXwiFi+25+ 650, 120);
      rectMode(CORNER);
      for (int i=0; i < lines3.length; i++) {
        String local = lines3[i].replace("signal:", "");
        local = trim(local);
        // fill(255);
        // textFont(font18,18);
        text(local, posXwiFi+ 25 + 650, 160 + (i*15));
      }
      updateWiFiTextPos();
}
  public void settings() { 	size(displayWidth,displayHeight,P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "visInterface" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
