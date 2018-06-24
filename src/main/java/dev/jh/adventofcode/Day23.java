package dev.jh.adventofcode;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day23 {

  public static class State {
    public final ImmutableMap<Character, Long> registers;
    public final int programCounter;

    public State() {
      this(ImmutableMap.of(), 0);
    }

    private State(ImmutableMap<Character, Long> registers, int programCounter) {
      this.registers = registers;
      this.programCounter = programCounter;
    }

    public State setRegister(char register, long value) {
      return new State(
          ImmutableMap.<Character, Long>builder()
              .putAll(registers.entrySet().stream()
                  .filter(entry -> entry.getKey() != register)
                  .collect(ImmutableList.toImmutableList()))
              .put(register, value)
              .build(),
          programCounter
      );
    }

    public long getRegister(char register) {
      return registers.getOrDefault(register, 0L);
    }

    public State incrementProgramCounter() {
      return setProgramCounter(programCounter + 1);
    }

    public State setProgramCounter(int value) {
      return new State(registers, value);
    }

    public boolean isRunning(ImmutableList<Instruction> instructions) {
      return programCounter >= 0 && programCounter < instructions.size();
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("registers", registers)
          .add("programCounter", programCounter)
          .toString();
    }
  }

  public static class RegisterOrNumber {
    private final boolean isRegister;
    private final char register;
    private final int number;

    public RegisterOrNumber(String value) {
      boolean isRegister;
      char register = 0;
      int number = 0;

      try {
        number = Integer.parseInt(value);
        isRegister = false;
      } catch (NumberFormatException ex) {
        register = value.charAt(0);
        isRegister = true;
      }

      this.isRegister = isRegister;
      this.register = register;
      this.number = number;
    }

    public long get(State state) {
      if (isRegister) {
        return state.getRegister(register);
      }

      return number;
    }

    @Override
    public String toString() {
      return isRegister ? Character.toString(register) : Integer.toString(number);
    }
  }

  public interface Instruction {
    State apply(State state);
  }

  /**
   * set X Y sets register X to the value of Y.
   */
  public static class SetInstruction implements Instruction {
    private final char x;
    private final RegisterOrNumber y;

    public SetInstruction(char x, RegisterOrNumber y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public State apply(State state) {
      return state
          .setRegister(x, y.get(state))
          .incrementProgramCounter();
    }

    @Override
    public String toString() {
      return "set " + x + ' ' + y;
    }
  }

  /**
   * sub X Y decreases X by the value of Y.
   */
  public static class SubInstruction implements Instruction {
    private final char x;
    private final RegisterOrNumber y;

    public SubInstruction(char x, RegisterOrNumber y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public State apply(State state) {
      return state
          .setRegister(x, state.getRegister(x) - y.get(state))
          .incrementProgramCounter();
    }

    @Override
    public String toString() {
      return "sub " + x + ' ' + y;
    }
  }

  /**
   * mul X Y sets register X to the result of multiplying the value contained in register X by the value of Y.
   */
  public static class MulInstruction implements Instruction {
    private final char x;
    private final RegisterOrNumber y;

    public MulInstruction(char x, RegisterOrNumber y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public State apply(State state) {
      return state
          .setRegister(x, state.getRegister(x) * y.get(state))
          .incrementProgramCounter();
    }

    @Override
    public String toString() {
      return "mul " + x + ' ' + y;
    }
  }

  /**
   * jnz X Y jumps with an offset of the value of Y, but only if the value of X is not zero.
   * (An offset of 2 skips the next instruction, an offset of -1 jumps to the previous instruction,
   * and so on.)
   */
  public static class JnzInstruction implements Instruction {
    private final RegisterOrNumber x;
    private final RegisterOrNumber y;

    public JnzInstruction(RegisterOrNumber x, RegisterOrNumber y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public State apply(State state) {
      if (x.get(state) != 0) {
        return state.setProgramCounter((int) (state.programCounter + y.get(state)));
      }

      return state.incrementProgramCounter();
    }

    @Override
    public String toString() {
      return "jnz " + x + ' ' + y;
    }
  }

  public static int runCountMul(ImmutableList<Instruction> program) {
    State state = new State();

    int numMul = 0;
    while (state.isRunning(program)) {
      Instruction instruction = program.get(state.programCounter);
      if (instruction instanceof MulInstruction) {
        numMul++;
      }

      state = instruction.apply(state);
    }

    return numMul;
  }

  private static final Pattern INSTRUCTION_PATTERN = Pattern.compile("^([a-z]{3}) ([a-z]|(?:-?\\d+)) ?([a-z]|(?:-?\\d+))?$");

  private static Instruction parseInstruction(String instruction) {
    Matcher matcher = INSTRUCTION_PATTERN.matcher(instruction);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid instruction " + instruction);
    }

    String command = matcher.group(1);
    switch(command) {
      case "set":
        return new SetInstruction(matcher.group(2).charAt(0), new RegisterOrNumber(matcher.group(3)));
      case "sub":
        return new SubInstruction(matcher.group(2).charAt(0), new RegisterOrNumber(matcher.group(3)));
      case "mul":
        return new MulInstruction(matcher.group(2).charAt(0), new RegisterOrNumber(matcher.group(3)));
      case "jnz":
        return new JnzInstruction(new RegisterOrNumber(matcher.group(2)), new RegisterOrNumber(matcher.group(3)));
      default:
        throw new IllegalArgumentException("Invalid instruction " + instruction);
    }
  }

  public static void main(String[] args) throws IOException {
    File file = new File(Resources.getResource("day23.txt").getFile());
    ImmutableList<Instruction> program = ImmutableList.copyOf(Files.readLines(file, Charsets.UTF_8)).stream()
        .map(Day23::parseInstruction)
        .collect(ImmutableList.toImmutableList());

    System.out.println("Part 1: " + runCountMul(program));
  }
}
