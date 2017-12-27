package main

import (
	"regexp"
	"strconv"
	"strings"
	"container/list"
	"os"
	"fmt"
	"bufio"
)

type Graph struct {
	// Adjacency list.  Each element in the array is the set of neighbor vertexes
	// https://en.wikipedia.org/wiki/Adjacency_list
	adjacent [][]int
}

// Constructs a new graph that has the given number of vertexes
func NewGraph(size int) *Graph {
	return &Graph{make([][]int, size)}
}

// Parses the given row in the format vertex <-> neighbors, modifying the graph to store the neighbors
var rowRegex = regexp.MustCompile("(\\d+) <-> ([0-9, ]+)")
func parse(row string, graph *Graph) {
	if row == "" {
		return
	}

	groups := rowRegex.FindStringSubmatch(row)

	if groups == nil {
		panic("Invalid row '" + row + "'")
	}

	vertex, err := strconv.Atoi(groups[1])
	if err != nil {
		panic(err)
	}

	neighborStrs := strings.Split(groups[2], ", ")
	neighbors := make([]int, len(neighborStrs))

	for i, neighborStr := range neighborStrs {
		neighbors[i], err = strconv.Atoi(neighborStr)
		if err != nil {
			panic(err)
		}
	}

	graph.adjacent[vertex] = neighbors
}

// Return the size of the spanning tree connected to the given node
func spanningTreeSize(graph *Graph, node int) int {
	visited := make(map[int]bool)

	toVisit := list.New()

	toVisit.PushBack(node)
	visited[node] = true

	for toVisit.Len() > 0 {
		current := toVisit.Front()
		toVisit.Remove(current)

		for _, neighbor := range graph.adjacent[current.Value.(int)] {
			if !visited[neighbor] {
				toVisit.PushBack(neighbor)
				visited[neighbor] = true
			}
		}
	}

	return len(visited)
}

func numTrees(graph *Graph) int {
	visitedNodes := make([]bool, len(graph.adjacent))
	numTrees := 0

	for i, visited := range visitedNodes {
		if visited {
			continue
		}

		numTrees ++
		toVisit := list.New()
		toVisit.PushBack(i)
		visitedNodes[i] = true

		for toVisit.Len() > 0 {
			current := toVisit.Front()
			toVisit.Remove(current)

			for _, neighbor := range graph.adjacent[current.Value.(int)] {
				if !visitedNodes[neighbor] {
					toVisit.PushBack(neighbor)
					visitedNodes[neighbor] = true
				}
			}
		}
	}

	return numTrees
}

// Reads newline separated lines from the file with the given name
func readLines(fileName string) ([]string, error) {
	f, err := os.Open(fileName)
	if err != nil {
		return nil, err
	}

	defer f.Close()

	s := bufio.NewScanner(f)
	l := list.New()

	for s.Scan() {
		row := strings.TrimSpace(s.Text())
		if row != "" {
			l.PushBack(row)
		}
	}

	lines := make([]string, l.Len())

	i := 0
	for e := l.Front(); e != nil; e = e.Next() {
		lines[i] = e.Value.(string)
		i ++
	}

	return lines, nil
}

func main() {
	if len(os.Args) != 2 {
		fmt.Print("Usage: ", os.Args[0], " <input file>")
		return
	}

	lines, err := readLines(os.Args[1])
	if err != nil {
		fmt.Print("Error reading", os.Args[1], err)
		return
	}

	graph := NewGraph(2000)

	for _, line := range lines {
		parse(line, graph)
	}

	fmt.Println("Part 1:", spanningTreeSize(graph, 0))
	fmt.Println("Part 2:", numTrees(graph))
}
