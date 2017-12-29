// Package knothash implements the knot hash algorithm introduced in day 10
package knothash

import (
	"fmt"
)

type KnotHash []int

type Yarn struct {
	Elements []int
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
func Knot(yarn Yarn, length int) Yarn {
	numElements := len(yarn.Elements)

	// Reverse the order of that length of elements in the list, starting with the element at the current position
	for i := 0; i < length / 2; i ++ {
		indexA := (yarn.current + i) % numElements
		indexB := (yarn.current + length + numElements - i - 1) % numElements

		yarn.Elements[indexA], yarn.Elements[indexB] = yarn.Elements[indexB], yarn.Elements[indexA]
	}

	// Move the current position forward by that length plus the skip size
	yarn.current = (yarn.current + length + yarn.skipSize) % numElements

	// Increase the skip size by one
	yarn.skipSize ++

	return yarn
}


var magicNumbers = []int{17, 31, 73, 47, 23}

// Encodes input into a yarn by converting each digit to ascii and appending magic numbers at the end
func encodeInput(input string) []int {
	encoded := make([]int, len(input) + 5)

	// Ascii representation of each character of the input
	for i, value := range input {
		encoded[i] = int(value)
	}

	// Followed by magic numbers
	for i, value := range magicNumbers {
		encoded[i + len(input)] = value
	}

	return encoded
}

func Hash(input string) KnotHash {
	lengths := encodeInput(input)
	yarn := NewYarn(256)

	for i := 0; i < 64; i ++ {
		for _, length := range lengths {
			yarn = Knot(yarn, length)
		}
	}

	denseHash := make(KnotHash, 16)
	for block := 0; block < 16; block ++ {
		condensed := 0

		for i := 0; i < 16; i ++ {
			condensed ^= yarn.Elements[block * 16 + i]
		}

		denseHash[block] = condensed
	}

	return denseHash
}

func (hash KnotHash) String() string {
	output := ""
	for _, value := range hash {
		output += fmt.Sprintf("%02x", value)
	}

	return output
}
