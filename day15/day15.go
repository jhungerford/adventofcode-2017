package main

import "fmt"

const (
	AFactor = uint(16807)
	BFactor = uint(48271)
)

type Generator struct {
	Value, factor, multiple uint
}

// Advances the generator's value, modifying the generator in the process
func (g *Generator) NextValue() uint {
	newValue := g.Value * g.factor % 2147483647
	for ; newValue % g.multiple != 0; newValue = g.Value * g.factor % 2147483647 {
		g.Value = newValue
	}

	g.Value = newValue
	return newValue
}

// Returns whether the lowest 16 bits of each generator value match
func Judge(a, b *Generator) bool {
	return (a.Value & uint(0xFFFF)) == (b.Value & uint(0xFFFF))
}

// Runs 40M iterations of each generator, and returns the number of pairs that the judge thinks match
func totalCount(aInit, bInit uint) int {
	a, b := &Generator{aInit, AFactor, 4}, &Generator{bInit, BFactor, 8}

	count := 0
	for i := 0; i < 5000000; i ++ {
		a.NextValue()
		b.NextValue()

		if Judge(a, b) {
			count ++
		}
	}

	return count
}

func main() {
	fmt.Println("Part 2: ", totalCount(591, 393))

}
