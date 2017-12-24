package main

import "testing"

type TestCase struct {
	input string
	expected int
}

func TestScore(t *testing.T) {
	testCases := []TestCase{
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
		if testCase.expected != actual.score {
			t.Error("Wrong score for", testCase.input, "Expected:", testCase.expected, "Actual:", actual.score)
		}
	}
}

func TestGarbageCount(t *testing.T) {
	testCases := []TestCase {
		{"<>", 0},
		{"<random characters>", 17},
		{"<<<<>", 3},
		{"<{!>}>", 2},
		{"<!!>", 0},
		{"<!!!>>", 0},
		{"<{o\"i!a,<{i<a>", 10},
	}

	for _, testCase := range testCases {
		actual := score(testCase.input)
		if testCase.expected != actual.garbageCount {
			t.Error("Wrong garbage count for", testCase.input, "Expected:", testCase.expected, "Actual:", actual.garbageCount)
		}
	}
}
