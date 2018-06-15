package dev.jh.adventofcode;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class Day17Test {

  private static final Day17.Buffer ZERO = new Day17.Buffer();
  private static final Day17.Buffer FOUR = new Day17.Buffer(1, new int[]{0, 2, 3, 1});

  @Test
  public void example() {
    Day17.Buffer actualBuffer = Day17.insertTimes(3, 9);

    assertThat(actualBuffer).isEqualTo(new Day17.Buffer(1, new int[]{0, 9, 5, 7, 2, 4, 3, 8, 6, 1}));
  }

  @Test
  public void stepZeroElements() {
    Day17.Buffer result = ZERO.step(3);

    assertThat(result).isEqualTo(ZERO); // Unchanged with only one element.
  }

  @Test
  public void stepFourElements() {
    Day17.Buffer result = FOUR.step(3);

    assertThat(result).isEqualTo(new Day17.Buffer(0, FOUR.values));
  }

  @Test
  public void insertZeroElements() {
    Day17.Buffer result = ZERO.insertAfter(1);

    assertThat(result).isEqualTo(new Day17.Buffer(1, new int[]{0, 1}));
  }

  @Test
  public void insertFourElements() {
    Day17.Buffer result = FOUR.insertAfter(4);

    assertThat(result).isEqualTo(new Day17.Buffer(2, new int[]{0, 2, 4, 3, 1}));
  }

  @Test
  public void insertAtStart() {
    Day17.Buffer buffer = new Day17.Buffer(0, new int[]{0, 1, 2});
    Day17.Buffer result = buffer.insertAfter(3);

    assertThat(result).isEqualTo(new Day17.Buffer(1, new int[]{0, 3, 1, 2}));
  }

  @Test
  public void insertAtEnd() {
    Day17.Buffer buffer = new Day17.Buffer(2, new int[]{0, 1, 2});
    Day17.Buffer result = buffer.insertAfter(3);

    assertThat(result).isEqualTo(new Day17.Buffer(3, new int[]{0, 1, 2, 3}));
  }

  @Test
  public void positionOfNotFound() {
    assertThat(FOUR.positionOf(4)).isEqualTo(-1);
  }

  @Test
  public void positionOfFoun() {
    assertThat(FOUR.positionOf(3)).isEqualTo(2);
  }
}
