package main

import (
	"regexp"
	"strconv"
	"strings"
	"fmt"
	"os"
	"../util"
)

type ParsedRow struct {
	name          string
	weight        int
	childrenNames []string
}

// Parses a row like 'pbga (66)' or 'fwft (72) -> ktlj, cntj, xhth'
func parseRow(row string) ParsedRow {
	r := regexp.MustCompile("^([a-z]+) \\(([0-9]+)\\)(?: -> ([a-z, ]+))?$")

	groups := r.FindStringSubmatch(row)

	if groups == nil {
		panic("Invalid row '" + row + "'")
	}

	weight, _ := strconv.Atoi(groups[2])
	var childrenNames []string
	if groups[3] != "" {
		childrenNames = strings.Split(groups[3], ", ")
	}

	return ParsedRow{groups[1], weight, childrenNames}
}

type Node struct {
	name        string
	weight      int
	childWeight int
	parent      *Node
	children    []*Node
}

// Given an array of parsed nodes, builds and returns a tree of nodes
func buildTree(parsedRows []ParsedRow) *Node {
	m := make(map[string]*Node)

	for _, parsedRow := range parsedRows {
		m[parsedRow.name] = &Node{parsedRow.name, parsedRow.weight, 0, nil, []*Node{}}
	}

	var root *Node
	for _, parsedRow := range parsedRows {
		node := m[parsedRow.name]

		node.children = make([]*Node, len(parsedRow.childrenNames))
		for i, name := range parsedRow.childrenNames {
			child := m[name]
			node.children[i] = child
			child.parent = node
		}

		root = node
	}

	for root.parent != nil {
		root = root.parent
	}

	setChildWeight(root)

	return root
}

// Recursively calculates the sum of the weight of this node's children, and sets it on the node.
func setChildWeight(node *Node) {
	sum := 0
	for _, child := range node.children {
		setChildWeight(child)
		sum += child.childWeight
	}

	node.childWeight = sum + node.weight
}

// All of a node's children must have the same weight for the tree to be balanced.
// Finds the node that causes the tree to be unbalanced, and returns what it's weight should be.
func findCorrectWeight(node *Node) int {
	numChildren := len(node.children)
	if numChildren == 0 {
		return 0
	}

	for i, child := range node.children {
		correctWeight := findCorrectWeight(child)

		if correctWeight != 0 {
			return correctWeight
		}

		prevDifference := child.childWeight - node.children[(i + numChildren - 1) % numChildren].childWeight
		nextDifference := child.childWeight - node.children[(i + 1) % numChildren].childWeight
		if prevDifference != 0 && nextDifference != 0 {
			return child.weight - prevDifference
		}
	}

	return 0
}

func main() {
	if len(os.Args) != 2 {
		fmt.Print("Usage: ", os.Args[0], " <input file>")
		return
	}

	lines, err := util.ReadLines(os.Args[1])
	if err != nil {
		fmt.Print("Error reading", os.Args[1], err)
		return
	}

	parsedRows := make([]ParsedRow, len(lines))
	for i, row := range lines {
		parsedRows[i] = parseRow(row)
	}

	tree := buildTree(parsedRows)

	fmt.Println("Part 1: ", tree.name)
	fmt.Println("Part 2: ", findCorrectWeight(tree))
}
