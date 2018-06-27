package dev.jh.adventofcode;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day23 {

  public static class State {
    public final Registers registers;
    public final int programCounter;

    public State() {
      this(new Registers(ImmutableMap.of()), 0);
    }

    private State(Registers registers, int programCounter) {
      this.registers = registers;
      this.programCounter = programCounter;
    }

    public long getRegister(char register) {
      return registers.get(register);
    }

    public State setRegister(char register, long value) {
      return new State(registers.set(register, value), programCounter);
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

  public static class Registers {
    private final ImmutableMap<Character, Long> registers;

    public Registers(ImmutableMap<Character, Long> registers) {
      this.registers = registers;
    }

    public Registers set(char register, long value) {
      return new Registers(ImmutableMap.<Character, Long>builder()
              .putAll(registers.entrySet().stream()
                      .filter(entry -> entry.getKey() != register)
                      .collect(ImmutableList.toImmutableList()))
              .put(register, value)
              .build());
    }

    public long get(char register) {
      return registers.getOrDefault(register, 0L);
    }

    @Override
    public String toString() {
      StringBuilder bldr = new StringBuilder();

      for (char register = 'a'; register <= 'h'; register ++) {
        bldr.append("\n\t").append(register).append(": ").append(get(register));
      }

      return bldr.append('\n').toString();
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
    private final int line;
    private final char x;
    private final RegisterOrNumber y;

    public SetInstruction(int line, char x, RegisterOrNumber y) {
      this.line = line;
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
      return String.format("%2d - set %c %s", line, x, y);
    }
  }

  /**
   * sub X Y decreases X by the value of Y.
   */
  public static class SubInstruction implements Instruction {
    private final int line;
    private final char x;
    private final RegisterOrNumber y;

    public SubInstruction(int line, char x, RegisterOrNumber y) {
      this.line = line;
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
      return String.format("%2d - sub %c %s", line, x, y);
    }
  }

  /**
   * mul X Y sets register X to the result of multiplying the value contained in register X by the value of Y.
   */
  public static class MulInstruction implements Instruction {
    private final int line;
    private final char x;
    private final RegisterOrNumber y;

    public MulInstruction(int line, char x, RegisterOrNumber y) {
      this.line = line;
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
      return String.format("%2d - mul %c %s", line, x, y);
    }
  }

  /**
   * jnz X Y jumps with an offset of the value of Y, but only if the value of X is not zero.
   * (An offset of 2 skips the next instruction, an offset of -1 jumps to the previous instruction,
   * and so on.)
   */
  public static class JnzInstruction implements Instruction {
    private final int line;
    private final RegisterOrNumber x;
    private final RegisterOrNumber y;

    public JnzInstruction(int line, RegisterOrNumber x, RegisterOrNumber y) {
      this.line = line;
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
      return String.format("%2d - jnz %s %s", line, x, y);
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

  public static int runOptimized() {
    // The program counts the number of composite numbers in the range [108100,125100] inefficiently
    // This method computes the sieve of eranthoses up to 125100 to compute the answer much quicker.

    int[] sieve = new int[125101];
    for (int i = 0; i < sieve.length; i ++) {
      sieve[i] = i;
    }

    for (int i = 2; i < sieve.length; i ++) {
      if (sieve[i] == 0) {
        continue;
      }

      for (int factor = i * 2; factor < sieve.length; factor += i) {
        sieve[factor] = 0;
      }
    }

    ImmutableSet<Integer> primes = Arrays.stream(sieve)
            .filter(i -> i != 0)
            .boxed()
            .collect(ImmutableSet.toImmutableSet());

    int count = 0;
    for (int b = 108100; b <= 125100; b += 17) {
      if (!primes.contains(b)) {
        count ++;
      }
    }

    return count;
  }

  private static final Pattern INSTRUCTION_PATTERN = Pattern.compile("^([a-z]{3}) ([a-z]|(?:-?\\d+)) ?([a-z]|(?:-?\\d+))?$");

  private static Instruction parseInstruction(int line, String instruction) {
    Matcher matcher = INSTRUCTION_PATTERN.matcher(instruction);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid instruction " + instruction);
    }

    String command = matcher.group(1);
    switch(command) {
      case "set":
        return new SetInstruction(line, matcher.group(2).charAt(0), new RegisterOrNumber(matcher.group(3)));
      case "sub":
        return new SubInstruction(line, matcher.group(2).charAt(0), new RegisterOrNumber(matcher.group(3)));
      case "mul":
        return new MulInstruction(line, matcher.group(2).charAt(0), new RegisterOrNumber(matcher.group(3)));
      case "jnz":
        return new JnzInstruction(line, new RegisterOrNumber(matcher.group(2)), new RegisterOrNumber(matcher.group(3)));
      default:
        throw new IllegalArgumentException("Invalid instruction " + instruction);
    }
  }

  private static ImmutableList<Instruction> loadProgram(String name) throws IOException {
    File file = new File(Resources.getResource(name).getFile());
    ImmutableList<String> lines = ImmutableList.copyOf(Files.readLines(file, Charsets.UTF_8));

    ImmutableList.Builder<Instruction> instructions = ImmutableList.builder();
    for (int i = 0; i < lines.size(); i ++) {
      instructions.add(parseInstruction(i, lines.get(i)));
    }
    return instructions.build();
  }

  public static void main(String[] args) throws IOException {
    System.out.println("Part 1: " + runCountMul(loadProgram("day23.txt")));
    System.out.println("Part 2: " + runOptimized());
  }
}
