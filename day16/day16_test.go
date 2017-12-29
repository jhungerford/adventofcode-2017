package main

import "testing"

func TestExample(t *testing.T) {
	moves, err := ParseMoves("s1,x3/4,pe/b")
	if err != nil {
		t.Error("Error parsing moves", err)
	}

	positions := NewPositions(5)

	for _, move := range moves {
		move.Move(positions)
	}

	expected := "baedc"
	actual := string(*positions)

	if expected != actual {
		t.Error("Wrong value for dance", moves, "Expected:", expected, "Actual:", actual)
	}
}

func TestSpinMove(t *testing.T) {
	move := SpinMove{1}
	positions := NewPositions(5)

	move.Move(positions)

	expected := "eabcd"
	actual := string(*positions)

	if expected != actual {
		t.Error("Wrong value for spin move.  Expected", expected, "Actual:", actual)
	}
}

func TestExchangeMove(t *testing.T) {
	move := ExchangeMove{3, 4}
	positions := Positions([]byte{'e', 'a', 'b', 'c', 'd'})

	move.Move(&positions)

	expected := "eabdc"
	actual := string(positions)

	if expected != actual {
		t.Error("Wrong value for exchange move.  Expected", expected, "Actual:", actual)
	}
}

func TestPartnerMove(t *testing.T) {
	move := PartnerMove{'e', 'b'}
	positions := Positions([]byte{'e', 'a', 'b', 'd', 'c'})

	move.Move(&positions)

	expected := "baedc"
	actual := string(positions)

	if expected != actual {
		t.Error("Wrong value for partner move.  Expected", expected, "Actual:", actual)
	}
}
