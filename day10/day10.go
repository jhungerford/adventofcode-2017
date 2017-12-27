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

	yarn := NewYarn(256)
	for _, lengthStr := range strings.Split(strings.TrimSpace(string(bytes)), ",") {
		length, err := strconv.Atoi(lengthStr)
		if err != nil {
			fmt.Println("Invalid length", lengthStr)
			return
		}

		yarn = knot(yarn, length)
	}

	fmt.Println("Part 1:", yarn.elements[0] * yarn.elements[1])
}
