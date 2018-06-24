package dev.jh.adventofcode;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

public class Day21Test {

  @Test
  public void ruleFromString() {
    assertThat(Day21.Rule.fromString("../.# => ##./#../...")).isEqualTo(new Day21.Rule(
        new char[][]{
            new char[]{'.', '.'},
            new char[]{'.', '#'}
        },
        new char[][]{
            new char[]{'#', '#', '.'},
            new char[]{'#', '.', '.'},
            new char[]{'.', '.', '.'},
        }
    ));
  }

  @Test
  public void ruleMatches() {
    Day21.Rule rule = Day21.Rule.fromString(".#./..#/### => .#./..#/###");

    assertThat(rule.matches(new Day21.GridSegment(
        0, 0, 3, new char[][]{
        new char[]{'.', '#', '.'},
        new char[]{'.', '.', '#'},
        new char[]{'#', '#', '#'}
    }))).isTrue();

    assertThat(rule.matches(new Day21.GridSegment(
        0, 0, 3, new char[][]{
        new char[]{'.', '#', '.'},
        new char[]{'#', '.', '.'},
        new char[]{'#', '#', '#'}
    }))).isTrue();

    assertThat(rule.matches(new Day21.GridSegment(
        0, 0, 3, new char[][]{
        new char[]{'#', '.', '.'},
        new char[]{'#', '.', '#'},
        new char[]{'#', '#', '.'}
    }))).isTrue();

    assertThat(rule.matches(new Day21.GridSegment(
        0, 0, 3, new char[][]{
        new char[]{'#', '#', '#'},
        new char[]{'.', '.', '#'},
        new char[]{'.', '#', '.'}
    }))).isTrue();

    assertThat(rule.matches(new Day21.GridSegment(
        0, 0, 3, new char[][]{
        new char[]{'#', '#', '#'},
        new char[]{'.', '.', '#'},
        new char[]{'.', '.', '.'}
    }))).isFalse();

    assertThat(rule.matches(new Day21.GridSegment(
        0, 0, 2, new char[][]{
        new char[]{'#', '#'},
        new char[]{'.', '.'}
    }))).isFalse();
  }

  @Test
  public void example() {
    ImmutableList<Day21.Rule> rules = ImmutableList.of(
        Day21.Rule.fromString("../.# => ##./#../..."),
        Day21.Rule.fromString(".#./..#/### => #..#/..../..../#..#")
    );

    assertThat(Day21.pixelsOn(rules, 2)).isEqualTo(12);
  }
}
