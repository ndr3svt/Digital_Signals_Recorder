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

import datetime
import time
import commands
import smbus

def run_command(command):

    ret_code, output = commands.getstatusoutput(command)
    if ret_code == 1:
        os.system('reboot')

    return output.splitlines()

ReadSeconds	= 0
ReadMinutes	= 1
ReadHours	= 2
ReadDay     = 3
ReadDate	= 4
ReadMonth	= 5
ReadYear	= 6

DS3231_Alarm1Sec =0x07
DS3231_Alarm1Min =0x08
DS3231_Alarm1H =0x09
DS3231_Alarm1Day =0x0A
DS3231_Alarm2Min =0x0B
DS3231_Alarm2H =0x0C
DS3231_Alarm2Day =0x0D
DS3231_Control = 0x0E
DS3231_Status   = 0x0F

i2c = smbus.SMBus(1)
i2c_DS3231 = 0x68


rtcseconds = i2c.read_byte_data(i2c_DS3231,ReadSeconds)
rtcminutes = i2c.read_byte_data(i2c_DS3231,ReadMinutes)
rtchours  =  i2c.read_byte_data(i2c_DS3231,ReadHours)
rtcday  =  i2c.read_byte_data(i2c_DS3231,ReadDay)
rtcdate  =  i2c.read_byte_data(i2c_DS3231,ReadDate)
rtcmonth  =  i2c.read_byte_data(i2c_DS3231,ReadMonth)
rtcyear  =  i2c.read_byte_data(i2c_DS3231,ReadYear)

rtcseconds=(rtcseconds & 0b00001111)+(rtcseconds & 0b01110000)/16*10 
rtcminutes = (rtcminutes & 0b00001111)+(rtcminutes & 0b01110000)/16*10 
rtchours = (rtchours & 0b00001111)+(rtchours & 0b00110000)/16*10	
rtcday = rtcday & 0b00000111					
rtcdate = (rtcdate & 0b00001111)+(rtcdate & 0b00110000)/16*10	
rtcmonth=(rtcmonth & 0b00001111)+(rtcmonth & 0b00010000)/16*10 
rtcyear=(rtcyear & 0b00001111)+(rtcyear & 0b11110000)/16*10	
seconds = rtcseconds
minutes=rtcminutes						
hours=rtchours							
day=rtcday
date=rtcdate
month=rtcmonth
year=rtcyear

if month < 10:
	month = '0'+str(month)
if hours <10:
	hours = '0'+str(hours)
if minutes <10:
	minutes = '0'+str(minutes)
if date <10:
	date = '0'+str(date)
MMDDhhmmYYYY=str(month) + str(date)+str(hours)+str(minutes)+str(year);
ss = str(seconds);
finalTime = MMDDhhmmYYYY + '.' +ss
run_command('sudo date '+ finalTime); 
print finalTime
# time.sleep(1.0)

quit()