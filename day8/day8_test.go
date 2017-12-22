package main

import "testing"

type ParseInstructionTestCase struct {
	input string
	expected Instruction
}

func TestParseInstruction(t *testing.T) {
	tests := []ParseInstructionTestCase{
		{"b inc 5 if a > 1", Instruction{"b", Increment, 5, "a", Operators[">"], 1}},
		{"a inc 1 if b < 5", Instruction{"a", Increment, 1, "b", Operators["<"], 5}},
		{"c dec -10 if a >= 1", Instruction{"c", Decrement, -10, "a", Operators[">="], 1}},
		{"c inc -20 if c == 10", Instruction{"c", Increment, -20, "c", Operators["=="], 10}},
	}

	for _, test := range tests {
		actual := parseInstruction(test.input)
		if !instructionsEqual(actual, test.expected) {
			t.Error("Wrong instruction for input", test.input, " Actual:", actual, "Expected:", test.expected)
		}
	}
}

func instructionsEqual(a, b Instruction) bool {
	return a.register == b.register &&
		a.direction == b.direction &&
		a.amount == b.amount &&
		a.conditionRegister == b.conditionRegister &&
		a.conditionOperator(0, 0) == b.conditionOperator(0, 0) &&
		a.conditionOperator(-1, 0) == b.conditionOperator(-1, 0) &&
		a.conditionValue == b.conditionValue
}

func TestApplyInstruction(t *testing.T) {
	lines := []string {
		"b inc 5 if a > 1",
		"a inc 1 if b < 5",
		"c dec -10 if a >= 1",
		"c inc -20 if c == 10",
	}

	cpu := NewCPU()

	for _, line := range lines {
		applyInstruction(parseInstruction(line), cpu)
	}

	checkRegister(t, cpu, "a", 1)
	checkRegister(t, cpu, "b", 0)
	checkRegister(t, cpu, "c", -10)
}

func checkRegister(t *testing.T, cpu *CPU, register string, expected int) {
	actual := cpu.registers[register]
	if actual != expected {
		t.Error("Wrong value for register", register, "Expected:", expected, "Actual:", actual)
	}
}

func TestLargestRegisterValue(t *testing.T) {
	cpu := NewCPU()
	cpu.registers["a"] = 1
	cpu.registers["c"] = -10

	actual := largestRegisterValue(cpu)
	if actual != 1 {
		t.Error("Wrong largest register value.  Expected 1, got", actual)
	}
}
