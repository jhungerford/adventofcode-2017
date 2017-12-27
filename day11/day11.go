package main

import (
	"strings"
	"os"
	"fmt"
	"io/ioutil"
)

// Hex coordinate system explained here: https://www.redblobgames.com/grids/hexagons/

// Cube coordinate.  +x is at 3:00, +y is at 11:00, +z is at 7:00
type HexCoordinate struct {
	x, y, z int
}

func movePath(path string) int {
	coord := HexCoordinate{0, 0, 0}

	for _, direction := range strings.Split(path, ",") {
		coord = move(coord, direction)
	}

	return distance(coord)
}

func move(coord HexCoordinate, direction string) HexCoordinate {
	switch direction {
	case "n":
		return HexCoordinate{coord.x, coord.y + 1, coord.z - 1}
	case "ne":
		return HexCoordinate{coord.x + 1, coord.y, coord.z - 1}
	case "se":
		return HexCoordinate{coord.x + 1, coord.y - 1, coord.z}
	case "s":
		return HexCoordinate{coord.x, coord.y - 1, coord.z + 1}
	case "sw":
		return HexCoordinate{coord.x - 1, coord.y, coord.z + 1}
	case "nw":
		return HexCoordinate{coord.x - 1, coord.y + 1, coord.z}
	}

	return coord
}

func distance(coord HexCoordinate) int {
	x, y, z := abs(coord.x), abs(coord.y), abs(coord.z)

	if x > y && x > z {
		return x
	}

	if y > z {
		return y
	}

	return z
}

func abs(i int) int {
	if i < 0 {
		return -1 * i
	}

	return i
}

func main() {
	if len(os.Args) != 2 {
		fmt.Println("Usage: ", os.Args[0], " <input file>")
		return
	}

	bytes, err := ioutil.ReadFile(os.Args[1])
	if err != nil {
		fmt.Println("Error reading", os.Args[1], err)
		return
	}

	input := strings.TrimSpace(string(bytes))

	fmt.Println("Part 1:", movePath(input))

}
