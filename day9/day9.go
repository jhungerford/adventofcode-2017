package main

import (
	"fmt"
	"io/ioutil"
	"os"
	"strings"
)

type State struct {
	score int
	level int
	escaped bool
}

type Handler func(r rune, state *State) Handler

func GroupHandler(r rune, state *State) Handler {
	if r == '{' {
		state.level ++
		return GroupHandler
	}

	if r == '<' {
		return GarbageHandler
	}

	if r == '}' {
		state.score += state.level
		state.level --
		return GroupHandler
	}

	if  r == ',' {
		return GroupHandler
	}

	panic("Invalid rune " + string(r) + "in GroupHandler")
}

func GarbageHandler(r rune, state *State) Handler {
	if state.escaped {
		state.escaped = false
		return GarbageHandler
	}

	if r == '!' {
		state.escaped = true
		return GarbageHandler
	}

	if r == '>' {
		return GroupHandler
	}

	return GarbageHandler
}

func score(input string) int {
	state := &State{0, 0, false}
	handler := GroupHandler

	for _, r := range input {
		handler = handler(r, state)
	}

	return state.score
}

func main() {
	if len(os.Args) != 2 {
		fmt.Println("Usage: ", os.Args[0], " <input file>")
		return
	}

	bytes, err := ioutil.ReadFile(os.Args[1])
	if err != nil {
		fmt.Println("Error reading", os.Args[1], err)
		return
	}

	input := strings.TrimSpace(string(bytes))

	fmt.Println("Part 1: ", score(string(input)))
}
