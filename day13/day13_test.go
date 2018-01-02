package main

import (
	"testing"
)

func newExample() *Firewall {
	return &Firewall{
		-1,
		[]int{3, 2, 0, 0, 4, 0, 4},
		[]int{0, 0, 0, 0, 0, 0, 0},
		[]int{1, 1, 0, 0, 1, 0, 1},
	}
}

func TestParse(t *testing.T) {
	lines := []string {
		"0: 3",
		"1: 2",
		"4: 4",
		"6: 4",
	}

	expected := newExample()

	actual, err := Parse(lines)

	if err != nil {
		t.Error("Error parsing example", err)
	}

	if !firewallsMatch(expected, actual) {
		t.Error("Wrong value for parse.  Expected:", expected, "Actual:", actual)
	}
}

func TestAdvancePacket(t *testing.T) {
	expected := newExample()
	expected.packetPosition = 0

	actual := newExample()
	actual.AdvancePacket()

	if !firewallsMatch(expected, actual) {
		t.Error("Wrong value for AdvancePacket.  Expected:", expected, "Actual:", actual)
	}
}

func TestAdvanceScanners(t *testing.T) {
	expected := newExample()
	expected.scannerPositions = []int{1, 1, 0, 0, 1, 0, 1}
	expected.scannerDirections = []int{1, -1, 0, 0, 1, 0, 1}

	actual := newExample()
	actual.AdvanceScanners()

	if !firewallsMatch(expected, actual) {
		t.Error("Wrong value for AdvanceScanners.  Expected:", expected, "Actual:", actual)
	}

	expected.scannerPositions = []int{2, 0, 0, 0, 2, 0, 2}
	expected.scannerDirections = []int{-1, 1, 0, 0, 1, 0, 1}
	actual.AdvanceScanners()

	if !firewallsMatch(expected, actual) {
		t.Error("Wrong value for AdvanceScanners.  Expected:", expected, "Actual:", actual)
	}
}

func TestSeverity(t *testing.T) {
	// Severity = level * depth if the packet and scanner share a cell
	firewall := newExample()

	// Packet and scanner in the same cell, but position  is 0 so the severity is 0
	firewall.packetPosition = 0
	expected := 0
	actual := firewall.Severity()

	if expected != actual {
		t.Error("Wrong severity for firewall", firewall, "Expected", expected, "Actual", actual)
	}

	// Packet and scanner in different cells
	firewall.AdvanceScanners()
	firewall.AdvancePacket()

	actual = firewall.Severity()

	if expected != actual {
		t.Error("Wrong severity for firewall", firewall, "Expected", expected, "Actual", actual)
	}

	// Packet on a level with no scanners so the severity is 0
	firewall.AdvanceScanners()
	firewall.AdvancePacket()

	actual = firewall.Severity()

	if expected != actual {
		t.Error("Wrong severity for firewall", firewall, "Expected", expected, "Actual", actual)
	}

	// Packet and scanner in the same cell at a non-zero position
	firewall = &Firewall{
		6,
		firewall.depths,
		[]int{3, 0, 0, 0, 0, 0, 0},
		[]int{-1, 1, 0, 0, 1, 0, 1},
	}

	expected = 24
	actual = firewall.Severity()

	if expected != actual {
		t.Error("Wrong severity for firewall", firewall, "Expected", expected, "Actual", actual)
	}
}

func TestTripSeverity(t *testing.T) {
	firewall := newExample()

	expected := 24
	actual := TripSeverity(firewall)

	if expected != actual {
		t.Error("Wrong trip severity.  Expected:", expected, "Actual:", actual)
	}
}

func firewallsMatch(a, b *Firewall) bool {
	return a.packetPosition == b.packetPosition &&
		arraysMatch(a.depths, b.depths) &&
		arraysMatch(a.scannerPositions, b.scannerPositions) &&
		arraysMatch(a.scannerDirections, b.scannerDirections)
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
