package dev.jh.adventofcode;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

import static dev.jh.adventofcode.Day3.Direction.*;

public class Day3 {

  public static class Square {
    public final int x;
    public final int y;

    public Square(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Square square = (Square) o;
      return x == square.x &&
          y == square.y;
    }

    @Override
    public int hashCode() {

      return Objects.hash(x, y);
    }

    @Override
    public String toString() {
      return "Square{" +
          "x=" + x +
          ", y=" + y +
          '}';
    }
  }

  private static ImmutableMap<Direction, Direction> NEXT_DIRECTION = ImmutableMap.of(
      UP, LEFT,
      LEFT, DOWN,
      DOWN, RIGHT,
      RIGHT, UP
  );

  public enum Direction {
    UP(
        square -> new Square(square.x, square.y - 1),
        distance -> distance
    ),
    LEFT(
        square -> new Square(square.x - 1, square.y),
        distance -> distance + 1
    ),
    DOWN(
        square -> new Square(square.x, square.y + 1),
        distance -> distance
    ),
    RIGHT(
        square -> new Square(square.x + 1, square.y),
        distance -> distance + 1
    );

    public final UnaryOperator<Square> nextSquare;
    public final IntUnaryOperator nextDistance;

    Direction(UnaryOperator<Square> nextSquare, IntUnaryOperator nextDistance) {
      this.nextSquare = nextSquare;
      this.nextDistance = nextDistance;
    }
  }

  private static int valuesAround(Square square, Map<Square, Integer> values) {
    int sum = 0;

    for (int x = -1; x <= 1; x ++) {
      for (int y = -1; y <= 1; y ++) {
        sum += values.getOrDefault(new Square(square.x + x, square.y + y), 0);
      }
    }

    return sum;
  }

  public static int valueGreaterThan(int maxValue) {
    Map<Square, Integer> values = new HashMap<>();

    Square square = new Square(0, 0);
    int value = 1;
    values.put(square, value);

    Direction direction = RIGHT;
    int distance = 1;
    int progress = 0;

    while (value < maxValue) {
      square = direction.nextSquare.apply(square);
      progress ++;

      value = valuesAround(square, values);
      values.put(square, value);


      if (progress == distance) {
        direction = NEXT_DIRECTION.get(direction);
        distance = direction.nextDistance.applyAsInt(distance);
        progress = 0;
      }
    }

    return value;
  }

  public static void main(String[] args) {
    System.out.println("Part 2: " + valueGreaterThan(325489));
  }
}
