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

import ddf.minim.*;
import ddf.minim.effects.*;


String [] fixedAudio;
Minim fixedSoundFile;
AudioPlayer fixedPlayer;

void audioLoad(int audioIndex,String globalAudioPath)
{

  fixedAudio[audioIndex] = globalAudioPath;

}

int oldWwhich=0;
boolean audioPlaying=false;
void audioPlay(int wwhich){

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

void audioGetVal(){
  float audioSumA=0;
  float audioSumB=0;

}

// ----- for visually displaying the audio wave - default is on
void audioDisplay(float audioX,float audioY)
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


