package dev.jh.adventofcode;

import static dev.jh.adventofcode.Day19.Direction.DONE;
import static dev.jh.adventofcode.Day19.Direction.DOWN;
import static dev.jh.adventofcode.Day19.Direction.LEFT;
import static dev.jh.adventofcode.Day19.Direction.RIGHT;
import static dev.jh.adventofcode.Day19.Direction.UP;

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
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Day19 {

  private static ImmutableMap<Direction, Direction> OPPOSITE_DIRECTIONS = ImmutableMap.of(
      UP, DOWN,
      DOWN, UP,
      LEFT, RIGHT,
      RIGHT, LEFT
  );

  public enum Direction {
    UP('|', position -> new Position(position.row - 1, position.column)),
    DOWN('|', position -> new Position(position.row + 1, position.column)),
    LEFT('-', position -> new Position(position.row, position.column - 1)),
    RIGHT('-', position -> new Position(position.row, position.column + 1)),
    DONE(' ', position -> new Position(position.row, position.column));

    public final char letter;
    public final UnaryOperator<Position> next;

    Direction(char letter, UnaryOperator<Position> next) {
      this.letter = letter;
      this.next = next;
    }
  }

  public static class Position {
    public final int row;
    public final int column;

    public Position(int row, int column) {
      this.row = row;
      this.column = column;
    }

    public char letter(char[][] maze) {
      return maze[row][column];
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Position)) {
        return false;
      }
      Position position = (Position) o;
      return row == position.row
          && column == position.column;
    }

    @Override
    public int hashCode() {
      return Objects.hash(row, column);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("row", row)
          .add("column", column)
          .toString();
    }

    public boolean isInside(char[][] maze) {
      return row >= 0 && column >= 0 && row < maze.length && column < maze[0].length;
    }
  }

  public static class State {
    public final Position position;
    public final Direction direction;

    public State(Position position, Direction direction) {
      this.position = position;
      this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof State)) {
        return false;
      }
      State state = (State) o;
      return Objects.equals(position, state.position) &&
          direction == state.direction;
    }

    @Override
    public int hashCode() {
      return Objects.hash(position, direction);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("position", position)
          .add("direction", direction)
          .toString();
    }
  }

  public static <T> T traverse(
      char[][] maze,
      T initialValue,
      BiFunction<State, T, T> visitor
  ) {
    State state = findStart(maze);
    T value = initialValue;

    while (state.direction != DONE) {
      value = visitor.apply(state, value);
      Position nextPosition = state.direction.next.apply(state.position);
      state = new State(nextPosition, direction(nextPosition, state.direction, maze));
    }

    return value;
  }

  private static Direction direction(Position nextPosition, Direction currentDirection, char[][] maze) {
    char nextLetter = nextPosition.letter(maze);

    if (nextLetter == ' ') {
      return DONE;
    } else if (nextLetter == '+') {
      for (Direction nextDirection : ImmutableList.of(UP, DOWN, LEFT, RIGHT)) {
        if (nextDirection == OPPOSITE_DIRECTIONS.get(currentDirection)) {
          continue;
        }

        Position position = nextDirection.next.apply(nextPosition);
        if (!position.isInside(maze)) {
          continue;
        }

        char letter = position.letter(maze);
        if (letter == nextDirection.letter || (letter >= 'A' && letter <= 'Z')) {
          return nextDirection;
        }
      }

      throw new IllegalStateException("No direction from " + nextPosition);
    } else {
      return currentDirection;
    }
  }

  public static String letters(ImmutableList<String> lines) {
    ImmutableSet<Character> directions = ImmutableSet.of('|', '-', '+');

    char[][] maze = parse(lines);

    return traverse(maze, "", (state, accumulator) -> {
      char letter = state.position.letter(maze);
      if (!directions.contains(letter)) {
        return accumulator + letter;
      }

      return accumulator;
    });
  }

  public static int steps(ImmutableList<String> lines) {
    char[][] maze = parse(lines);
    return traverse(maze, 0, (state, accumulator) -> accumulator + 1);
  }

  public static String path(ImmutableList<String> lines) {
    char[][] maze = parse(lines);

    char[][] buffer = new char[maze.length][maze[0].length];
    for (int row = 0; row < buffer.length; row ++) {
      for (int column = 0; column < buffer[row].length; column++) {
        buffer[row][column] = ' ';
      }
    }

    char[][] visited = traverse(maze, buffer, (state, accumulator) -> {
      char letter = state.position.letter(maze);
      if (letter == ' ') {
        letter = '.';
      }

      accumulator[state.position.row][state.position.column] = letter;
      return accumulator;
    });

    return Arrays.stream(visited)
        .map(String::new)
        .collect(Collectors.joining("\n"));
  }

  public static State findStart(char[][] maze) {
    for (int i = 0; i < maze[0].length; i ++) {
      if (maze[0][i] == '|') {
        return new State(new Position(0, i), DOWN);
      }
    }

    throw new IllegalStateException("No starting position in the maze.");
  }

  public static char[][] parse(ImmutableList<String> lines) {
    return lines.stream()
        .map(line -> line.toCharArray())
        .toArray(char[][]::new);
  }

  public static void main(String[] args) throws IOException {
    File file = new File(Resources.getResource("day19.txt").getFile());
    ImmutableList<String> lines = ImmutableList.copyOf(Files.readLines(file, Charsets.UTF_8));

    System.out.println(path(lines));

    System.out.println("Part 1: " + letters(lines));
    System.out.println("Part 2: " + steps(lines));
  }
}
