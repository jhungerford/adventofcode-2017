package main

import "testing"

func TestMaxIndex(t *testing.T) {
	tests := []maxIndexTestPair{
		{[]int{0, 2, 7, 0}, 2},
		{[]int{2, 4, 1, 2}, 1},
		{[]int{3, 1, 2, 3}, 0},
		{[]int{0, 2, 3, 4}, 3},
	}

	runMaxIndexTests(t, tests)
}

type maxIndexTestPair struct {
	input []int
	result int
}

func runMaxIndexTests(t *testing.T, tests []maxIndexTestPair) {
	for _, pair := range tests {
		result := maxIndex(pair.input)
		if result != pair.result {
			t.Errorf("Wrong value for %v.  Expected %v, got %v", pair.input, pair.result, result)
		}
	}
}

func TestBalance(t *testing.T) {
	tests := []balanceTestPair{
		{[]int{0, 2, 7, 0}, []int{2, 4, 1, 2}},
		{[]int{2, 4, 1, 2}, []int{3, 1, 2, 3}},
		{[]int{3, 1, 2, 3}, []int{0, 2, 3, 4}},
		{[]int{0, 2, 3, 4}, []int{1, 3, 4, 1}},
		{[]int{1, 3, 4, 1}, []int{2, 4, 1, 2}},
	}

	runBalanceTests(t, tests)
}

type balanceTestPair struct {
	input []int
	result []int
}

func runBalanceTests(t *testing.T, tests []balanceTestPair) {
	for _, pair := range tests {
		result := balance(pair.input)

		if len(result) != len(pair.result) {
			t.Errorf("Wrong length for %v.  Expected %v, got %v", pair.input, pair.result, result)
		}

		for i, v := range result {
			if v != pair.result[i] {
				t.Errorf("Wrong value for %v.  Expected %v, got %v", pair.input, pair.result, result)
			}
		}
	}
}

func TestCycles(t *testing.T) {
	input := []int{0, 2, 7, 0}
	expected := 5
	result := len(cycles(input))

	if result != expected {
		t.Errorf("Wrong number of cycles for %v.  Expected %d, got %d", input, expected, result)
	}
}

func TestCycleLength(t *testing.T) {
	input := []int{0, 2, 7, 0}
	expected := 4
	result := cycleLength(cycles(input))

	if result != expected {
		t.Errorf("Wrong cycle length for %v.  Expected %d, got %d", input, expected, result)
	}
}