package dev.jh.adventofcode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Day24Test {

  public static final ImmutableList<Day24.Component> EXAMPLE_COMPONENTS =ImmutableList.of(
      Day24.Component.fromString("0/2"),
      Day24.Component.fromString("2/2"),
      Day24.Component.fromString("2/3"),
      Day24.Component.fromString("3/4"),
      Day24.Component.fromString("3/5"),
      Day24.Component.fromString("0/1"),
      Day24.Component.fromString("10/1"),
      Day24.Component.fromString("9/10")
  );

  @Test
  public void componentFromString() {
    assertThat(Day24.Component.fromString("32/31")).isEqualTo(new Day24.Component(32, 31));
  }

  @Test
  public void componentLowHigh() {
    Day24.Component lowHigh = Day24.Component.fromString("31/32");
    Day24.Component highLow = Day24.Component.fromString("10/1");

    assertThat(lowHigh.getLow()).isEqualTo(31);
    assertThat(lowHigh.getHigh()).isEqualTo(32);

    assertThat(highLow.getLow()).isEqualTo(1);
    assertThat(highLow.getHigh()).isEqualTo(10);
  }

  @Test
  public void bridgeStrength() {
    ImmutableList<Day24.Component> bridge1 = ImmutableList.of(Day24.Component.fromString("0/1"));
    assertThat(Day24.bridgeStrength(bridge1)).isEqualTo(1);

    ImmutableList<Day24.Component> bridge31 = ImmutableList.of(
        Day24.Component.fromString("0/1"),
        Day24.Component.fromString("1/10"),
        Day24.Component.fromString("10/9"));
    assertThat(Day24.bridgeStrength(bridge31)).isEqualTo(31);
  }

  @Test
  public void validBridgesExample() {
    assertThat(Day24.validBridges(EXAMPLE_COMPONENTS)).containsExactlyInAnyOrder(
        ImmutableList.of(Day24.Component.fromString("0/1")),
        ImmutableList.of(Day24.Component.fromString("0/1"), Day24.Component.fromString("10/1")),
        ImmutableList.of(Day24.Component.fromString("0/1"), Day24.Component.fromString("10/1"), Day24.Component.fromString("9/10")),
        ImmutableList.of(Day24.Component.fromString("0/2")),
        ImmutableList.of(Day24.Component.fromString("0/2"), Day24.Component.fromString("2/3")),
        ImmutableList.of(Day24.Component.fromString("0/2"), Day24.Component.fromString("2/3"), Day24.Component.fromString("3/4")),
        ImmutableList.of(Day24.Component.fromString("0/2"), Day24.Component.fromString("2/3"), Day24.Component.fromString("3/5")),
        ImmutableList.of(Day24.Component.fromString("0/2"), Day24.Component.fromString("2/2")),
        ImmutableList.of(Day24.Component.fromString("0/2"), Day24.Component.fromString("2/2"), Day24.Component.fromString("2/3")),
        ImmutableList.of(Day24.Component.fromString("0/2"), Day24.Component.fromString("2/2"), Day24.Component.fromString("2/3"), Day24.Component.fromString("3/4")),
        ImmutableList.of(Day24.Component.fromString("0/2"), Day24.Component.fromString("2/2"), Day24.Component.fromString("2/3"), Day24.Component.fromString("3/5"))
    );
  }

  @Test
  public void strongestExample() {
    assertThat(Day24.strongestBridge(Day24.validBridges(EXAMPLE_COMPONENTS))).isEqualTo(31);
  }

  @Test
  public void longestExample() {
    assertThat(Day24.longestBridge(Day24.validBridges(EXAMPLE_COMPONENTS))).isEqualTo(19);
  }

  @Test
  public void portMap() {
    assertThat(Day24.portMap(EXAMPLE_COMPONENTS)).isEqualTo(ImmutableMap.<Integer, ImmutableSet<Integer>>builder()
        .put(0, ImmutableSet.of(1, 2))
        .put(1, ImmutableSet.of(0, 10))
        .put(2, ImmutableSet.of(0, 2, 3))
        .put(3, ImmutableSet.of(2, 4, 5))
        .put(4, ImmutableSet.of(3))
        .put(5, ImmutableSet.of(3))
        .put(9, ImmutableSet.of(10))
        .put(10, ImmutableSet.of(1, 9))
        .build()
    );
  }
}