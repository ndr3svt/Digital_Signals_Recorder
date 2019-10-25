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

void updateGraphics(){

  updateSatelliteGPSInformation();
  updateSatellites();
  updateSatelliteGraph(actualGPS.x,actualGPS.y,0);
  updateSingleSignals(actualGPS.x,actualGPS.y,0);
  updateMap();  
  lastWhich=which;
}



//  ---------------- display Satellites


void displaySatText(){

	fill(67, 90, 137);
	textFont(font12,12);
	textAlign(LEFT,TOP);
	satLabelInfo = "Satellites tracking : " + int(satAmount);
	text(satLabelInfo,25,height-60);
}

//  ---------------- display GPS information


void displayGPSInfoText(){

	fill(67, 90, 137);
	textFont(font12,12);
	textAlign(LEFT,TOP);
	gpsLabelInfo = "Lat : " + currLat + " , Long : " + currLong  + " , Alt : " + currAlt;
	text(gpsLabelInfo,25,height-35);
}

//  ---------------- update Satellite and GPS 

void updateSatelliteGPSInformation(){
	String [] lines = loadStrings(gpsData[which]);
	for (int i = 0; i < lines.length; i++) {
		String[] list = split(lines[i], ',');
		for (int j= 0; j < list.length; j++) {

	        //extracting number of satellites
	        if (j==0) {
	          String[] lst = split(list[j], ':');
	          for (int k=0; k<lst.length; k++) {
	            satAmount=float(lst[lst.length-1]);
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
			      longitude=float(lst[lst.length-1]);
			    }
			  }  

		  // extracting latitude
		  if (j==3) {
		    String[] lst = split(list[j], ':');
		    for (int k=0; k<lst.length; k++) {
		      latitude=float(lst[lst.length-1]);
		    }
		  }      

		  // extracting altitude
		  if (j==4) {
		    String[] lst = split(list[j], ':');
		    for (int k=0; k<lst.length; k++) {
		      altitude=float(lst[lst.length-1]);
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

void initPointInCase(){

	pointInCase=new PVector(0,0,0);

}

void updateSatellites(){

	dynamicMapRegion();
	updateGPSOrientation();
	updatePlaces();

}

// ------------- generating satellite graphics

void updateSatelliteGraph(float x, float y, float z){

    satellitesGraphic.beginDraw();
    satellitesGraphic.background(0);
    satellitesGraphic.noStroke();
    satellitesGraphic.fill(67, 90, 137,150);
    for (int i=0; i < satAmount; i++) {
      float r=300.0;
      float a=(360.0/satAmount)*i;
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

void displaySatellites(){
  blendMode(ADD);
  image(satellitesGraphic,0,0,width,height);
  blendMode(BLEND);
}

// ------------- loading satellite icons

PShape satelliteSVG ;
void setupSatellite(){
	satelliteSVG = loadShape("../data/icons/satellite.svg");
	satelliteSVG.disableStyle();
}

// ------------- reading the emf points

PGraphics emfGraphic;
boolean emfOneTime=false;
int emfWhich=0;
int emfResStep=1;
float oldemfVal=50;
void displayEMFInformation(){

	if(which != emfWhich){
		float emfVal=0.0;
		float x =0.0;
		float oX=0.0;
		float y=0.0;
		float oY=0.0;
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
						emfVal=float(list[1]);
					}
					emfGraphic.stroke(120,120,255);
					emfGraphic.strokeWeight(1.5);
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
				emfGraphic.strokeWeight(1.5);
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

void loadMapImg(){
	zurichMap=loadImage("maps/zurich_map.png");
	schoenenwerdMap= loadImage("maps/schoenenwerd_map.png");
	milanoMap = loadImage("maps/milano_map.png");
	cdmxMap = loadImage("maps/cdmx_roma_map.png");
}

// ----------------  drawing maps images

void displayMapImg(){

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


