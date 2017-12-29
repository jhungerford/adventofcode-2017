package main

import (
	"strings"
	"fmt"
	"strconv"
	"os"
	"io/ioutil"
)

type Positions []byte

func NewPositions(num int) *Positions {
	positions := make(Positions, num)

	for i := range positions {
		positions[i] = byte('a' + i)
	}

	return &positions
}

type Move interface {
	Move(*Positions)
}

type SpinMove struct {
	size int
}

func (move SpinMove) Move(positionsRef *Positions) {
	positions := *positionsRef
	numPositions := len(positions)

	newPositions := make(Positions, len(positions))
	for i := 0; i < numPositions; i ++ {
		newPositions[i] = positions[(numPositions + i - move.size) % numPositions]
	}

	for i, newPosition := range newPositions {
		positions[i] = newPosition
	}
}

type ExchangeMove struct {
	positionA, positionB int
}

func (move ExchangeMove) Move(positionsRef *Positions) {
	positions := *positionsRef
	positions[move.positionA], positions[move.positionB] = positions[move.positionB], positions[move.positionA]
}

type PartnerMove struct {
	programA, programB byte
}

func (move PartnerMove) Move(positionsRef *Positions) {
	positions := *positionsRef

	aPos := strings.Index(string(positions), string(move.programA))
	bPos := strings.Index(string(positions), string(move.programB))

	positions[aPos], positions[bPos] = positions[bPos], positions[aPos]
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

	fmt.Println("Part 1:", string(*positions))
}
