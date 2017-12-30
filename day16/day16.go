package main

import (
	"strings"
	"fmt"
	"strconv"
	"os"
	"io/ioutil"
)

type Positions struct {
	// Number of steps the dancers have been rotated to the right
	rotation int
	// Array of a-e, value is the position of that dancer
	positions []int
	// Array of 0-4, value is dancer in that position
	dancers []byte
}

func NewPositions(num int) *Positions {
	positions := Positions{
		0,
		make([]int, num),
		make([]byte, num),
	}

	for i := 0; i < num; i ++ {
		positions.positions[i] = i
		positions.dancers[i] = byte('a' + i)
	}

	return &positions
}

func (p *Positions) Set(dancers []byte) *Positions {
	if len(dancers) != len(p.dancers) {
		panic("Length mismatch between dancers and positions")
	}

	p.rotation = 0

	for i, dancer := range dancers {
		p.positions[dancerIndex(dancer)] = i
		p.dancers[i] = dancer
	}

	return p
}

func (p *Positions) String() string {
	numDancers := len(p.dancers)
	arr := make([]byte, numDancers)

	for i := range p.dancers {
		arr[i] = p.dancers[spinPosition(p, i)]
	}

	return string(arr)
}

// Returns the given position, spun by the amount on the Positions
func spinPosition(p *Positions, i int) int {
	return (len(p.dancers) + i - p.rotation) % len(p.dancers)
}

// Returns the index of the given dancer
//func (p *Positions) positionIndex(dancer byte) int {
//
//}

// Returns the index of the dancer at the given position
//func (p *Positions) dancerIndex(position int) int {
//
//}

func dancerIndex(dancer byte) int {
	return int(dancer - 'a')
}

type Move interface {
	Move(*Positions)
}

// s1 - spin, amount
type SpinMove struct {
	size int
}

func (move SpinMove) Move(p *Positions) {
	p.rotation = (p.rotation + move.size) % len(p.positions)
}

// x3/4 - exchange dancers at position 3 and 4
type ExchangeMove struct {
	positionA, positionB int
}

func (move ExchangeMove) Move(p *Positions) {
	spinPositionA, spinPositionB := spinPosition(p, move.positionA), spinPosition(p, move.positionB)

	dancerA, dancerB := p.dancers[spinPositionA], p.dancers[spinPositionB]

	p.dancers[spinPositionA], p.dancers[spinPositionB] = dancerB, dancerA
	p.positions[dancerIndex(dancerA)], p.positions[dancerIndex(dancerB)] = spinPositionB, spinPositionA
}

// pe/b - exchange positions of dancers e and b
type PartnerMove struct {
	programA, programB byte
}

func (move PartnerMove) Move(p *Positions) {
	positionA, positionB := p.positions[dancerIndex(move.programA)], p.positions[dancerIndex(move.programB)]

	p.dancers[positionA], p.dancers[positionB] = move.programB, move.programA
	p.positions[dancerIndex(move.programA)], p.positions[dancerIndex(move.programB)] = positionB, positionA
}

// Parses the given comma-separated list of moves into an array of moves
func ParseMoves(str string) ([]Move, error) {
	strMoves := strings.Split(str, ",")

	moves := make([]Move, len(strMoves))
	for i, strMove := range strMoves {
		move, err := parseMove(strMove)
		if err != nil {
			return nil, err
		}

		moves[i] = move
	}

	return moves, nil
}

func parseMove(str string) (Move, error) {
	switch str[0] {
	case 's': // Spin: sX
		size, err := strconv.Atoi(str[1:])
		if err != nil {
			return nil, err
		}

		return SpinMove{size}, nil

	case 'x': // Exchange: xA/B
		slash := strings.Index(str, "/")
		aPos, aErr := strconv.Atoi(str[1:slash])
		if aErr != nil {
			return nil, aErr
		}

		bPos, bErr := strconv.Atoi(str[slash + 1:])
		if bErr != nil {
			return nil, bErr
		}

		return ExchangeMove{aPos, bPos}, nil

	case 'p': // Partner: pA/B
		return PartnerMove{str[1], str[3]}, nil

	default:
		return nil, fmt.Errorf("invalid move: '%s'", str)
	}
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

	moves, movesErr := ParseMoves(strings.TrimSpace(string(bytes)))
	if movesErr != nil {
		fmt.Println("Error parsing moves", movesErr)
	}

	positions := NewPositions(16)
	for _, move := range moves {
		move.Move(positions)
	}

	fmt.Println("Part 1:", positions.String())

	for i := 1; i < 1000000000; i ++ {
		for _, move := range moves {
			move.Move(positions)
		}

		if i % 100000 == 0 {
			fmt.Println("Progress:", i)
		}
	}

	fmt.Println("Part 2:", positions.String())
}
