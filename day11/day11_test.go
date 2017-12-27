package main

import "testing"

func TestPart1Examples(t *testing.T) {
	type TestCase struct {
		input string
		expected int
	}

	testCases := []TestCase {
		{"ne,ne,ne", 3},
		{"ne,ne,sw,sw", 0},
		{"ne,ne,s,s", 2},
		{"se,sw,se,sw,sw", 3},
	}

	for _, testCase := range testCases {
		actual := movePath(testCase.input)

		if testCase.expected != actual {
			t.Error("Wrong distance for path", testCase.input, "Expected", testCase.expected, "Actual", actual)
		}
	}
}
