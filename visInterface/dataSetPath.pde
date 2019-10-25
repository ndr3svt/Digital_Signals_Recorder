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

void setupDataPath(){
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

void updateDataPath(String selectedDataPath){

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
			gpsPoints[i]=new PVector(0.0,0.0,0.0);
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

void folderSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
  	dataLoaded=false;

    println("User selected : " + selection.getAbsolutePath());
    updateDataPath(selection.getAbsolutePath());
  }
}


//  ---------------- functions for compressing and reducing size from jpeg images

float compressionLevel = 0.5;
String o_path_comp="";
int idxComp=0;

// code part referenced from
// jeff thompson
// https://gist.github.com/jeffThompson/ea54b5ea40482ec896d1e6f9f266c731
// edited by andrés villa torres /\ ndr3s -v -t



//  ---------------- actual compression function
int timeCompressionMillis=0;
int sizeFactor=8;
void compressImage(String path){
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

 void deleteFile(File toDelete){
	toDelete.delete();
}


