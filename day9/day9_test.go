package main

import "testing"

type ScoreTestCase struct {
	input string
	expected int
}

func TestScore(t *testing.T) {
	testCases := []ScoreTestCase {
		{"{}", 1},
		{"{{{}}}", 6},
		{"{{},{}}", 5},
		{"{{{},{},{{}}}}", 16},
		{"{<a>,<a>,<a>,<a>}", 1},
		{"{{<ab>},{<ab>},{<ab>},{<ab>}}", 9},
		{"{{<!!>},{<!!>},{<!!>},{<!!>}}", 9},
		{"{{<a!>},{<a!>},{<a!>},{<ab>}}", 3},
	}

	for _, testCase := range testCases {
		actual := score(testCase.input)
		if testCase.expected != actual {
			t.Error("Wrong value for", testCase.input, "Expected:", testCase.expected, "Actual:", actual)
		}
	}
}
