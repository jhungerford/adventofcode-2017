package main

import "testing"

const exampleInput = "flqrgnkx"

func TestMakeDisk(t *testing.T) {
	expectedCorner := []string {
		"##.#.#..",
		".#.#.#.#",
		"....#.#.",
		"#.#.##.#",
		".##.#...",
		"##..#..#",
		".#...#..",
		"##.#.##.",
	}

	disk := MakeDisk(exampleInput)

	actualCorner := make([]string, len(expectedCorner))

	for i, row := range (*disk)[:len(expectedCorner)] {
		rowLength := len(expectedCorner[i])
		actualRow := make([]byte, rowLength)
		for j, used := range row[:rowLength] {
			if used {
				actualRow[j] = '#'
			} else {
				actualRow[j] = '.'
			}
		}

		actualCorner[i] = string(actualRow)
	}

	if !arraysEqual(expectedCorner, actualCorner) {
		t.Error("Wrong value for MakeDisk", exampleInput, ".  Expected:", expectedCorner, "Actual:", actualCorner)
	}
}

func TestCountUsed(t *testing.T) {
	disk := MakeDisk(exampleInput)

	expected := 8108
	actual := disk.CountUsed()

	if expected != actual {
		t.Error("Wrong value for CountUsed", exampleInput, "Expected:", expected, "Actual:", actual)
	}
}

func arraysEqual(a, b []string) bool {
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
