// Package util contains utility functions that many days use.
package util

import (
	"os"
	"bufio"
	"container/list"
	"strings"
)

// Reads newline separated lines from the file with the given name
func ReadLines(fileName string) ([]string, error) {
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
