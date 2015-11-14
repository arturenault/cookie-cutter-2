#!/usr/bin/python

from multiprocessing import Pool, Manager
from functools import partial
import itertools
import datetime
import sys

SHAPE_SIZE = 11


def find(shapes, shape):
    if ShapeBuilder.pretty(shape) in shapes:
        return True

    return False


class ShapeBuilder:
    def __init__(self, size):
        self.side = (size * 2) - 1
        self.offset = [self.side, self.side]
        self.grid = [[0 for _ in range((size * 2) - 1)] for _ in range((size * 2) - 1)]
        self.points = []

    def opts(self):
        rslt = list()
        for r, c in self.points:
            if r - 1 >= 0 and not self.grid[r - 1][c]:
                cell = (r - 1, c)
                if cell not in rslt:
                    rslt.append(cell)
            if c + 1 < self.side and not self.grid[r][c + 1]:
                cell = (r, c + 1)
                if cell not in rslt:
                    rslt.append(cell)
            if r + 1 < self.side and not self.grid[r + 1][c]:
                cell = (r + 1, c)
                if cell not in rslt:
                    rslt.append(cell)
            if c - 1 >= 0 and not self.grid[r][c - 1]:
                cell = (r, c - 1)
                if cell not in rslt:
                    rslt.append(cell)

        return rslt

    def set_cell(self, r, c):
        self.grid[r][c] = 1
        if r < self.offset[0]:
            self.offset[0] = r
        if c < self.offset[1]:
            self.offset[1] = c
        self.points.append(tuple((r, c)))

    def shape(self):
        rslt = list()
        for r, c in self.points:
            rslt.append((r - self.offset[0], c - self.offset[1]))

        return tuple(rslt)

    @staticmethod
    def pretty(shape):
        grid = ["-" * SHAPE_SIZE for _ in range(SHAPE_SIZE)]

        for i, j in shape:
            row = list(grid[j])
            row[i] = "#"
            grid[j] = "".join(row)

        return '\n'.join(grid)

    @staticmethod
    def xy2rc(x, y):
        return (SHAPE_SIZE - 1) - y, (SHAPE_SIZE - 1) - x

    @staticmethod
    def rc2xy(r, c):
        return -(c - (SHAPE_SIZE - 1)), -(r - (SHAPE_SIZE - 1))


def multi(shapes, lock, move):
    # start = datetime.datetime.now()
    sb = ShapeBuilder(SHAPE_SIZE)
    sb.set_cell(*sb.xy2rc(0, 0))
    for m in move:
        try:
            sb.set_cell(*sb.opts()[m])
        except:
            return

    shape = sb.shape()
    if not find(shapes, shape):
        key = ShapeBuilder.pretty(shape)
        shapes[key] = shape
        lock.acquire()
        print >> sys.stderr, shape
        print >> sys.stderr,  key
        print >> sys.stderr
        lock.release()

    # print (datetime.datetime.now() - start).total_seconds()


def main():
    mngr = Manager()
    shapes = mngr.dict()
    lock = mngr.Lock()
    func = partial(multi, lock, shapes)
    pool = Pool()

    prod = []
    for i in range(SHAPE_SIZE - 1):
        prod.append(range(int((float(1 + (i + 1)) / 2) * 4)))
    moves = itertools.product(*prod)

    pool.imap_unordered(func, moves, 2500000)
    pool.close()
    pool.join()

    for key, val in shapes.items():
        print val
        print key
        print

    print len(shapes)


if __name__ == "__main__":
    main()
