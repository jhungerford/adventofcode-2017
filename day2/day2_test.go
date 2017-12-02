package main

import "testing"

func checkMinMaxRowChecksum(t *testing.T, expected int, row []int) {
	actual := minMaxRowChecksum(row)

	if actual != expected {
		t.Errorf("Wrong min/max row checksum for row %v.  Expected: %d, Actual: %d", row, expected, actual)
	}
}

func TestMinMaxRowChecksum(t *testing.T) {
	checkMinMaxRowChecksum(t, 8, []int {5, 1, 9, 5})
	checkMinMaxRowChecksum(t, 4, []int {7, 5, 3})
	checkMinMaxRowChecksum(t, 6, []int {2, 4, 6, 8})
}

func TestMinMaxChecksum(t *testing.T) {
	input := [][]int {
		{5, 1, 9, 5},
		{7, 5, 3},
		{2, 4, 6, 8},
	}

	expected := 18
	actual := checksum(minMaxRowChecksum, input)

	if actual != expected {
		t.Errorf("Wrong min/max checksum for input %v.  Expected: %d, Actual: %d", input, expected, actual)
	}
}

func checkDivisionRowChecksum(t *testing.T, expected int, row []int) {
	actual := divisionRowChecksum(row)

	if actual != expected {
		t.Errorf("Wrong division checksum for row %v.  Expected: %d, Actual: %d", row, expected, actual)
	}
}

func TestDivisionRowChecksum(t *testing.T) {
	checkDivisionRowChecksum(t, 4, []int {5, 9, 2, 8})
	checkDivisionRowChecksum(t, 3, []int {9, 4, 7, 3})
	checkDivisionRowChecksum(t, 2, []int {3, 8, 6, 5})
}

func TestDivisionChecksum(t *testing.T) {
	input := [][]int {
		{5, 9, 2, 8},
		{9, 4, 7, 3},
		{3, 8, 6, 5},
	}

	actual := checksum(divisionRowChecksum, input)
	expected := 9

	if actual != expected {
		t.Errorf("Wrong division checksum for input %v.  Expected: %d, Actual: %d", input, actual, expected)
	}
}