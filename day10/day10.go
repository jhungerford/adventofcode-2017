package main

import (
	"fmt"
	"os"
	"io/ioutil"
	"strings"
	"../knothash"
	"strconv"
)


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

	lengths, lengthsErr := stringToNumbers(input)
	if lengthsErr != nil {
		fmt.Println("Error converting input to numbers", err)
		return
	}

	part1Yarn := knothash.NewYarn(256)
	for _, length := range lengths {
		part1Yarn = knothash.Knot(part1Yarn, length)
	}

	fmt.Println("Part 1:", part1Yarn.Elements[0] * part1Yarn.Elements[1])
	fmt.Println("Part 2:", knothash.Hash(input))
}
