package dev.jh.adventofcode;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class Day25Test {

  @Test
  public void example() {
    Day25.Machine initial = new Day25.Machine(new Day25.Tape(), 0, 'a');

    ImmutableMap<Day25.StateValue, Function<Day25.Machine, Day25.Machine>> instructions = ImmutableMap.of(
        new Day25.StateValue('a', false), machine -> new Day25.Machine(
            machine.tape.write(machine.cursor, true),
            machine.cursor + 1,
            'b'
        ),
        new Day25.StateValue('a', true), machine -> new Day25.Machine(
            machine.tape.write(machine.cursor, false),
            machine.cursor - 1,
            'b'
        ),
        new Day25.StateValue('b', false), machine -> new Day25.Machine(
            machine.tape.write(machine.cursor, true),
            machine.cursor - 1,
            'a'
        ),
        new Day25.StateValue('b', true), machine -> new Day25.Machine(
            machine.tape.write(machine.cursor, true),
            machine.cursor + 1,
            'a'
        )
    );

    Day25.Machine machine = Day25.runSteps(6, initial, instructions);
    assertThat(machine.tape.checksum()).isEqualTo(3);
    assertThat(machine.tape).isEqualTo(new Day25.Tape()
        .write(-2, true)
        .write(-1, true)
        .write(1, true)
    );
  }

  @Test
  public void tapeGet() {
    Day25.Tape tape = new Day25.Tape()
        .write(-2, true)
        .write(-2, false)
        .write(0, false)
        .write(1, true);

    assertThat(tape.get(-2)).isFalse();
    assertThat(tape.get(0)).isFalse();
    assertThat(tape.get(1)).isTrue();
  }

  @Test
  public void tapeChecksum() {
    assertThat(new Day25.Tape().checksum()).isEqualTo(0);

    Day25.Tape tape = new Day25.Tape()
        .write(-2, true)
        .write(-1, true)
        .write(0, false)
        .write(1, true);

    assertThat(tape.checksum()).isEqualTo(3);
  }

  @Test
  public void tapeEquals() {
    Day25.Tape onlyOn = new Day25.Tape()
        .write(-2, true)
        .write(-1, true)
        .write(1, true);

    Day25.Tape full = new Day25.Tape()
        .write(-3, true)
        .write(-3, false)
        .write(-2, true)
        .write(-1, true)
        .write(0, false)
        .write(1, true)
        .write(2, false);

    assertThat(onlyOn).isEqualTo(full);
  }
}