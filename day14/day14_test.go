package main

import "testing"

const exampleInput = "flqrgnkx"

func TestMakeDisk(t *testing.T) {
	expectedCorner :=
		"##.#.#..\n" +
		".#.#.#.#\n" +
		"....#.#.\n" +
		"#.#.##.#\n" +
		".##.#...\n" +
		"##..#..#\n" +
		".#...#..\n" +
		"##.#.##.\n"

	disk := MakeDisk(exampleInput)
	actualCorner := disk.PrintCorner(8)

	if expectedCorner != actualCorner {
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

func TestCountRegions(t *testing.T) {
	disk := MakeDisk(exampleInput)

	expected := 1242
	actual := disk.CountRegions()

	if expected != actual {
		t.Error("Wrong value for CountRegions.  Expected:", expected, "Actual:", actual)
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
