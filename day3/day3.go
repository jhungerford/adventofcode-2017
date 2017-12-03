package main

import "fmt"

type Point struct {
	X, Y int
}

type PointState struct {
	Num, SideLength, SideIndex int
	Point Point
	Mover Mover
}

type Mover func(state PointState) (PointState)

func upMover(state PointState) (PointState) {
	num := state.Num + 1
	point := Point{state.Point.X, state.Point.Y + 1}
	index := state.SideIndex + 1

	if index == state.SideLength {
		return PointState{num, state.SideLength + 1, 0, point, leftMover}
	}

	return PointState{num, state.SideLength, index, point, upMover}
}

func leftMover(state PointState) (PointState) {
	num := state.Num + 1
	point := Point{state.Point.X - 1, state.Point.Y}
	index := state.SideIndex + 1

	if index == state.SideLength {
		return PointState{num, state.SideLength, 0, point, downMover}
	}

	return PointState{num, state.SideLength, index, point, leftMover}
}

func downMover(state PointState) (PointState) {
	num := state.Num + 1
	point := Point{state.Point.X, state.Point.Y - 1}
	index := state.SideIndex + 1

	if index == state.SideLength {
		return PointState{num, state.SideLength + 1, 0, point, rightMover}
	}

	return PointState{num, state.SideLength, index, point, downMover}
}

func rightMover(state PointState) (PointState) {
	num := state.Num + 1
	point := Point{state.Point.X + 1, state.Point.Y}
	index := state.SideIndex + 1

	if index == state.SideLength {
		return PointState{num, state.SideLength, 0, point, upMover}
	}

	return PointState{num, state.SideLength, index, point, rightMover}
}

// Given a number, returns the point where the number lives in memory.
// Positive X is to the right, and positive Y is up.  1 is {0, 0}
func location(num int) (Point) {
	state := PointState{1, 1, 0, Point{0, 0}, rightMover}

	for state.Num < num {
		state = state.Mover(state)
	}

	return state.Point
}

// Given a point, returns the manhatten distance between the point and the square 1
func distance(point Point) (int) {
	xDist := point.X
	yDist := point.Y

	if xDist < 0 {
		xDist *= -1
	}

	if yDist < 0 {
		yDist *= -1
	}

	return xDist + yDist
}

func main() {
	input := 325489

	fmt.Printf("Part 1: %d\n", distance(location(input)))
}
