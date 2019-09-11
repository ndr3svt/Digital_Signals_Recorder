# #!/usr/bin/python
# # -*- coding: utf-8 -*-

# # code written by Andrés Villa Torres (ndr3s -v -t) && Lukas Stäussi (Smartronic GmbH)
# # collegium helveticum
# # digital societies - digital spaces
# # recording electromagnetic radiation, wifi and other signals
# #

# # CC0 1.0 Universal

# # __________________________________________________________________________________ 

# # Licensing, Patrimonial Rights, Intellectual Copyrights and Intellectual Property of this Project
# # This project is patrimonial property from Collegium Helveticum. 
# # It is licensed and protected under Open Access, Open Source and Creative Commons Intellectual Copyrights (CC0 1.0 Universal) . 
# # You and anyone are free to use the technology information provided in this document as well as the online documentation 
# # accessible inside the Github repository for academic, research, artistic, commercial and non commercial purposes.
# # You are free to refer the author, co-authors and the institutions behind this project if you consider it appropriate, 
# # specially if you generate further knowledge and results directly derived from using the whole, a copy or any of the parts
# # of this documentation or any piece of software and hardware directly retrieved from the authors or the online repositories.
# # The author and co-authors are not responsible for any intentional or unintentional damage or defect on the hardware, software, 
# # data loss, or the nature of further content generated derived from usage or misusage of these technologies. 
# # You are responsible for any usage and misusage, as well as any in iction to data privacy, data management and health regulations 
# # that may derive from the use of the provided technologies.

# # https://github.com/andresvillatorres/Digital_Signals_Recorder.git
# # For future inquires or contributions regarding the project, handbook, hardware and software:
# # info@andresvillatorres.work
import os
import datetime
import time
import sys
import commands
import gps
import math

import subprocess as s
import itertools
import RPi.GPIO as GPIO
import smbus as smbus
import numpy as np


import scipy.io.wavfile
from scipy.fftpack import fft



# maxRecordings = 212
maxRecordings = 4200


# FOR LEDS

GPIO.setmode(GPIO.BOARD)
GPIO.setwarnings(False)
GPIO.setup(16,GPIO.OUT)
GPIO.setup(18,GPIO.OUT)  
GPIO.output(16,GPIO.HIGH) 
GPIO.output(18,GPIO.HIGH)
time.sleep(0.01)
GPIO.output(16,GPIO.LOW) 
GPIO.output(18,GPIO.LOW)

# mount usb drive
MOUNT_DIR = "/media/usbstick"

###########################################################
# CONSTANT VARIABLES AND FUNCTIONS
###########################################################




def restart():
    import sys
    import os
    os.execv(sys.executable, ['python'] + sys.argv)



# to run commands
def run_command(command):
    # start = time.time()
    ret_code, output = commands.getstatusoutput(command)
    if ret_code == 1:
        os.system('reboot')
    return output.splitlines()

def uuid_from_line(line):
    start_str = "UUID=\""
    example_uuid = "6784-3407"
    uuid_start = line.index(start_str) + len(start_str)
    uuid_end = uuid_start + len(example_uuid)
    return line[uuid_start: uuid_end]




###########################################################
#   VARIABLE TO PRINT LOG INFORMATION IN LOG FILE
###########################################################


# reading character
working_directory = "./data"
# open and read last_index.txt
with open('/home/pi/root/sessionIndex.txt', 'r') as f:
    xI = int(f.read())
print("========================== start recording : " + str(xI)+ " ========================================")
print ("new recording index = " + str(xI))




t = time.time()



# Defining directory
dataDir = "/home/pi/root/recordedDataFinal/"+datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S')
os.makedirs(dataDir)
os.chdir(dataDir)


log = open('/home/pi/root/GetData.log','a')
logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S') + " start log\n"
log.write(logtxt) #Give your csv text here.

logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S')+ "   Rec Index : " + str(xI) + "\n"
log.write(logtxt) #Give your csv text here.



###########################################################################################################################
# Read WiFi Signal Strength 
##########################################################################################################################
GPIO.output(16,GPIO.HIGH) 
GPIO.output(18,GPIO.LOW)
time.sleep(0.01)
GPIO.output(16,GPIO.LOW) 
GPIO.output(18,GPIO.LOW)
wifiStrengthSuccess = False
while not wifiStrengthSuccess:
	try:
		wifiSigStr_mW = 0.0
		wifiSigStr_dBM = 0.0
		p = s.Popen(['/sbin/iw','wlan0','scan'], stdout=s.PIPE) 
		r = p.communicate()[0].replace('\n','')
		r = filter(None, [filter(None, item.split('\t')) for item in r.split('Cell')]) 
		# getting single wifi strength's

		a_list_of_interest = ['signal:'] 
		sigStr = []
		for index, cell in enumerate(r):
		    tmp_cell = []
		    
		    for items in cell:
		        for item in a_list_of_interest:
		            if item in items:
		                sigStr.append(items)
		                tmp_cell.append(items)
		        r[index] = tmp_cell

		logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S')+" WiFi Traced Signals' Strength:"+ "\n"
		log.write(logtxt)

		if len(sigStr) > 0:
		    f = open('WiFi_Sig_Power.csv','w')
		    for m in range(len(sigStr)):
		            print sigStr[m]
		            logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S')+ "\t" + str(sigStr[m]) + "\n"
		            log.write(logtxt)
		            f.write(str(sigStr[m]) + "\n") #Give your csv text here.
		    f.close()
		    wifiStrengthSuccess = True
	except:
		pass


      
