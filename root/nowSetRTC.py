# #!/usr/bin/python
# # -*- coding: utf-8 -*-

# # code written by Andrés Villa Torres (ndr3s -v -t)
# # collegium helveticum
# # digital societies - digital spaces
# # recording electromagnetic radiation, wifi and other signals

# # run this scrypt only if the device is connected to an active WLAN with access to Internet


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

import smbus
import time
import datetime

address = 0x68
register = 0x00


now = datetime.datetime.now()
print "year:" + str(now.year) + " , month:" + str(now.month) + ", day:" +  str(now.day)  +" , hour: " + str(now.hour) + " , minute: " + str(now.minute) + " , second: " + str(now.second)  

n_Year= now.year%100
n_month = now.month
n_day = now.day
n_hours = now.hour
n_minutes = now.minute
n_seconds = now.second
n_week = now.weekday()
print n_week

n_Year= (n_Year/10*16)+n_Year%10
n_seconds=(n_seconds/10*16)+  n_seconds % 10
n_minutes=(n_minutes/10*16)+  n_minutes % 10
n_hours=(n_hours/10*16)+ n_hours % 10
n_day=(n_day/10*16)+ n_day % 10
n_month=(n_month/10*16)+ n_month % 10
n_week=(n_week/10*16)+ n_week % 10


print "hex year : " + str(n_Year) + ", hex week day : " + str(n_week)

#sec min hour week day month year
NowTime = [n_seconds,n_minutes,n_hours,n_week,n_day,n_month,n_Year]

w  = ["Mon","Tues","Wed","Thur","Fri","Sat", "Sun"];
#/dev/i2c-1
bus = smbus.SMBus(1)
def ds3231SetTime():
	bus.write_i2c_block_data(address,register,NowTime)
 
def ds3231ReadTime():
	return bus.read_i2c_block_data(address,register,7);
 
ds3231SetTime()
for uujl in range(0,5,1):
	t = ds3231ReadTime()
	t[0] = t[0]&0x7F  #sec
	t[1] = t[1]&0x7F  #min
	t[2] = t[2]&0x3F  #hour
	t[3] = t[3]&0x07  #week
	t[4] = t[4]&0x3F  #day
	t[5] = t[5]&0x1F  #month


	seconds = "%x"%t[0]
	minutes="%x"%(t[1])						
	hours="%x"%t[2]							
	day="%x" %(t[4])
	date="%x" %(t[4])
	month="%x" %(t[5])
	year="%x" %(t[6])

	if int(month) < 10:
		month = '0'+str(month)
	if int(hours) <10:
		hours = '0'+str(hours)
	if int(minutes) <10:
		minutes = '0'+str(minutes)
	if int(date) <10:
		date = '0'+str(date)
	MMDDhhmmYYYY=str(month) + str(date)+str(hours)+str(minutes)+str(year)
	ss = str(seconds)
	finalTime = MMDDhhmmYYYY + '.' +ss
	print "real final time : " + str(finalTime)
	print "20%x/%x/%x %x:%x:%x  %s" %(t[6],t[5],t[4],t[2],t[1],t[0],w[t[3]])
	time.sleep(1.0)
quit()
