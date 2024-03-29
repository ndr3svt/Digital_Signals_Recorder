CC0 1.0 Universal 
__________________________________________________________________________________ 
Licensing, Patrimonial Rights, Intellectual Copyrights and Intellectual Property of this Project
This project is patrimonial property from Collegium Helveticum. It is licensed and protected under Open Access, Open Source and Creative Commons Intellectual Copyrights (CC0 1.0 Universal) . You and anyone are free to use the technology information provided in this document as well as the online documentation accessible inside the Github repository for academic, research, artistic, commercial and non commercial purposes.

You are free to refer the author, co-authors and the institutions behind this project if you consider it appropriate, specially if you generate further knowledge and results directly derived from using the whole, a copy or any of the parts of this documentation or any piece of software and hardware directly retrieved from the authors or the online repositories.

The author and co-authors are not responsible for any intentional or unintentional damage or defect on the hardware, software, data loss, or the nature of further content generated derived from usage or misusage of these technologies. You are responsible for any usage and misusage, as well as any harm to data privacy, data management and health regulations that may derive from the use of the provided technologies.

https://github.com/andresvillatorres/Digital_Signals_Recorder.git 

__________________________________________________________________________________

Structure
  
  root ::: contains all the scrypts inside the raspberry pi, you can compile them directly from the pi or run them on the rc.local scrypt
  
  ******you may need to install all dependencies on the pi, the error messages will guide you through ;)
  
  gps
  
  math
  
  numpy
  
  scipy 
 
  reSpeaker 2-Mics Pi-HAT drivers
  http://wiki.seeedstudio.com/ReSpeaker_2_Mics_Pi_HAT/
  
  for running with airspy go to the link bellow and follow the instructions
  https://airspy.com/quickstart/
  
  currently developing a version of the project with another EMF recording device
  
  visInterface ::: contains all the scrypts from the visualisation app built for processing 3.5.3 and running on mac os x Mojave
  
  ******you may need to install or modify the access to some libraries depending on your processing version --- also error messages are self explaining, however I list them all here
  
 
  
  // these parts are already written on the scrypts you may not need to modify anything
  (readingAudio.pde)
  - import ddf.minim.*;
  - import ddf.minim.effects.*;
  
  in case of errors 
   - install minim library for sound manipulation
  
  (visInterface.pde)
  - import java.io.File;
  - import java.util.Arrays;

  // ---------------- loading libraries necessary for doing the jpeg compression
  - import java.awt.image.BufferedImage;
  - import javax.imageio.plugins.jpeg.*;
  - import javax.imageio.*;
  - import javax.imageio.stream.*;
  

__________________________________________________________________________________

For future inquires or contributions regarding the project, handbook, hardware and software:
Andrés Villa Torres info@andresvillatorres.work 

Intellectual and Creative Authorship:

Andrés Villa Torres, Scientific Researcher at Collegium Helveticum.
Research, Concept, Design, Prototyping, Fabrication, Engineering, Electronics, Programming.

Lukas Stäussi, Electrical Engineer at Smartronic GmbH. 
Electronics, Engineering, Programming.

Prof. Hannes Rickli, Prof. Dr. Mike Martin, Fellows at Collegium Helveticum. Dr. Christina Röcke, URPP Dynamics of Healthy Aging. University of Zürich. Project Board.
