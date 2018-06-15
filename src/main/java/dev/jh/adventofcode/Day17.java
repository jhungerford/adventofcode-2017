package dev.jh.adventofcode;

import com.google.common.base.MoreObjects;

import java.util.Arrays;
import java.util.Objects;

public class Day17 {

  public static class Buffer {
    public final int position;
    public final int[] values;

    /**
     * Constructs a new buffer containing only the value '0'.
     */
    public Buffer() {
      this.position = 0;
      this.values = new int[]{0};
    }

    public Buffer(int position, int[] values) {
      this.position = position;
      this.values = values;
    }

    /**
     * Advances the current position by the given number of steps around the circular buffer.  Returns a new
     * buffer with the updated position.
     *
     * @param steps Number of steps to advance the buffer
     * @return New buffer with the current position advanced by the number of steps
     */
    public Buffer step(int steps) {
      return new Buffer(
          modPosition(position + steps),
          values
      );
    }

    /**
     * Inserts the given value after the current position, and changes the current position to the new value.
     * Returns a new buffer representing the new state.
     *
     * @param value Value to insert
     * @return Buffer with the value inserted after the current position, and the current position updated
     */
    public Buffer insertAfter(int value) {
      int[] newValues = new int[values.length + 1];
      System.arraycopy(values, 0, newValues, 0, position + 1);
      newValues[position + 1] = value;

      if (position < values.length - 1) {
        System.arraycopy(values, position + 1, newValues, position + 2, values.length - position - 1);
      }

      return new Buffer(
          (position + 1) % newValues.length,
          newValues
      );
    }

    /**
     * Returns the position of the given value, or -1 if the value isn't present in this buffer.
     *
     * @param value Value to find
     * @return Position of the given value, or -1 if the value isn't present.
     */
    public int positionOf(int value) {
      for (int i = 0; i < values.length; i ++) {
        if (values[i] == value) {
          return i;
        }
      }

      return -1;
    }

    /**
     * Returns the given position, constrained to the range of positions in this buffer's values.
     *
     * @param position Position to mod
     * @return Position within this buffer's value
     */
    public int modPosition(int position) {
      return position % values.length;
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof Buffer)) {
        return false;
      }
      Buffer buffer = (Buffer) other;
      return position == buffer.position && Arrays.equals(values, buffer.values);
    }

    @Override
    public int hashCode() {
      int result = Objects.hash(position);
      result = 31 * result + Arrays.hashCode(values);
      return result;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("position", position)
          .add("values", values)
          .toString();
    }
  }

  /**
   * Returns a buffer representing the result of performing times insertions into an empty buffer,
   * stepping by the given amount each time.
   *
   * @param step Step size
   * @param times Number of times to perform insertions
   * @return Resulting buffer
   */
  public static Buffer insertTimes(int step, int times) {
    Buffer buffer = new Buffer();

    for (int i = 1; i <= times; i ++) {
      buffer = buffer
          .step(step)
          .insertAfter(i);
    }

    return buffer;
  }

  public static void main(String[] args) {
    Buffer part1 = insertTimes(349, 2017);
    int part1Index = part1.positionOf(2017);

    System.out.println("Part 1: " + part1.values[part1.modPosition(part1Index + 1)]);
  }
}
