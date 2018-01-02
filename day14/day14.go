package main

import (
	"fmt"
	"../knothash"
	"container/list"
)

type Disk [][]bool

// Constructs a disk where each row contains the bits of the knot hash of input-row
func MakeDisk(input string) *Disk {
	disk := make(Disk, 128)

	for row := range disk {
		rowInput := fmt.Sprintf("%s-%d", input, row)

		knotHash := knothash.Hash(rowInput)

		disk[row] = make([]bool, 128)

		for i, value := range knotHash {
			for j := 0; j < 8; j ++ {
				disk[row][i * 8 + j] = value & (1 << uint(7 - j)) != 0
			}
		}
	}

	return &disk
}

func (disk *Disk) CountUsed() int {
	count := 0

	for _, row := range *disk {
		for _, used := range row {
			if used {
				count ++
			}
		}
	}

	return count
}

func (disk *Disk) CountRegions() int {
	// Greedily count regions by finding an uncounted cell and turning off it's neighbors.

	// Algorithm needs to modify the disk, so d is a copy that can be destroyed
	size := len(*disk)
	d := make(Disk, size)
	for i, row := range *disk {
		d[i] = make([]bool, size)
		copy(d[i], row)
	}

	regions := 0

	type cell struct {
		row, column int
	}

	for row := range d {
		for column := range d {
			if d[row][column] {
				regions ++

				stack := list.New()

				d[row][column] = false
				stack.PushBack(cell{row, column})

				for entry := stack.Front(); entry != nil; entry = stack.Front() {
					stack.Remove(entry)
					c := entry.Value.(cell)

					neighbors := []cell {
						{c.row-1, c.column},
						{c.row+1, c.column},
						{c.row, c.column-1},
						{c.row, c.column+1},
					}

					for _, n := range neighbors {
						if n.row >= 0 && n.column >= 0 && n.row < size && n.column < size && d[n.row][n.column] {
							d[n.row][n.column] = false
							stack.PushBack(n)
						}
					}
				}
			}
		}
	}

	return regions
}

// Prints the upper left corner of the disk.  Pass the full size to print the whole disk.
func (disk *Disk) PrintCorner(size int) string {
	str := ""

	for _, row := range (*disk)[:size] {
		actualRow := make([]byte, size)
		for j, used := range row[:size] {
			if used {
				actualRow[j] = '#'
			} else {
				actualRow[j] = '.'
			}
		}

		str += string(actualRow) + "\n"
	}

	return str
}

func main() {
	input := "hfdlxzhv"

	disk := MakeDisk(input)

	fmt.Println("Part 1:", disk.CountUsed())
	fmt.Println("Part 2:", disk.CountRegions())
}
