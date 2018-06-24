package dev.jh.adventofcode;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day21 {
    private static final char[][] START = new char[][]{
        new char[]{'.', '#', '.'},
        new char[]{'.', '.', '#'},
        new char[]{'#', '#', '#'}
    };

  public static class Rule {
    private static final Pattern PATTERN = Pattern.compile("^([.#/]+) => ([.#/]+)$");

    public final char[][] pattern;
    public final char[][] replacement;

    public Rule(char[][] pattern, char[][] replacement) {
      this.pattern = pattern;
      this.replacement = replacement;
    }

    public boolean matches(GridSegment segment) {
      if (segment.size != pattern.length) {
        return false;
      }

      char[][] rotated = this.pattern;

      for (int i = 0; i < 4; i ++) {
        if (segment.equals(rotated)) {
          return true;
        }
        rotated = rotate(rotated);
      }

      rotated = flipHorizontal(rotated);
      for (int i = 0; i < 4; i ++) {
        if (segment.equals(rotated)) {
          return true;
        }
        rotated = rotate(rotated);
      }

      return false;
    }

    private static char[][] rotate(char[][] pattern) {
      int size = pattern.length;
      char[][] rotated = new char[size][size];

      for (int row = 0; row < size; row ++) {
        for (int column = 0; column < size; column++) {
          rotated[row][column] = pattern[size - column - 1][row];
        }
      }

      return rotated;
    }

    private static char[][] flipHorizontal(char[][] pattern) {
      int size = pattern.length;
      char[][] flipped = new char[size][size];

      for (int row = 0; row < size; row ++) {
        for (int column = 0; column < size; column++) {
          flipped[row][column] = pattern[row][size - column - 1];
        }
      }

      return flipped;
    }

    public static Rule fromString(String string) {
      Matcher matcher = PATTERN.matcher(string);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid pattern '" + string + "'");
      }

      return new Rule(
          gridFromString(matcher.group(1)),
          gridFromString(matcher.group(2))
      );
    }

    private static char[][] gridFromString(String string) {
      return Arrays.stream(string.split("/"))
          .map(String::toCharArray)
          .toArray(char[][]::new);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }

      if (!(o instanceof Rule)) {
        return false;
      }

      Rule rule = (Rule) o;
      return Arrays.deepEquals(pattern, rule.pattern) &&
          Arrays.deepEquals(replacement, rule.replacement);
    }

    @Override
    public int hashCode() {
      int result = Arrays.hashCode(pattern);
      result = 31 * result + Arrays.hashCode(replacement);
      return result;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("pattern", pattern)
          .add("replacement", replacement)
          .toString();
    }
  }

  public static class GridSegment {
    public final int row;
    public final int column;
    public final int size;
    public final char[][] grid;

    public GridSegment(int row, int column, int size, char[][] grid) {
      this.row = row;
      this.column = column;
      this.size = size;
      this.grid = grid;
    }

    public boolean equals(char[][] pattern) {
      for (int row = 0; row < size; row ++) {
        for (int column = 0; column < size; column ++) {
          if (grid[this.row + row][this.column + column] != pattern[row][column]) {
            return false;
          }
        }
      }

      return true;
    }

    public char[][] findReplacement(ImmutableList<Rule> rules) {
      for (Rule rule : rules) {
        if (rule.matches(this)) {
          return rule.replacement;
        }
      }

      throw new IllegalStateException("No replacement");
    }
  }

  public static int pixelsOn(ImmutableList<Rule> rules, int iterations) {
    char[][] grid = START;

    for (int iteration = 0; iteration < iterations; iteration ++) {
      int gridSize = grid.length;
      int divisionSize = gridSize % 2 == 0 ? 2 : 3;
      int replacementSize = divisionSize + 1;
      int newGridSize = (gridSize / divisionSize) * replacementSize;

      char[][] newGrid = new char[newGridSize][newGridSize];

      for (int row = 0; row < gridSize / divisionSize; row ++) {
        for (int column = 0; column < gridSize / divisionSize; column ++) {
          GridSegment segment = new GridSegment(row * divisionSize, column * divisionSize, divisionSize, grid);

          char[][] replacement = segment.findReplacement(rules);
          for (int r = 0; r < replacementSize; r ++) {
            for (int c = 0; c < replacementSize; c ++) {
              newGrid[row * replacementSize + r][column * replacementSize + c] = replacement[r][c];
            }
          }
        }
      }

      grid = newGrid;
    }

    int numOn = 0;
    for (int row = 0; row < grid.length; row ++) {
      for (int column = 0; column < grid.length; column ++) {
        if (grid[row][column] == '#') {
          numOn++;
        }
      }
    }

    return numOn;
  }

  public static void main(String[] args) throws IOException {
    File file = new File(Resources.getResource("day21.txt").getFile());
    ImmutableList<String> lines = ImmutableList.copyOf(Files.readLines(file, Charsets.UTF_8));
    ImmutableList<Rule> rules = lines.stream()
        .map(Rule::fromString)
        .collect(ImmutableList.toImmutableList());

    System.out.println("Part 1: " + pixelsOn(rules, 5));
  }
}