if len(sigStr) > 0:
    start_str = "signal: "
    end_str = " dBm"
    wifiSigStr_mW = 0.0
    wifiSigStr_dBM = 0.0
    mWf=0.0
    for m in range(len(sigStr)):
        line = sigStr[m]
        dBmStart = line.index(start_str) + len(start_str)
        dBmEnd = line.index(end_str)
        z= float(line[dBmStart: dBmEnd])
        mWf=math.pow(10,(z/10))
        wifiSigStr_mW = wifiSigStr_mW + mWf

    print "%f mW" % (wifiSigStr_mW)

    wifiSigStr_dBM= 10*math.log10(wifiSigStr_mW)
    print "%f mW" % (wifiSigStr_dBM)
    wifiStr = "%f dBm, %f mW\n" %(wifiSigStr_dBM,wifiSigStr_mW)

    logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S') + " WifiStärke ausgelesen %fmW, %fdBm" % (wifiSigStr_mW,wifiSigStr_dBM) + "\n"
    log.write(logtxt)




GPIO.output(16,GPIO.HIGH) 
GPIO.output(18,GPIO.LOW)
time.sleep(0.01)
GPIO.output(16,GPIO.LOW) 
GPIO.output(18,GPIO.LOW)


################################ 
#### getting single wifi id's
################################

wifiSuccess = False
while not wifiSuccess:
	try:
		p2 = s.Popen(['/sbin/iw','wlan0','scan'], stdout=s.PIPE) 
		r2 = p2.communicate()[0].replace('\n','')
		r2 = filter(None, [filter(None, item2.split('\t')) for item2 in r2.split('Cell')])
		
		a_list_of_interest2 = ['SSID']  
		sigID = []

		for index2, cell2 in enumerate(r2):
		    tmp_cell2 = []
		    
		    for items2 in cell2:
		        for item2 in a_list_of_interest2:
		            if item2 in items2:
		                sigID.append(items2)
		                tmp_cell2.append(items2)
		        r2[index2] = tmp_cell2

		logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S')+" WiFi Visible ID's:"+ "\n"
		log.write(logtxt)

		# checking if the sigID is true which means there where wifi sig active in the surroundings
		if len(sigID) > 0:
		    f = open('WiFi_Ids.csv','w')
		    for m in range(len(sigID)):
		            print sigID[m]
		            logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S')+ "\t" + str(sigID[m]) + "\n"
		            log.write(logtxt)
		            f.write(str(sigID[m]) + "\n") #Give your csv text here.
		    f.close()
		    wifiSuccess = True
	except:
		pass

GPIO.output(16,GPIO.HIGH) 
GPIO.output(18,GPIO.LOW)
time.sleep(0.01)
GPIO.output(16,GPIO.LOW)
GPIO.output(18,GPIO.LOW)


# NUMBER OF GENERATED FILES 131 = 1148 MHz
# NUMBER OF GENERATED FILES 220 = 1760 MHZ
# NUMBER OF GENERATED FILES 225 = 1800MHz




###################################################################################################
#Get Data from the AirSpy uncomment for recording airspy data
####################################################################################################


emfFileAmount = 131
onOff = 0
for x in range(0, emfFileAmount):
 
    freq = 100 + x*8
    print "Freq:  %iMhz" % (freq)
    # text =  'airspy_rx -w -t 0 -f ' + repr(freq) + ' -a 10000000 -g 12 -n 65536'
    # text =  'airspy_rx -w -t 0 -f ' + repr(freq) + ' -a 10000000 -g 12 -n 16384'
    text =  'airspy_rx -w -t 0 -f ' + repr(freq) + ' -a 10000000 -g 12 -n 4096'
    # print (text,'file id:',str(x+1))
    cmd = text
    os.system(cmd)

    # uncomment for visual feedback with the LED
    if onOff<1:
        onOff=onOff+1
    else:
        onOff=0
    if onOff==0:
        GPIO.output(16,GPIO.LOW)
    else:
        GPIO.output(16,GPIO.HIGH)
        time.sleep(0.005)
        GPIO.output(16,GPIO.LOW) 


logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S') + " AirSpy Data Collected\n"
log.write(logtxt) 

print "elapsed time after start and recording airspy data %f" % (time.time()-t)

###################################################################################################
####  DATA from AirSpy to FFT Spectrum
####################################################################################################


freqToWrite = []
fftToWrite = []
print 'getting FFT spectrum from airspy data'

for y in range(0,emfFileAmount,1):
    dataID = (y*8)+100


    rate,audData=scipy.io.wavfile.read(dataDir +"/"+ str(dataID)+ "MHz.wav")
    print(dataID , "MHz")
    top = len(audData)
    Fs = 10000000
    NData = 64
    y_comp = []
   
    for x in range(0,top,1):
        y_comp.append(complex(audData[x,0],audData[x,1]))
    y_fft2 = 0
    y_fft_shift = 0

    for x in range(0,top-NData,1):
        y_fft = abs(fft(y_comp[x:x+NData-1], NData))
        y_fft2= y_fft2 + y_fft

    ## uncomment / comment for visual feedback from the LED -> can help to save battery if turned off
    if onOff<1:
        onOff=onOff+1
    else:
        onOff=0

    if onOff==0:
        GPIO.output(16,GPIO.LOW)
    else:
        GPIO.output(16,GPIO.HIGH) 
        time.sleep(0.005)
        GPIO.output(16,GPIO.LOW) 
       
    y_fft2 = 20*np.log10(y_fft2)
    y_fft_shift = np.linspace(1,2,NData)
    y_fft_shift[NData/2:NData] = y_fft2[0:NData/2]
    y_fft_shift[0:NData/2] = y_fft2[NData/2:NData]

    freq = np.linspace(-NData/2,NData/2-1,NData)
    for x in range(0,NData,1):
        freq[x] = Fs * freq[x]/NData + ((y*8e6)+100e6)
        
        if (x >= 6) and (x <= (NData-8) ): 
            freqToWrite.append(freq[x])
            fftToWrite.append(y_fft_shift[x])



f = open('Spectrum_EMF_FFT.csv','w')
for x in range(0,len(freqToWrite),1):
    f.write("%f , %f\n" % (freqToWrite[x], fftToWrite[x]))
f.close()

######################################################
### uncomment /comment to delete or keep raw files from the airspy
######################################################
print 'erasing airspy data'
for y in range(0,emfFileAmount):
    dataID = (y*8)+100
    print('errasing files ... ' + str(dataID))
    fileName = dataDir +"/" + str(dataID) + "MHz.wav"
    command = 'sudo rm -rf ' + fileName
    output = run_command(command)





print "Elapsed Time bevor GPS and after Spectrum: %f" % (time.time()-t)
logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S') + " Elapsed Time bevor GPS: %f" % (time.time()-t) + "\n"
log.write(logtxt) 



###########################################################
# GET GPS DATA
############################################################
# if moudle available where the GPS is connected to the raspberry pi
command = 'ls /dev | grep ttyUSB0'
output = run_command(command)
print output
SKYReceive = False
GPSReceive = False
satNr = "no Satellites yet"
gpsData = "none yet"
if output:  
    command = 'sudo gpsd /dev/ttyUSB0 -F /var/run/gpsd.sock'
    run_command(command)
    try:
        session = gps.gps("localhost", "2947")
        session.stream(gps.WATCH_ENABLE | gps.WATCH_NEWSTYLE)
    except Exception as e:    
        gpsOutput = 'No ttyUSB0'
        print(gpsOutput)
else: 
    print('no GPS connected to USB')

GPIO.output(16,GPIO.HIGH)
GPIO.output(18,GPIO.LOW)
time.sleep(0.01)
GPIO.output(16,GPIO.LOW)
GPIO.output(18,GPIO.LOW)

waitGPSLimit = 0 # limit the waiting time till gps otherwise skip and generate empty file with string

while ( (not SKYReceive or not GPSReceive) and (waitGPSLimit<25) ):
    try:
        report = session.next()
        satReport = report
        if report['class'] == 'TPV':
            if hasattr(report, 'time') and hasattr(report, 'lon') and hasattr(report, 'lat') and hasattr(report, 'alt') and not GPSReceive:

                print("Time : "+ str(report.time) + ", longitude : " + str(report.lon) +  " , latitude : " + str(report.lat) + " , altitude : " + str(report.alt) )
                gpsData = "Time : "+ str(report.time) + ", longitude : " + str(report.lon) +  " , latitude : " + str(report.lat) + " , altitude : " + str(report.alt) 
                GPSReceive = True
            else:
                print('no signal')
                waitGPSLimit = waitGPSLimit+1
                print('waiting for signal: ' + str(waitGPSLimit))

        else:
            print('No TPV Information')
            waitGPSLimit = waitGPSLimit+1
            print('waiting for signal: ' + str(waitGPSLimit))

        if satReport['class'] == 'SKY':
            if hasattr(satReport, 'satellites') and not SKYReceive:
                print('number of satellites : ' + str(len(satReport.satellites)))
                satNr = 'number of satellites : ' + str(len(satReport.satellites))
                SKYReceive = True
    

    except KeyError:
        pass
    except KeyboardInterrupt:
        print("GPSD has terminated yyyiii")
        quit()
    except StopIteration:
        session = None
        print("GPSD has terminated xxx65")

