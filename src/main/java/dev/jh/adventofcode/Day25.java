package dev.jh.adventofcode;

import com.google.common.collect.ImmutableMap;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class Day25 {

  public static class Machine {
    public final Tape tape;
    public final int cursor;
    public final char state;

    public Machine(Tape tape, int cursor, char state) {
      this.tape = tape;
      this.cursor = cursor;
      this.state = state;
    }
  }

  public static class Tape {
    private final Set<Integer> on = new HashSet<>();

    public boolean get(int cursor) {
      return on.contains(cursor);
    }

    public Tape write(int cursor, boolean value) {
      if (value) {
        on.add(cursor);
      } else {
        on.remove(cursor);
      }

      return this;
    }

    public int checksum() {
      return on.size();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Tape tape = (Tape) o;
      return Objects.equals(on, tape.on);
    }

    @Override
    public int hashCode() {
      return Objects.hash(on);
    }
  }

  public static class StateValue {
    public final char state;
    public final boolean value;

    public StateValue(char state, boolean value) {
      this.state = state;
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      StateValue that = (StateValue) o;
      return state == that.state &&
          value == that.value;
    }

    @Override
    public int hashCode() {
      return Objects.hash(state, value);
    }
  }

  public static Machine runSteps(
      int steps,
      Machine initial,
      ImmutableMap<StateValue, Function<Machine, Machine>> instructions
  ) {
    Machine machine = initial;
    for (int i = 0; i < steps; i ++) {
      StateValue stateValue = new StateValue(machine.state, machine.tape.get(machine.cursor));

      machine = instructions.get(stateValue).apply(machine);
    }

    return machine;
  }

  public static void main(String[] args) {
    Machine initial = new Machine(new Tape(), 0, 'a');

    ImmutableMap<StateValue, Function<Machine, Machine>> instructions =
        ImmutableMap.<StateValue, Function<Machine, Machine>>builder()
            .put(new StateValue('a', false), machine -> new Machine(
                machine.tape.write(machine.cursor, true),
                machine.cursor + 1,
                'b'
            ))
            .put(new StateValue('a', true), machine -> new Machine(
                machine.tape.write(machine.cursor, false),
                machine.cursor + 1,
                'f'
            ))
            .put(new StateValue('b', false), machine -> new Machine(
                machine.tape.write(machine.cursor, false),
                machine.cursor - 1,
                'b'
            ))
            .put(new StateValue('b', true), machine -> new Machine(
                machine.tape.write(machine.cursor, true),
                machine.cursor - 1,
                'c'
            ))
            .put(new StateValue('c', false), machine -> new Machine(
                machine.tape.write(machine.cursor, true),
                machine.cursor - 1,
                'd'
            ))
            .put(new StateValue('c', true), machine -> new Machine(
                machine.tape.write(machine.cursor, false),
                machine.cursor + 1,
                'c'
            ))
            .put(new StateValue('d', false), machine -> new Machine(
                machine.tape.write(machine.cursor, true),
                machine.cursor - 1,
                'e'
            ))
            .put(new StateValue('d', true), machine -> new Machine(
                machine.tape.write(machine.cursor, true),
                machine.cursor + 1,
                'a'
            ))
            .put(new StateValue('e', false), machine -> new Machine(
                machine.tape.write(machine.cursor, true),
                machine.cursor - 1,
                'f'
            ))
            .put(new StateValue('e', true), machine -> new Machine(
                machine.tape.write(machine.cursor, false),
                machine.cursor - 1,
                'd'
            ))
            .put(new StateValue('f', false), machine -> new Machine(
                machine.tape.write(machine.cursor, true),
                machine.cursor + 1,
                'a'
            ))
            .put(new StateValue('f', true), machine -> new Machine(
                machine.tape.write(machine.cursor, false),
                machine.cursor - 1,
                'e'
            ))
            .build();

    System.out.println("Part 1: " + runSteps(12425180, initial, instructions).tape.checksum());
  }
}
