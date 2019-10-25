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

import java.io.File;
import java.util.Arrays;

// ---------------- loading libraries necessary for doing the jpeg compression

import java.awt.image.BufferedImage;
import javax.imageio.plugins.jpeg.*;
import javax.imageio.*;
import javax.imageio.stream.*;


PImage imgExample;
PImage contextImage;
Float [] points;
Float [] yPoints;

Float [] liquidX;
Float [] liquidZ;

float scrollUpDown=0;
float rotaryUpDown=0;
float mon1Scale=1.0;
float mon2Scale=1.0;
float scaleAllFactor=0.75;

PFont font144,font136,font128,font111,font96,font72,font48,font36,font24,font18,font16,font14,font12,font10,font8;
PVector [] pointPos;
PVector [] prevPointPos;
int amountSamples=0;

//  ---------------- main initialization

void setup(){

	size(displayWidth,displayHeight,P2D);
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
void draw(){
	background(0);
	allDrawingFunctions();

}


// ----------------- all drawing functions
void allDrawingFunctions(){
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
		audioDisplay(width - 720*0.5 - 25,150);
		displayControls();	
		displayCurrentMarker();
	}
	displayDebuggingText();
	displayPointer();
}


//  ---------------- loading fonts
void loadAllFonts(){


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
void displayTimeStampText(){
	fill(255);
	textFont(font72,72);
	textAlign(LEFT,TOP);
	text(extractTime,25-7,25);
	textAlign(LEFT,TOP);
	text("# " + which,1055, height-96);
}



//  ---------------- update image and image information

void updateContextImage(){
	contextImage= imgExample;
}


// ----------------- display image and image information

boolean imgRequested=false;
int lastReqWhich=0;
PImage lastAsynchImage;
void displayContextImage(){
	if(!dataLoaded){

	}	else{		
		if(which!= lastReqWhich ){

			asynchronousImage= requestImage(imgData[which]);
			lastReqWhich=which;

		}
		if(asynchronousImage.width>0){
			image(asynchronousImage,width - 720*0.5 - 25,25,720*0.5,540*0.5);
			lastAsynchImage=asynchronousImage;
		}else{
			if(lastAsynchImage.width>0){
				image(lastAsynchImage,width - 720*0.5 - 25,25,720*0.5,540*0.5);
			}
		}
	}

}

//  --------------- changing the label of the city according to the region in case

String locationLabel="Xilitla";
int oldWhichCity=-1;
void displayContextImageLabel(){
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

void setWiFiText(){
	wifiID= new String[wifiAmount];
	wifiStrength = new int[wifiAmount];

	for(int i = 0; i < wifiAmount; i ++){
		wifiID[i] = "sdfñkaj" + i ;
		wifiStrength[i] = int(random(-90,-30));
	}
}


void displayWiFiText(){

	fill(255);
	textFont(font12,12);
	textAlign(LEFT,TOP);
	for(int i= 0; i< wifiAmount; i ++){
		text("SSID : " + wifiID[i],25,160 + (15*i));
		text(wifiStrength[i] + " dBm",325,160 + (15*i));
	}
	
}

//  ---------------- debugging information

void displayDebuggingText(){
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
  		strokeWeight(0.5);
  		line(mouseX,0,mouseX,height);
  		line(0,mouseY,width,mouseY);

  		textFont(font12,12);
  		String locationText = "Lat : " + mapYtoLat(mouseY) + " , Long : " + mapXtoLong(mouseX ) ;
  		text(locationText, mouseX+5,mouseY-25);
	}
}

//  ---------------- interactions through mousewheel

void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  scrollUpDown=scrollUpDown+e;
  which = which + int(e);
  which = constrain(which, 0, amountSamples-1);

}

//  ---------------- display pointer
void displayPointer(){
	if(showPointer && !debug){
		noFill();
  		stroke(67, 90, 137);
  		strokeWeight(0.5);
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
void displayControls(){
		oControlsPx=controlsPx*0.25 + oControlsPx*0.75;
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
void keyPressed(){

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


void mouseClicked(MouseEvent evt) {
  if (evt.getCount() == 2)doubleClicked();
}

void doubleClicked(){
	String goToLat= str(mapYtoLat(mouseY));
  	String goToLong= str(mapXtoLong(mouseX));
	link("https://www.google.com/maps/place/"+goToLat+","+goToLong+"/z9");
}

