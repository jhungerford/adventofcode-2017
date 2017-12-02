package main

import "testing"

func checkRowChecksum(t *testing.T, expected int, row []int) {
	actual := rowChecksum(row)

	if actual != expected {
		t.Errorf("Wrong row checksum for row %v.  Expected: %d, Actual: %d", row, expected, actual)
	}
}

func TestRowChecksum(t *testing.T) {
	checkRowChecksum(t, 8, []int {5, 1, 9, 5})
	checkRowChecksum(t, 4, []int {7, 5, 3})
	checkRowChecksum(t, 6, []int {2, 4, 6, 8})
}

func TestChecksum(t *testing.T) {
	input := [][]int {
		{5, 1, 9, 5},
		{7, 5, 3},
		{2, 4, 6, 8},
	}

	expected := 18
	actual := checksum(input)

	if actual != expected {
		t.Errorf("Wrong checksum for input %v.  Expected: %d, Actual: %d", input, expected, actual)
	}
}
