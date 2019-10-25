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

void setupMap(){

	actualGPS = new PVector(0,0);
	pointALat = 19.423453;
	pointALong= -99.166108;
	pointBLat=19.413945;
	pointBLong=-99.135857;

}



// -------- dynaic regions for readjusting the map corners according to six regions mwhich have been
// -------- manually identified by andrés villa torres
// -------- these six regions correspond to the sample data bodies provided by andrés which have been recorded using diverse
// -------- versions of the signal recording device

int whichCity=0;
void dynamicMapRegion(){
	//CDMX
	if(latitude>18.0 && latitude < 19.98){
		
		whichCity=0;
		pointALat = 19.424401;
		pointALong = -99.166304;

	// 19.424401, -99.166304

		// 19.424425, -99.166238 calle londres / eje 2
		// pointALat =19.423453;
		// pointALong=-99.166108;
		// 19.427024, -99.167676 - Angel de la independencia
		pointBLat = 19.414634;
		pointBLong = -99.148072;
 
		// 19.414634, -99.148072

		// pointBLat=19.413945;
		// pointBLong=-99.135857;
		// 19.417371, -99.149230 Dr Jimenez 140, Doctores, 06720 Ciudad de México, CDMX, Mexico
	}
		
	//ZURICH
	if(latitude>44.0 && latitude < 50.0 && longitude>8.5){
		whichCity=1;
		// pointALat = 47.35; 	// yA
		pointALat = 47.341358;
		pointALong = 8.508; // xA
		pointBLat = 47.41; 	// yB	
		pointBLong = 8.57; 	// xB
	}
	//XILITLA
	if(latitude>21.0 && latitude < 23.0){
		whichCity=2;
		pointALat=21.6;
		// pointALong=-98.999;
		pointBLat=21.35;
		// pointBLong=-98.979;
		pointALong=-98.999176667;
		pointBLong=-98.902991667;
	}
	//PACHUCA
	if(latitude>19.98 && latitude < 21.0){
		whichCity=3;
		pointALat=20.155703;
		pointALong=-98.895405;
		pointBLat=19.98;
		pointBLong=-98.685398;
	}
	// MILANO
	if(latitude>45.483 && latitude < 45.49){
		whichCity=4;
		pointALat=45.473540;
		pointALong=9.155545; 
		pointBLat=45.498775;
		pointBLong=9.220816;  
	}

	// SCHOENENWERD
	if(latitude>47.36 && latitude < 47.38 && longitude<8.1){
		whichCity=5;
		pointALat=47.364746;
		pointALong=7.981519; 
		pointBLat=47.383415;
		pointBLong=8.030357;  
	}

}

// -------- this function is adapting the  region orientations ( -/+ degrees) which variate according to the positioning in the meridian and equator
// -------- these regions have been manually identified by andrés villa torres
// -------- these six regions correspond to the sample data bodies provided by andrés which have been recorded using diverse
// -------- versions of the signal recording device while developing and testing

void updateGPSOrientation(){

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

void updateMap(){

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
void displayCurrentMarker(){


	smoothGPSx=smoothGPSx*0.85 + actualGPS.x*0.15;
	smoothGPSy=smoothGPSy*0.85 + actualGPS.y*0.15;
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

void displayMap(){
	blendMode(ADD);
	image(gpsMapGraphic,0,0,width,height);
	blendMode(BLEND);
}



PVector [] places;
String [] placesLabel;

// ---------------- updating the list of places

void updatePlaces(){

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
         			places[_indx] = new PVector( mapLongToGPSMap(float(pieces[i+2])), mapLatToGPSMap(float(pieces[i+1])) );
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
void drawPath(){

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
						 	fLong=(float(lst[lst.length-1]));
						}
					}  
					// extracting latitude
					if (j==3) {
						String[] lst = split(list[j], ':');
						for (int k=0; k<lst.length; k++) {
					  		fLat=(float(lst[lst.length-1]));
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

float gpsMarginsHeight=0.0;
float gpsMarginsWidth=0.0;
float mapLatToGPSMap(float _val){
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
float mapLongToGPSMap(float _val){
	_val = map(_val,pointALong, pointBLong, gpsMarginsWidth,gpsMapGraphic.width -gpsMarginsWidth);
	return _val;

}

float mapXtoLong(float _val){
	_val = map(_val,gpsMarginsWidth,gpsMapGraphic.width -gpsMarginsWidth,pointALong, pointBLong );
	return _val;
}

float mapYtoLat(float _val){
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



