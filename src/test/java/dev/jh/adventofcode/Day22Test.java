package dev.jh.adventofcode;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

public class Day22Test {

  @Test
  public void gridFromLines() {
    ImmutableList<String> lines = ImmutableList.of(
        "..#",
        "#..",
        "..."
    );

    assertThat(Day22.Grid.fromLines(lines).infectedNodes).containsExactly(
        new Day22.Node(-1, 1),
        new Day22.Node(0, -1)
    );
  }

  @Test
  public void step() {
    ImmutableList<String> lines = ImmutableList.of(
        "..#",
        "#..",
        "..."
    );

    Day22.State step = new Day22.State(Day22.Grid.fromLines(lines), Day22.Virus.INITIAL).step();

    assertThat(step.grid.infectedNodes).containsExactly(
        new Day22.Node(-1, 1),
        new Day22.Node(0, -1),
        new Day22.Node(0, 0)
    );

    assertThat(step.virus).isEqualTo(new Day22.Virus(new Day22.Node(0, -1), Day22.Direction.LEFT));
  }

  @Test
  public void example() {
    ImmutableList<String> lines = ImmutableList.of(
        "..#",
        "#..",
        "..."
    );

    assertThat(Day22.countInfections(Day22.Grid.fromLines(lines), 10000)).isEqualTo(5587);
  }

  @Test
  public void evolvedExample100() {
    ImmutableList<String> lines = ImmutableList.of(
            "..#",
            "#..",
            "..."
    );

    assertThat(Day22.countEvolvedInfections(Day22.Grid.fromLines(lines), 100)).isEqualTo(26);
  }

  @Test
  public void evolvedExample10000000() {
    ImmutableList<String> lines = ImmutableList.of(
            "..#",
            "#..",
            "..."
    );

    assertThat(Day22.countEvolvedInfections(Day22.Grid.fromLines(lines), 10000000)).isEqualTo(2511944);
  }
}
