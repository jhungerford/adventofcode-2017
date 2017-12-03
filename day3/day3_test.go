package main

import "testing"

func checkDistance(t *testing.T, expected int, point Point) {
	actual := distance(point)

	if expected != actual {
		t.Errorf("Wrong distance for point %v.  Expected: %d, Actual: %d", point, expected, actual)
	}
}

func TestDistance(t *testing.T) {
	checkDistance(t, 0, Point{0, 0})
	checkDistance(t, 4, Point{2, 2})
	checkDistance(t, 3, Point{-1, 2})
	checkDistance(t, 5, Point{2, -3})
	checkDistance(t, 31, Point{-15, 16})
}

func checkLocation(t *testing.T, expected Point, input int) {
	actual := location(input)

	if expected != actual {
		t.Errorf("Wrong location for input %d.  Expected: %v, Actual: %v", input, expected, actual)
	}
}

func TestLocation(t *testing.T) {
	checkLocation(t, Point{0, 0}, 1)
	checkLocation(t, Point{2, 1}, 12)
	checkLocation(t, Point{0, -2}, 23)
	checkLocation(t, Point{-15, 16}, 1024)
}
