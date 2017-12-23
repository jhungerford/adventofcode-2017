package main

import (
	"testing"
)

type ParseRowTestPair struct {
	input string
	expected ParsedRow
}

func TestParseRow(t *testing.T) {
	tests := []ParseRowTestPair{
		{"pbga (66)", ParsedRow{"pbga", 66, []string{}}},
		{"xhth (57)", ParsedRow{"xhth", 57, []string{}}},
		{"ebii (61)", ParsedRow{"ebii", 61, []string{}}},
		{"havc (66)", ParsedRow{"havc", 66, []string{}}},
		{"ktlj (57)", ParsedRow{"ktlj", 57, []string{}}},
		{"fwft (72) -> ktlj, cntj, xhth", ParsedRow{"fwft", 72, []string{"ktlj", "cntj", "xhth"}}},
		{"qoyq (66)", ParsedRow{"qoyq", 66, []string{}}},
		{"padx (45) -> pbga, havc, qoyq", ParsedRow{"padx", 45, []string{"pbga", "havc", "qoyq"}}},
		{"tknk (41) -> ugml, padx, fwft", ParsedRow{"tknk", 41, []string{"ugml", "padx", "fwft"}}},
		{"jptl (61)", ParsedRow{"jptl", 61, []string{}}},
		{"ugml (68) -> gyxo, ebii, jptl", ParsedRow{"ugml", 68, []string{"gyxo", "ebii", "jptl"}}},
		{"gyxo (61)", ParsedRow{"gyxo", 61, []string{}}},
		{"cntj (57)", ParsedRow{"cntj", 57, []string{}}},
	}

	for _, test := range tests {
		actual := parseRow(test.input)

		if !parsedRowsEqual(test.expected, actual) {
			t.Error("Wrong result for parseRow", test.input, " - expected:", test.expected, "actual:", actual)
		}
	}
}

func parsedRowsEqual(a ParsedRow, b ParsedRow) bool {
	if a.name != b.name || a.weight != b.weight {
		return false
	}

	if len(a.childrenNames) != len(b.childrenNames) {
		return false
	}

	for i, av := range a.childrenNames {
		if av != b.childrenNames[i] {
			return false
		}
	}

	return true
}

func TestBuildTree(t *testing.T) {
	expected := Node{"tknk", 41, 778, nil, []*Node{
		{"ugml", 68, 251, nil, []*Node{
			{"gyxo", 61, 61, nil, []*Node{}},
			{"ebii", 61, 61, nil, []*Node{}},
			{"jptl", 61, 61, nil, []*Node{}},
		}},
		{"padx", 45, 243, nil, []*Node{
			{"pbga", 66, 66, nil, []*Node{}},
			{"havc", 66, 66, nil, []*Node{}},
			{"qoyq", 66, 66, nil, []*Node{}},
			}},
		{"fwft", 72, 243, nil, []*Node{
			{"ktlj", 57, 57, nil, []*Node{}},
			{"cntj", 57, 57, nil, []*Node{}},
			{"xhth", 57, 57, nil, []*Node{}},
			}},
	}}

	parsedRows := []ParsedRow {
		{"pbga", 66, []string{}},
		{"xhth", 57, []string{}},
		{"ebii", 61, []string{}},
		{"havc", 66, []string{}},
		{"ktlj", 57, []string{}},
		{"fwft", 72, []string{"ktlj", "cntj", "xhth"}},
		{"qoyq", 66, []string{}},
		{"padx", 45, []string{"pbga", "havc", "qoyq"}},
		{"tknk", 41, []string{"ugml", "padx", "fwft"}},
		{"jptl", 61, []string{}},
		{"ugml", 68, []string{"gyxo", "ebii", "jptl"}},
		{"gyxo", 61, []string{}},
		{"cntj", 57, []string{}},
	}

	actual := buildTree(parsedRows)

	if !treeEqual(&expected, actual) {
		t.Error("Wrong result for buildTree.  Expected", expected, "Actual", actual)
	}
}

func TestFindCorrectWeight(t *testing.T) {
	lines := []string {
		"pbga (66)",
		"xhth (57)",
		"ebii (61)",
		"havc (66)",
		"ktlj (57)",
		"fwft (72) -> ktlj, cntj, xhth",
		"qoyq (66)",
		"padx (45) -> pbga, havc, qoyq",
		"tknk (41) -> ugml, padx, fwft",
		"jptl (61)",
		"ugml (68) -> gyxo, ebii, jptl",
		"gyxo (61)",
		"cntj (57)",
	}

	parsedRows := make([]ParsedRow, len(lines))
	for i, row := range lines {
		parsedRows[i] = parseRow(row)
	}

	tree := buildTree(parsedRows)

	actual := findCorrectWeight(tree)

	if actual != 60 {
		t.Error("Wrong result for findCorrectWeight.  Expected 60, Actual ", actual)
	}
}

func treeEqual(expected *Node, actual *Node) bool {
	if expected.name != actual.name || expected.weight != actual.weight || expected.childWeight != actual.childWeight {
		return false
	}

	if len(expected.children) != len(actual.children) {
		return false
	}

	for i, above := range expected.children {
		if !treeEqual(above, actual.children[i]) {
			return false
		}
	}

	return true
}

func TestReadLines(t *testing.T) {
	lines, err := readLines("testdata/lines.txt")

	if err != nil {
		t.Error("Error reading file", err)
	}

	if len(lines) != 13 {
		t.Error("Wrong number of lines")
	}

	if lines[0] != "pbga (66)" {
		t.Error("Wrong value for line 1")
	}

	if lines[12] != "cntj (57)" {
		t.Error("Wrong value for line 13")
	}
}
