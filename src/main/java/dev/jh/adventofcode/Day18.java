package dev.jh.adventofcode;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day18 {

  public static class State {
    /** Registers - map of character to value.  Registers start with the value 0. */
    public final ImmutableMap<Character, Long> registers;
    /** Frequency of the last sound played */
    public final long playedFrequency;
    /** Frequency of the last sound recovered */
    public final long recoveredFrequency;
    /** Program counter */
    public final long programCounter;

    /**
     * Constructs a new state, with all registers initialized to 0, no sound played,
     * and the program counter at the beginning of the instructions.
     */
    public State() {
      this.registers = ImmutableMap.of();
      this.playedFrequency = 0;
      this.recoveredFrequency = 0;
      this.programCounter = 0;
    }

    private State(
        ImmutableMap<Character, Long> registers,
        long playedFrequency,
        long recoveredFrequency,
        long programCounter
    ) {
      this.registers = registers;
      this.playedFrequency = playedFrequency;
      this.recoveredFrequency = recoveredFrequency;
      this.programCounter = programCounter;
    }

    /**
     * Returns a new state with the given register set to the given value.
     *
     * @param register Register to set
     * @param value Value to set the register to
     * @return New state with the register set to the given value
     */
    public State setRegister(char register, long value) {
      return new State(
          ImmutableMap.<Character, Long>builder()
              .putAll(registers.entrySet().stream()
                  .filter(entry -> entry.getKey() != register)
                  .collect(ImmutableList.toImmutableList()))
              .put(register, value)
              .build(),
          playedFrequency,
          recoveredFrequency,
          programCounter
      );
    }

    /**
     * Returns the value of the given register, or 0 if the register isn't set.
     *
     * @param register Register to look up
     * @return Value of the register
     */
    public long getRegister(char register) {
      return registers.getOrDefault(register, 0L);
    }

    /**
     * Returns a new State that's playing the given sound.
     *
     * @param value Sound to play
     * @return New state playing the sound
     */
    public State playSound(long value) {
      return new State(registers, value, recoveredFrequency, programCounter);
    }

    /**
     * Returns a new state that recovers the last played frequency.
     *
     * @return New state with the last played frequency recovered
     */
    public State recoverFrequency() {
      return new State(
          registers,
          playedFrequency,
          playedFrequency,
          programCounter
      );
    }

    /**
     * Returns a new state with the program counter incremented by 1.
     *
     * @return New state with the program counter incremented.
     */
    public State incrementProgramCounter() {
      return setProgramCounter(programCounter + 1);
    }

    /**
     * Returns a new state with the program counter set to the given value.
     *
     * @param value Value to set the program counter to
     * @return New state with the program counter set to the given value.
     */
    public State setProgramCounter(long value) {
      return new State(
          registers,
          playedFrequency,
          recoveredFrequency,
          value
      );
    }
  }

  /**
   * Many instructions take a register or number argument.
   */
  public static class RegisterOrNumber {
    private final boolean isRegister;
    private final char register;
    private final int number;

    /**
     * Constructs a new RegisterOrNumber by parsing the given value.
     *
     * @param value String representing a register or a number, like a or 1.
     */
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

    /**
     * Returns the value of this RegisterOrNumber, looking up the value in the state if this is
     * a register.
     *
     * @param state State to look up a value in
     * @return Value of this RegisterOrNumber
     */
    public long get(State state) {
      if (isRegister) {
        return state.getRegister(register);
      }

      return number;
    }
  }

  public interface Instruction {
    /**
     * Applies this instruction to the given state, returning a new state with the result.
     *
     * @param state State to apply this instruction to.
     * @return New state after applying this instruction.
     */
    State apply(State state);
  }

  /**
   * snd X plays a sound with a frequency equal to the value of X.
   */
  public static class SndInstruction implements Instruction {
    private final RegisterOrNumber x;

    public SndInstruction(RegisterOrNumber x) {
      this.x = x;
    }

    @Override
    public State apply(State state) {
      return state
          .playSound(x.get(state))
          .incrementProgramCounter();
    }
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
  }

  /**
   * add X Y increases register X by the value of Y.
   */
  public static class AddInstruction implements Instruction {
    private final char x;
    private final RegisterOrNumber y;

    public AddInstruction(char x, RegisterOrNumber y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public State apply(State state) {
      return state
          .setRegister(x, state.getRegister(x) + y.get(state))
          .incrementProgramCounter();
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
  }

  /**
   * mod X Y sets register X to the remainder of dividing the value contained in register X by the value of Y
   * (that is, it sets X to the result of X modulo Y).
   */
  public static class ModInstruction implements Instruction {
    private final char x;
    private final RegisterOrNumber y;

    public ModInstruction(char x, RegisterOrNumber y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public State apply(State state) {
      long value = y.get(state);
      if (value == 0) {
        return state.incrementProgramCounter();
      }

      return state
          .setRegister(x, state.getRegister(x) % value)
          .incrementProgramCounter();
    }
  }

  /**
   * rcv X recovers the frequency of the last sound played, but only when the value of X is not zero.
   * (If it is zero, the command does nothing.)
   */
  public static class RcvInstruction implements Instruction {
    private final char x;

    public RcvInstruction(char x) {
      this.x = x;
    }

    @Override
    public State apply(State state) {
      if (state.getRegister(x) != 0) {
        return state
            .recoverFrequency()
            .incrementProgramCounter();
      }

      return state
          .incrementProgramCounter();
    }
  }

  /**
   * jgz X Y jumps with an offset of the value of Y, but only if the value of X is greater than zero.
   * (An offset of 2 skips the next instruction, an offset of -1 jumps to the previous instruction,
   * and so on.)
   */
  public static class JgzInstruction implements Instruction {
    private final char x;
    private final RegisterOrNumber y;

    public JgzInstruction(char x, RegisterOrNumber y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public State apply(State state) {
      if (state.getRegister(x) > 0) {
        return state.setProgramCounter(state.programCounter + y.get(state));
      }

      return state.incrementProgramCounter();
    }
  }

  /**
   * Runs the given program, returning the state of the machine after executing all of the instructions.
   *
   * @param program Program to run
   * @return State of the machine after running the program
   */
  public static State run(ImmutableList<String> program, Predicate<State> runPredicate) {
    State state = new State();
    ImmutableList<Instruction> instructions = program.stream()
        .map(Day18::parseInstruction)
        .collect(ImmutableList.toImmutableList());

    while (state.programCounter >= 0 && state.programCounter < instructions.size() && runPredicate.test(state)) {
      state = instructions.get((int) state.programCounter).apply(state);
    }

    return state;
  }

  private static final Pattern INSTRUCTION_PATTERN = Pattern.compile("^([a-z]{3}) ([a-z]|(?:-?\\d+)) ?([a-z]|(?:-?\\d+))?$");

  /**
   * Parses the given string instruction.
   *
   * @param instruction Instruction to parse
   * @return Parsed instruction
   */
  private static Instruction parseInstruction(String instruction) {
    Matcher matcher = INSTRUCTION_PATTERN.matcher(instruction);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid instruction " + instruction);
    }

    String command = matcher.group(1);
    switch(command) {
      case "snd":
        return new SndInstruction(new RegisterOrNumber(matcher.group(2)));
      case "set":
        return new SetInstruction(matcher.group(2).charAt(0), new RegisterOrNumber(matcher.group(3)));
      case "add":
        return new AddInstruction(matcher.group(2).charAt(0), new RegisterOrNumber(matcher.group(3)));
      case "mul":
        return new MulInstruction(matcher.group(2).charAt(0), new RegisterOrNumber(matcher.group(3)));
      case "mod":
        return new ModInstruction(matcher.group(2).charAt(0), new RegisterOrNumber(matcher.group(3)));
      case "rcv":
        return new RcvInstruction(matcher.group(2).charAt(0));
      case "jgz":
        return new JgzInstruction(matcher.group(2).charAt(0), new RegisterOrNumber(matcher.group(3)));
      default:
        throw new IllegalArgumentException("Invalid instruction " + instruction);
    }
  }

  public static void main(String[] args) throws IOException {
    File file = new File(Resources.getResource("day18.txt").getFile());
    ImmutableList<String> program = ImmutableList.copyOf(Files.readLines(file, Charsets.UTF_8));

    Day18.State part1State = Day18.run(program, state -> state.recoveredFrequency == 0);
    System.out.println("Part 1: " + part1State.recoveredFrequency);
  }
}