gpsOutput = satNr + " , " + gpsData

logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S') + " GPS Recorded, Output DATA : " + gpsOutput +"\n"
log.write(logtxt) 

GPIO.output(16,GPIO.HIGH) 
GPIO.output(18,GPIO.LOW)
time.sleep(0.01)
GPIO.output(16,GPIO.LOW) 
GPIO.output(18,GPIO.LOW)
###########################################################
# AUDIO RECORDING
###########################################################

print('recording audio during 5 seconds ... ')
command = 'arecord -f cd -D plughw:1,0 -d 5 -r44100 audioR_5_sec.wav'
output = run_command(command)
print output
logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S') + " *** AUDIO Recorded Successfully" + str(output) +"\n"
log.write(logtxt) 

GPIO.output(16,GPIO.HIGH) 
GPIO.output(18,GPIO.LOW)
time.sleep(0.01)
GPIO.output(16,GPIO.LOW) 
GPIO.output(18,GPIO.LOW)

###########################################################
# MAKE FOTO
###########################################################
# advanced settings for camera raspistill -vf -hf -ISO 100 -ss 60000 -br 80 -co 100 -o ouuch2.jpg

print('making foto ... ')
command = 'raspistill -vf -hf -o foto.jpg'
output = run_command(command)
print output
logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S') + " *** FOTO MADE" + str(output) +"\n"
log.write(logtxt)

GPIO.output(16,GPIO.HIGH) 
GPIO.output(18,GPIO.LOW)
time.sleep(0.01)
GPIO.output(16,GPIO.LOW) 
GPIO.output(18,GPIO.LOW)

###########################################################
# FINAL PARTS
###########################################################

## Write CSV File with GPS Data
print('finalazing ... ')
f = open('GPSData.csv','w')
f.write(gpsOutput) #Give your csv text here.
f.close()

# Write CSV File with Wifi Intensity
f = open('WifiStrength.csv','w')
f.write("%fmW, %fdBm\n" % (wifiSigStr_mW,wifiSigStr_dBM)) #Give your csv text here.
f.close()



logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S') + " Elapsed Time: %f" % (time.time()-t) +"\n"
log.write(logtxt) 
logtxt = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S') + " finished recording routine, waiting 600 seconds for next one "  +"\n"
log.write(logtxt) 

log.close()

print "Elapsed Time: %f" % (time.time()-t)
print('done! waiting for the next record 600 seconds')

GPIO.output(16,GPIO.HIGH) 
GPIO.output(18,GPIO.LOW)
time.sleep(0.01)
GPIO.output(16,GPIO.LOW) 
GPIO.output(18,GPIO.LOW)



if xI < maxRecordings:
    xI = xI + 1
    # print xI
    os.chdir('../..')
    with open('/home/pi/root/sessionIndex.txt', 'w') as f:
        f.write(str(xI))
        command = 'sudo killall gpsd'
        run_command(command)
    # time.sleep(3)
    # time.sleep(300) # waiting 15 seconds > 5 minutes
    for x in range(0,60,1): # waiting 60 times n seconds if n == 10 > 600 seconds > 10 minutes
    # for x in range(0,2,1): # waiting 60 times n seconds if n == 10 > 600 seconds > 10 minutes
    # for x in range(0,5,1): # waiting 5 times n seconds if n == 1 > 5 seconds

        GPIO.output(16,GPIO.HIGH)
        # GPIO.output(18,GPIO.LOW)
        time.sleep(0.01) # is on for 0.01 seconds
        GPIO.output(16,GPIO.LOW) 
        # GPIO.output(18,GPIO.LOW)
        time.sleep(9.99) # waits 0.99 seconds
        print("waiting : " + str(x*10) + " ellapsed seconds")
        # time.sleep(9.99) # waits 9.99 seconds
        # time.sleep(0.99) # waits 0.99 seconds
    
    print("restarting ... ")
    print("===================== end of recording : " + str(xI - 1)+ " ========================")
    restart()
else:
    xI = 1
    os.chdir('../..')
    with open('/home/pi/root/sessionIndex.txt', 'w') as f:
        f.write(str(xI))
    print("ending ...")
    print("==================================================================")
    quit() 

