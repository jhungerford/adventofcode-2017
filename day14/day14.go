package main

import (
	"fmt"
	"../knothash"
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

func main() {
	input := "hfdlxzhv"

	disk := MakeDisk(input)

	fmt.Println("Part 1:", disk.CountUsed())
}
