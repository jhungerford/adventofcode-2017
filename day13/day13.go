package main

import (
	"strconv"
	"strings"
	"errors"
	"os"
	"bufio"
	"container/list"
	"fmt"
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
		if firewall.depths[position] <= 1 {
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

func TripSeverity(firewall *Firewall) int {
	severity := 0
	for firewall.packetPosition < len(firewall.depths) - 1 {
		firewall.AdvancePacket()
		severity += firewall.Severity()
		firewall.AdvanceScanners()
	}

	return severity
}

// Reads newline separated lines from the file with the given name
func readLines(fileName string) ([]string, error) {
	f, err := os.Open(fileName)
	if err != nil {
		return nil, err
	}

	defer f.Close()

	s := bufio.NewScanner(f)
	l := list.New()

	for s.Scan() {
		row := strings.TrimSpace(s.Text())
		if row != "" {
			l.PushBack(row)
		}
	}

	lines := make([]string, l.Len())

	i := 0
	for e := l.Front(); e != nil; e = e.Next() {
		lines[i] = e.Value.(string)
		i ++
	}

	return lines, nil
}

func main() {
	if len(os.Args) != 2 {
		fmt.Println("Usage: ", os.Args[0], " <input file>")
		return
	}

	lines, err := readLines(os.Args[1])
	if err != nil {
		fmt.Println("Error reading", os.Args[1], err)
		return
	}

	firewall, parseErr := Parse(lines)
	if parseErr != nil {
		fmt.Println("Error parsing lines", parseErr)
	}

	fmt.Println("Part 1:", TripSeverity(firewall))
}
