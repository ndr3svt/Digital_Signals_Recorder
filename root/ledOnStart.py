# #!/usr/bin/python
# # -*- coding: utf-8 -*-

# # code written by Andrés Villa Torres (ndr3s -v -t)
# # collegium helveticum
# # digital societies - digital spaces
# # recording electromagnetic radiation, wifi and other signals

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


import time
import sys
import RPi.GPIO as GPIO


GPIO.setmode(GPIO.BOARD)
GPIO.setup(16,GPIO.OUT)
GPIO.setup(18,GPIO.OUT)  
GPIO.output(16,GPIO.HIGH) 
GPIO.output(18,GPIO.HIGH)

x = 0
for x in range(25):
	# print(x)
	x = x + 1
	
	time.sleep(0.05)
	GPIO.output(16,GPIO.HIGH) 
	GPIO.output(18,GPIO.HIGH)
	time.sleep(0.01)
	GPIO.output(16,GPIO.LOW) 
	GPIO.output(18,GPIO.LOW)


y = 0
for y in range(57600): # after these amount of iterations it will stop this programm and quit
	# print(x)
	# y = y + 1
	time.sleep(0.01) # waiting for 0.01 seconds ---> this is the time that it stays on
	# GPIO.output(16,GPIO.LOW) 
	GPIO.output(18,GPIO.LOW)
	time.sleep(4.99) # waiting for 4.99 seconds
	# GPIO.output(16,GPIO.LOW)
	GPIO.output(18,GPIO.HIGH)

time.sleep(2.25)
GPIO.output(16,GPIO.LOW) 
GPIO.output(18,GPIO.LOW)

quit()


