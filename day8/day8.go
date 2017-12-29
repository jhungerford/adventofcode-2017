package main

import (
	"regexp"
	"strconv"
	"fmt"
	"math"
	"os"
	"../util"
)

var Increment = 1
var Decrement = -1

type Operator func(a, b int) bool
var Operators = makeOperators()

func makeOperators() map[string]Operator {
	m := make(map[string]Operator, 6)

	m["!="] = func(a, b int) bool { return a != b }
	m["<"] = func (a, b int) bool { return a < b }
	m["<="] = func (a, b int) bool { return a <= b }
	m["=="] = func (a, b int) bool { return a == b }
	m[">"] = func (a, b int) bool { return a > b }
	m[">="] = func (a, b int) bool { return a >= b }

	return m
}

type Instruction struct {
	register          string
	direction         int
	amount            int
	conditionRegister string
	conditionOperator Operator
	conditionValue    int
}

// b inc 5 if a > 1
var InstructionRegexp = regexp.MustCompile("([a-z]+) ((?:inc)|(?:dec)) (-?\\d+) if ([a-z]+) ([<>=!]+) (-?\\d+)")

// Parses a single instruction, panicking if the instruction is invalid
func parseInstruction(line string) Instruction {
	groups := InstructionRegexp.FindStringSubmatch(line)

	if groups == nil {
		panic("Invalid instruction " + line)
	}

	direction := Increment
	if groups[2] == "dec" {
		direction = Decrement
	}

	amount, err := strconv.Atoi(groups[3])
	if err != nil {
		panic(err)
	}

	conditionValue, conditionErr := strconv.Atoi(groups[6])
	if conditionErr != nil {
		panic(err)
	}

	return Instruction{
		groups[1],
		direction,
		amount,
		groups[4],
		Operators[groups[5]],
		conditionValue,
	}
}

type CPU struct {
	registers map[string]int
	maxValue int
}

// Creates a new CPU with all registers set to 0
func NewCPU() *CPU {
	return &CPU{make(map[string]int), 0}
}

// Applies the instruction, potentially modifying the values of the CPU registers
func applyInstruction(instruction Instruction, cpu *CPU) {
	if instruction.conditionOperator(cpu.registers[instruction.conditionRegister], instruction.conditionValue) {
		newValue := cpu.registers[instruction.register] + instruction.direction * instruction.amount

		cpu.registers[instruction.register] = newValue

		if newValue > cpu.maxValue {
			cpu.maxValue = newValue
		}
	}
}

// Returns the largest value in any register
func largestRegisterValue(cpu *CPU) int {
	max := math.MinInt64
	for _, value := range cpu.registers {
		if value > max {
			max = value
		}
	}

	return max
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

	cpu := NewCPU()

	for _, line := range lines {
		applyInstruction(parseInstruction(line), cpu)
	}

	fmt.Println("Part 1:", largestRegisterValue(cpu))
	fmt.Println("Part 2:", cpu.maxValue)
}
