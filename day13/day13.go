package main

import (
	"strconv"
	"strings"
	"errors"
	"os"
	"fmt"
	"../util"
)

type Firewall struct {
	packetPosition int
	depths []int
	scannerPositions []int
	scannerDirections []int
}

func Parse(lines []string) (*Firewall, error) {
	m := make(map[int]int)

	maxPosition := 0
	for _, line := range lines {
		if line == "" {
			continue
		}

		// position: depth
		split := strings.Split(line, ": ")
		if len(split) != 2 {
			return nil, errors.New("Invalid line '" + line + "'")
		}

		position, positionErr := strconv.Atoi(split[0])
		if positionErr != nil {
			return nil, positionErr
		}

		depth, depthErr := strconv.Atoi(split[1])
		if depthErr != nil {
			return nil, depthErr
		}

		m[position] = depth

		if position > maxPosition {
			maxPosition = position
		}
	}

	firewall := Firewall{
		-1,
		make([]int, maxPosition + 1),
		make([]int, maxPosition + 1),
		make([]int, maxPosition + 1),
	}

	for position, depth := range m {
		firewall.depths[position] = depth
		if firewall.depths[position] > 0 {
			firewall.scannerDirections[position] = 1
		}
	}

	return &firewall, nil
}

func (f *Firewall) AdvancePacket() {
	f.packetPosition ++
}

func (f *Firewall) AdvanceScanners() {
	for i, depth := range f.scannerPositions {
		direction := f.scannerDirections[i]
		if direction == 0 {
			continue
		}

		newDepth := depth + direction
		f.scannerPositions[i] = newDepth
		if newDepth == 0 {
			f.scannerDirections[i] = 1
		} else if newDepth == f.depths[i] - 1 {
			f.scannerDirections[i] = -1
		}
	}
}

func (f *Firewall) Severity() int {
	position := f.packetPosition

	if f.scannerPositions[position] == 0 {
		return position * f.depths[position]
	}

	return 0
}

func (f *Firewall) String() string {
	str := ""

	maxDepth := 0
	for i, depth := range f.depths {
		str += fmt.Sprintf("%2d  ", i)
		if depth > maxDepth {
			maxDepth = depth
		}
	}

	str += "\n"

	// Directions
	for _, direction := range f.scannerDirections {
		if direction == 0 {
			str += "    "
		} else {
			str += fmt.Sprintf("%2d  ", direction)
		}
	}

	str += "\n"

	// Packet and empty layers have a different formatting on the first row
	for i, depth := range f.depths {
		var row []byte

		if depth == 0 {
			row = []byte("... ")
		} else if f.scannerPositions[i] == 0 {
			row = []byte("[S] ")
		} else {
			row = []byte("[ ] ")
		}

		// Packet position is indicated by parenthesis - overwrites layer brackets or ellipsis
		if f.packetPosition == i {
			row[0] = '('
			row[2] = ')'
		}

		str += string(row)
	}

	str += "\n"

	// Remaining layers
	for d := 1; d < maxDepth; d ++ {
		for i, depth := range f.depths {
			if d >= depth {
				str += "    "
			} else if f.scannerPositions[i] == d {
				str += "[S] "
			} else {
				str += "[ ] "
			}
		}

		str += "\n"
	}

	return str
}

func TripSeverity(firewall *Firewall) int {
	severity := 0

	//fmt.Println("Initial State:")
	//fmt.Println(firewall.String())

	for i := 0; firewall.packetPosition < len(firewall.depths) - 1; i ++ {
		//fmt.Println("Picosecond", i)

		firewall.AdvancePacket()
		//fmt.Println(firewall.String())

		severity += firewall.Severity()
		//fmt.Println("  Severity", firewall.Severity())

		firewall.AdvanceScanners()
		//fmt.Println(firewall.String())
	}

	return severity
}

func MinDelay(firewall *Firewall) int {
	// l: layer, d: delay
	// It takes s=2*(depth-1) steps for each layer to return to position 0
	// Min delay to get through the firewall without getting caught is:
	// d, such that (d + l) % s[l] != 0 for all layers that have a non-zero depth

	steps := make([]int, len(firewall.depths))
	for i, depth := range firewall.depths {
		if depth == 0 {
			steps[i] = 0
		} else if depth == 1 {
			// Impossible - scanner never moves
			panic("Scanner never moves from position " + string(i))
		}

		steps[i] = 2 * (depth - 1)
	}

	for delay := 0 ;; delay ++ {
		caught := false

		for i, steps := range steps {
			if steps > 0 && (delay + i) % steps == 0 {
				caught = true
			}
		}

		if !caught {
			return delay
		}
	}
}

func main() {
	if len(os.Args) != 2 {
		fmt.Println("Usage: ", os.Args[0], " <input file>")
		return
	}

	lines, err := util.ReadLines(os.Args[1])
	if err != nil {
		fmt.Println("Error reading", os.Args[1], err)
		return
	}

	firewall, parseErr := Parse(lines)
	if parseErr != nil {
		fmt.Println("Error parsing lines", parseErr)
	}

	firewall2, _ := Parse(lines)

	fmt.Println("Part 1:", TripSeverity(firewall))
	fmt.Println("Part 2:", MinDelay(firewall2))
}
