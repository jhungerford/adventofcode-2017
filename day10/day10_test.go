package main

import "testing"

type TestCase struct {
	input Yarn
	length int
	expected Yarn
}

func TestKnot(t *testing.T) {
	testCases := []TestCase{
		{
			Yarn{[]int{0, 1, 2, 3, 4}, 0, 0},
			3,
			Yarn{[]int{2, 1, 0, 3, 4}, 3, 1},
		},{
			Yarn{[]int{2, 1, 0, 3, 4}, 3, 1},
			4,
			Yarn{[]int{4, 3, 0, 1, 2}, 3, 2},
		},{
			Yarn{[]int{4, 3, 0, 1, 2}, 3, 2},
			1,
			Yarn{[]int{4, 3, 0, 1, 2}, 1, 3},
		},{
			Yarn{[]int{4, 3, 0, 1, 2}, 1, 3},
			5,
			Yarn{[]int{3, 4, 2, 1, 0}, 4, 4},
		},
	}

	for _, testCase := range testCases {
		actual := knot(testCase.input, testCase.length)
		if !yarnMatches(testCase.expected, actual) {
			t.Error(
				"Wrong value for yarn ", testCase.input,
				"and length", testCase.length,
				"Expected:", testCase.expected,
				"Actual:", actual)
		}
	}
}

func yarnMatches(a, b Yarn) bool {
	if a.current != b.current || a.skipSize != b.skipSize || len(a.elements) != len(b.elements) {
		return false
	}

	for i, v := range a.elements {
		if v != b.elements[i] {
			return false
		}
	}

	return true
}
