package main

import "fmt"

// Returns the index of the memory bank with the most blocks
func maxIndex(memory []int) int {
	max := 0
	maxIndex := 0

	for i, v := range memory {
		if v > max {
			max, maxIndex = v, i
		}
	}

	return maxIndex
}

// Given an array, computes a new array by finding the memory bank with the most blocks and
// distributing it to each successive block.
func balance(memory []int) []int {
	maxIndex := maxIndex(memory)
	value := memory[maxIndex]

	balanced := make([]int, len(memory))
	copy(balanced, memory)

	balanced[maxIndex] = 0

	for i := 1; i <= value; i ++ {
		balanced[(maxIndex + i) % len(balanced)] ++
	}

	return balanced
}

// Returns whether the memory configuration has been seen before
func contains(memory []int, seen [][]int) bool {
	for _, v := range seen {
		if arrayEquals(memory, v) {
			return true
		}
	}

	return false
}

// Returns whether the two arrays have the same length and values
func arrayEquals(a []int, b []int) bool {
	if len(a) != len(b) {
		return false
	}

	for i, v := range a {
		if v != b[i] {
			return false
		}
	}

	return true
}

// Counts the number of cycles that must be completed before a balance configuration is produced
// that has been seen before.
func cycles(memory []int) int {
	seen := [][]int{memory}

	i := 1
	for memory := balance(memory); !contains(memory, seen); memory = balance(memory) {
		seen = append(seen, memory)
		i ++
	}

	return i
}

func main() {
	input := []int{10, 3, 15, 10, 5, 15, 5, 15, 9, 2, 5, 8, 5, 2, 3, 6}

	fmt.Println("Part 1: ", cycles(input))
}
