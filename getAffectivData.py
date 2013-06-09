#!/usr/bin/python

"""
call python getAffectivData [FILE_NAME]
Assumes data is structured as columns of short term exc, long term,
engage_bored, meditation, frustration.
"""

import sys
file_name = sys.argv[1]
f = open(file_name, "r")
lines = f.read().split("\n")

ex_short = []
ex_long = []
engage_bored = []
meditation = []
frustration = []

for i in range(2, len(lines)-2):
	items = lines[i].split(" ")
	ex_short.append(items[0])
	ex_long.append(items[1])
	engage_bored.append(items[2])
	meditation.append(items[3])
	frustration.append(items[4])
f.close()
print "exshort:" + str(ex_short)