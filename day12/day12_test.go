package main

import "testing"

var exampleGraph = &Graph{
[][]int{
{2},
{1},
{0, 3, 4},
{2, 4},
{2, 3, 6},
{6},
{4, 5},
},
}

func TestParse(t *testing.T) {
	rows := []string{
		"0 <-> 2",
		"1 <-> 1",
		"2 <-> 0, 3, 4",
		"3 <-> 2, 4",
		"4 <-> 2, 3, 6",
		"5 <-> 6",
		"6 <-> 4, 5",
	}

	graph := NewGraph(7)

	for _, row := range rows {
		parse(row, graph)
	}

	if !graphsMatch(graph, exampleGraph) {
		t.Error("Parse failed.  Expected:", exampleGraph, "Actual:", graph)
	}
}

func TestSpanningTreeSize(t *testing.T) {
	if spanningTreeSize(exampleGraph, 0) != 6 {
		t.Error("Wrong spanning tree size for node 0.  Expected: 6 Actual:", spanningTreeSize(exampleGraph, 0))
	}

	if spanningTreeSize(exampleGraph, 1) != 1 {
		t.Error("Wrong spanning tree size for node 1.  Expected: 1 Actual:", spanningTreeSize(exampleGraph, 1))
	}
}

func TestNumTrees(t *testing.T) {
	numTrees := numTrees(exampleGraph)
	if numTrees != 2 {
		t.Error("Wrong number of trees.  Expected: 2 Actual:", numTrees)
	}
}


func graphsMatch(a, b *Graph) bool {
	if len(a.adjacent) != len(b.adjacent) {
		return false
	}

	for i, neighbors := range a.adjacent {
		if len(neighbors) != len(b.adjacent[i]) {
			return false
		}

		for j, neighbor := range neighbors {
			if neighbor != b.adjacent[i][j] {
				return false
			}
		}
	}

	return true
}
