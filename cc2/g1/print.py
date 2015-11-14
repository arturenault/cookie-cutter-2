#!/usr/bin/python
import re


def pretty(shape):
    grid = ["-" * len(shape) for _ in range(len(shape))]

    for i, j in shape:
        row = list(grid[j])
        row[i] = "#"
        grid[j] = "".join(row)

    return '\n'.join(grid)


def main():
    fd = open('shapes_data.txt', 'r')

    shapes = dict()
    ptn = re.compile('\d+')

    for i in range(1, 10):
        shapes[i] = dict()
        for j in [11, 8, 5]:
            shapes[i][j] = dict()

    for line in fd.readlines():
        cols = line.split('|')

        for i, s in zip(range(9, 12), [11, 8, 5]):
            rslt = ptn.findall(cols[i])
            shape = list()
            for d in range(0, len(rslt), 2):
                shape.append((int(rslt[d]), int(rslt[d + 1])))
            shapes[int(cols[3])][s][pretty(shape)] = shape

        for i, s in zip(range(12, 15), [11, 8, 5]):
            rslt = ptn.findall(cols[i])
            shape = list()
            for d in range(0, len(rslt), 2):
                shape.append((int(rslt[d]), int(rslt[d + 1])))
            shapes[int(cols[4])][s][pretty(shape)] = shape

        # print json.dumps(shapes)

    for i in range(1, 10):
        print "g" + str(i)
        for j in [11, 8, 5]:
            for key, val in shapes[i][j].items():
                print key
                print

    fd.close()

if __name__ == '__main__':
    main()
