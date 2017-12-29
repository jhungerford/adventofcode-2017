package main

import "fmt"

const (
	AFactor = uint(16807)
	BFactor = uint(48271)
)

type Generator struct {
	Value, factor uint
}

// Advances the generator's value, modifying the generator in the process
func (g *Generator) NextValue() uint {
	newValue := g.Value * g.factor % 2147483647
	g.Value = newValue

	return newValue
}

// Returns whether the lowest 16 bits of each generator value match
func Judge(a, b *Generator) bool {
	return (a.Value & uint(0xFFFF)) == (b.Value & uint(0xFFFF))
}

// Runs 40M iterations of each generator, and returns the number of pairs that the judge thinks match
func totalCount(aInit, bInit uint) int {
	a, b := &Generator{aInit, AFactor}, &Generator{bInit, BFactor}

	count := 0
	for i := 0; i < 40000000; i ++ {
		a.NextValue()
		b.NextValue()

		if Judge(a, b) {
			count ++
		}
	}

	return count
}

func main() {
	fmt.Println("Part 1: ", totalCount(591, 393))

}
