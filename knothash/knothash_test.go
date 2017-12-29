package knothash

import "testing"

func TestKnot(t *testing.T) {
	type KnotTestCase struct {
		input Yarn
		length int
		expected Yarn
	}

	testCases := []KnotTestCase{
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
		actual := Knot(testCase.input, testCase.length)
		if !yarnMatches(testCase.expected, actual) {
			t.Error(
				"Wrong value for yarn ", testCase.input,
				"and length", testCase.length,
				"Expected:", testCase.expected,
				"Actual:", actual)
		}
	}
}

func TestEncodeInput(t *testing.T) {
	input := "1,2,3"
	expected := []int{49, 44, 50, 44, 51, 17, 31, 73, 47, 23}
	actual := encodeInput(input)

	if !arraysMatch(expected, actual) {
		t.Error("Wrong value for encodeInput(", input, ").  Expected: ", expected, "Actual: ", actual)
	}
}

func TestHash(t *testing.T) {
	type TestCase struct {
		input string
		expected string
	}

	testCases := []TestCase {
		{"", "a2582a3a0e66e6e86e3812dcb672a272"},
		{"AoC 2017", "33efeb34ea91902bb2f59c9920caa6cd"},
		{"1,2,3", "3efbe78a8d82f29979031a4aa0b16a9d"},
		{"1,2,4", "63960835bcdc130f0b66d7ff4f6a5a8e"},
	}

	for _, testCase := range testCases {
		actual := Hash(testCase.input).String()
		if actual != testCase.expected {
			t.Error("Wrong value for hash", testCase.input, "Expected", testCase.expected, "Actual", actual)
		}
	}
}

func yarnMatches(a, b Yarn) bool {
	return a.current == b.current && a.skipSize == b.skipSize && arraysMatch(a.elements, b.elements)
}

func arraysMatch(a, b []int) bool {
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

