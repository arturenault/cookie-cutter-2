#!/usr/bin/python

import itertools
import json

big = list()
mid = list()
sml = list()

fd = open('11-piece.upd.txt', 'r')
for line in fd.readlines():
    try:
        data = json.loads(line)
        if len(data) == 11:
            big.append(data)
    except:
        pass
fd.close()

fd = open('8-piece.upd.txt', 'r')
for line in fd.readlines():
    try:
        data = json.loads(line)
        if len(data) == 8:
            mid.append(data)
    except:
        pass
fd.close()

fd = open('5-piece.upd.txt', 'r')
for line in fd.readlines():
    try:
        data = json.loads(line)
        if len(data) == 5:
            sml.append(data)
    except:
        pass
fd.close()

for each in itertools.product(big, mid, sml):
    print each
