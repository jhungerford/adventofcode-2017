package main

import (
	"regexp"
	"strconv"
	"strings"
	"fmt"
	"os"
	"bufio"
	"container/list"
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
	name     string
	weight   int
	parent   *Node
	children []*Node
}

// Given an array of parsed nodes, builds and returns a tree of nodes
func buildTree(parsedRows []ParsedRow) *Node {
	m := make(map[string]*Node)

	for _, parsedRow := range parsedRows {
		m[parsedRow.name] = &Node{parsedRow.name, parsedRow.weight, nil, []*Node{}}
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

	return root
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

	parsedRows := make([]ParsedRow, len(lines))
	for i, row := range lines {
		parsedRows[i] = parseRow(row)
	}

	tree := buildTree(parsedRows)

	fmt.Print("Part 1: ", tree.name)
}
