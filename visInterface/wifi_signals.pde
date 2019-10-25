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
float wifiSum=0.0;
float wifiSumNormal=0.0;

//  ---------------- function for updating single signals reading from the data base

void updateSingleSignals(float x, float y, float z){

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
      
      float locStr=map(float(local), -100.0,-80.0,10,150);
      float dBm= float (local);
      float mW = pow(10,dBm/10);
      _wifiSum=_wifiSum+ mW;
      float r=50.0;
      float a=55.0*i;
      float radialX = r*cos(a*PI/180);
      float radialY = r*sin(a*PI/180);

      wifiSignalGraphic.ellipse(x + radialX, y + radialY , locStr,locStr );


    }
    wifiSignalGraphic.endDraw();
    noStroke();
    wifiSum=_wifiSum;
    wifiSumNormal=map(wifiSum, 0.0000001,0.000005,0,1);
}

//  ---------------- function for displaying single wifi signals in the map as blobs

void displaySingleSignals(){
  blendMode(ADD);
  image(wifiSignalGraphic,0,0,width,height);
  blendMode(BLEND);
}


//  ---------------- function for updating position from the displayed wifi labels

int countWiFiShow=0;
float postPosXwiFi=0.0;
float posXwiFi=0.0;
void updateWiFiTextPos(){
  posXwiFi=postPosXwiFi*0.25 + posXwiFi*0.75;
  if(posXwiFi< 0.5 || posXwiFi > width - 0.5){
     if(posXwiFi<0.5){
      posXwiFi=0;
    }
    if(posXwiFi>width-0.5){
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

void updateWiFiTexts(){


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