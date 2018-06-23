package dev.jh.adventofcode;

import static dev.jh.adventofcode.Day19.Direction.DOWN;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

public class Day19Test {

  public static final ImmutableList<String> EXAMPLE =ImmutableList.of(
      "     |          ",
      "     |  +--+    ",
      "     A  |  C    ",
      " F---|----E|--+ ",
      "     |  |  |  D ",
      "     +B-+  +--+ "
  );

  @Test
  public void example() {
    assertThat(Day19.letters(EXAMPLE)).isEqualTo("ABCDEF");
  }

  @Test
  public void findStart() {
    Day19.State expected = new Day19.State(new Day19.Position(0, 5), DOWN);

    assertThat(Day19.findStart(Day19.parse(EXAMPLE))).isEqualTo(expected);
  }
}
