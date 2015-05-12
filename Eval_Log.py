#!/usr/bin/python

'''

HZI, 2015-04-21 (first version)

Read binary data file created by Android app "DataMonitor" (see here:

https://github.com/yigiter/DataMonitor.git

)

and convert it to CSV

'''

import sys, struct, argparse, datetime

parser = argparse.ArgumentParser(description='Process data files created by Android DataMonitor')

parser.add_argument('-infile', '-f', required=True, help='Data file')

subparsers = parser.add_subparsers(help='Either create a CSV with all data (to STDOUT) or act as input filter for Grace')

csv_parser = subparsers.add_parser('csv')
csv_parser.set_defaults(which='csv')

grace_parser = subparsers.add_parser('grace')
grace_parser.set_defaults(which='grace')
grace_parser.add_argument('-sensor', '-s', required=True, help='Sensor ID (e.g. Temp: 13, Humidity: 12, Press: 6, Light: 5; list see here: http://developer.android.com/reference/android/hardware/Sensor.html)')
grace_parser.add_argument('-component', '-c', type=int, default=0, help='Which component of vector valued sensor output to use; default: 0')

args = parser.parse_args()

infile = open(args.infile,"rb")

if args.which == 'csv':
	sys.stdout.write ("SensorType;Time;DataFieldCount;Data1;Data1;Data2;Data4;Data5;Data6\n")

while True:
	try:
		b = infile.read(4)
		sensor_type = struct.unpack(">i",b)[0]
		b = infile.read(8)
		time_stamp = struct.unpack(">Q",b)[0]
		b = infile.read(8)
		b = infile.read(4)
		data_field_count = struct.unpack(">i",b)[0]
		if args.which == 'csv':
			sys.stdout.write ("%i;%s;%i" % (sensor_type,datetime.datetime.fromtimestamp(time_stamp / 1000).strftime('%Y-%m-%d %H:%M:%S'),data_field_count))
			for i in range(0,data_field_count):
				b = infile.read(4)
				sys.stdout.write (";%f" % struct.unpack(">f",b)[0])
			sys.stdout.write ("\n")
		elif args.which == 'grace':
			if sensor_type == int(args.sensor):
				bb = []
				for i in range(0,data_field_count):
					bb.append(infile.read(4))
				sys.stdout.write ("%s %f" % (datetime.datetime.fromtimestamp(time_stamp / 1000).strftime('%Y-%m-%dT%H:%M:%S'),struct.unpack(">f",bb[args.component])[0]))
				sys.stdout.write ("\n")
			else:
				for i in range(0,data_field_count):
					b = infile.read(4)
			
	except (struct.error):
		exit (-1)
