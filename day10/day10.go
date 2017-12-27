package main

import (
	"fmt"
	"os"
	"io/ioutil"
	"strings"
	"strconv"
)

type Yarn struct {
	elements []int
	current int
	skipSize int
}

func NewYarn(length int) Yarn {
	elements := make([]int, length)
	for i := 0; i < length; i ++ {
		elements[i] = i
	}

	return Yarn{elements, 0, 0}
}

// Given a yarn and a length, ties a knot and returns the Yarn
func knot(yarn Yarn, length int) Yarn {
	numElements := len(yarn.elements)

	// Reverse the order of that length of elements in the list, starting with the element at the current position
	for i := 0; i < length / 2; i ++ {
		indexA := (yarn.current + i) % numElements
		indexB := (yarn.current + length + numElements - i - 1) % numElements

		yarn.elements[indexA], yarn.elements[indexB] = yarn.elements[indexB], yarn.elements[indexA]
	}

	// Move the current position forward by that length plus the skip size
	yarn.current = (yarn.current + length + yarn.skipSize) % numElements

	// Increase the skip size by one
	yarn.skipSize ++

	return yarn
}

func stringToNumbers(input string) ([]int, error) {
	stringNumbers := strings.Split(input, ",")
	numbers := make([]int, len(stringNumbers))

	var err error
	for i, stringNumber := range stringNumbers {
		numbers[i], err = strconv.Atoi(stringNumber)

		if err != nil {
			return nil, err
		}
	}

	return numbers, nil
}

var MagicNumbers = []int{17, 31, 73, 47, 23}

// Encodes input into a yarn by converting each digit to ascii and appending magic numbers at the end
func encodeInput(input string) []int {
	encoded := make([]int, len(input) + 5)

	// Ascii representation of each character of the input
	for i, value := range input {
		encoded[i] = int(value)
	}

	// Followed by magic numbers
	for i, value := range MagicNumbers {
		encoded[i + len(input)] = value
	}

	return encoded
}

func hash(input string) string {
	lengths := encodeInput(input)
	yarn := NewYarn(256)

	for i := 0; i < 64; i ++ {
		for _, length := range lengths {
			yarn = knot(yarn, length)
		}
	}

	denseHash := make([]int, 16)
	for block := 0; block < 16; block ++ {
		condensed := 0

		for i := 0; i < 16; i ++ {
			condensed ^= yarn.elements[block * 16 + i]
		}

		denseHash[block] = condensed
	}

	output := ""
	for _, value := range denseHash {
		output += fmt.Sprintf("%02x", value)
	}

	return output
}

func main() {
	hash("1,2,3")

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

	lengths, lengthsErr := stringToNumbers(input)
	if lengthsErr != nil {
		fmt.Println("Error converting input to numbers", err)
		return
	}

	part1Yarn := NewYarn(256)
	for _, length := range lengths {
		part1Yarn = knot(part1Yarn, length)
	}

	fmt.Println("Part 1:", part1Yarn.elements[0] * part1Yarn.elements[1])
	fmt.Println("Part 2:", hash(input))
}
