#!/usr/bin/python

from itertools import combinations, product
from multiprocessing import Pool, Manager
from functools import partial
import math
import json

SHAPE_SIZE = 11


def dist(a, b):
    return math.sqrt(math.pow(a[0] - b[0], 2) + math.pow(a[1] - b[1], 2))


def print_shape(shape):
    s = set([tuple(i) for i in shape])
    grid = ["-" * SHAPE_SIZE for _ in range(SHAPE_SIZE)]

    for (x, y) in s:
        row = list(grid[x - 1])
        row[y - 1] = "#"
        grid[x - 1] = "".join(row)

    for row in grid:
        print row


def edges(shape):
    total_edges = 0
    for i in range(len(shape) - 1):
        local_edges = 0
        for j in range(i + 1, len(shape)):
            if dist(shape[i], shape[j]) == 1:
                local_edges += 1
                total_edges += 1

        if local_edges == 0:
            return 0

    return total_edges


def find(shapes, shape):
    for each in shapes:
        e = set(each)
        s = set(shape)
        if len(e ^ s) == 0:
            return True

    return False


def multi(shapes, shape):
    # start = timeit.default_timer()
    edgs = edges(shape)
    if edgs >= SHAPE_SIZE - 1:
        x_min = min(shape, key=lambda p: p[0])
        if x_min[0] > 1:
            val = x_min[0] - 1
            for i in range(len(shape)):
                shape = shape[:i] + ((shape[i][0] - val, shape[i][1]),) + shape[i + 1:]
        y_min = min(shape, key=lambda p: p[1])
        if y_min[1] > 1:
            val = y_min[1] - 1
            for i in range(len(shape)):
                shape = shape[:i] + ((shape[i][0], shape[i][1] - val),) + shape[i + 1:]

    # stop = timeit.default_timer()
    # print stop - start


def main():
    mngr = Manager()
    shapes = mngr.list()
    func = partial(multi, shapes)
    pool = Pool()
    grid = product(xrange(1, SHAPE_SIZE + 1), xrange(1, SHAPE_SIZE + 1))
    lst = [cell for cell in grid]

    pool.imap_unordered(func, combinations(lst, SHAPE_SIZE), 2500000)
    pool.close()
    pool.join()

    for each in shapes:
        print json.dumps(each)
        print_shape(each)
        print

    print len(shapes)

if __name__ == "__main__":
    main()
