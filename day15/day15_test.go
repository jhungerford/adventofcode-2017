package main

import "testing"

func TestAdvance(t *testing.T) {
	type TestCase struct {
		initValue uint
		factor uint
		expectedValues []uint
	}

	testCases := []TestCase {
		{65, AFactor, []uint {
			1092455,
			1181022009,
			245556042,
			1744312007,
			1352636452,
		}},
		{8921, BFactor, []uint {
			430625591,
			1233683848,
			1431495498,
			137874439,
			285222916,
		}},
	}

	for _, testCase := range testCases {
		generator := &Generator{testCase.initValue, testCase.factor, 1}

		for i, expected := range testCase.expectedValues {
			actual := generator.NextValue()

			if expected != actual {
				t.Error("Wrong value for NextValue", i, "Expected:", expected, "Actual:", actual)
			}
		}
	}
}

func TestJudge(t *testing.T) {
	type TestCase struct {
		aValue, bValue uint
		expected bool
	}

	testCases := []TestCase{
		{1092455, 430625591, false},
		{1181022009, 1233683848, false},
		{245556042, 1431495498, true},
		{1744312007, 137874439, false},
		{1352636452, 285222916, false},
	}

	for _, testCase := range testCases {
		a, b := &Generator{testCase.aValue, AFactor, 1}, &Generator{testCase.bValue, BFactor, 1}
		actual := Judge(a, b)

		if actual != testCase.expected {
			t.Errorf("Wrong result for judge(%d, %d).  Expected: %v Actual: %v",
				testCase.aValue, testCase.bValue, testCase.expected, actual)
		}
	}
}

func TestTotalCount(t *testing.T) {
	expected := 588
	actual := totalCount(65, 8921)

	if expected != actual {
		t.Error("Wrong total count.  Expected:", expected, "Actual:", actual)
	}
}
