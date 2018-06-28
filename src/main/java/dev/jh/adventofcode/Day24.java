package dev.jh.adventofcode;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Day24 {
  public static class Component {
    public final int left;
    public final int right;

    public Component(int left, int right) {
      this.left = left;
      this.right = right;
    }

    public int getLow() {
      return left < right ? left : right;
    }

    public int getHigh() {
      return left > right ? left : right;
    }

    public static Component fromString(String string) {
      String[] parts = string.split("/");
      return new Component(
          Integer.parseInt(parts[0]),
          Integer.parseInt(parts[1])
      );
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Component component = (Component) o;
      return left == component.left &&
          right == component.right;
    }

    @Override
    public int hashCode() {
      return Objects.hash(left, right);
    }

    @Override
    public String toString() {
      return left + "/" + right;
    }
  }

  public static int bridgeStrength(ImmutableList<Component> components) {
    return components.stream()
        .mapToInt(component -> component.left + component.right)
        .sum();
  }

  public static ImmutableMap<Integer, ImmutableSet<Integer>> portMap(ImmutableList<Component> components) {
    Map<Integer, ImmutableSet.Builder<Integer>> map = new HashMap<>();

    for (Component component : components) {
      map.computeIfAbsent(component.left, i -> ImmutableSet.builder()).add(component.right);
      map.computeIfAbsent(component.right, i -> ImmutableSet.builder()).add(component.left);
    }

    return map.entrySet().stream().collect(ImmutableMap.toImmutableMap(
        Map.Entry::getKey,
        entry -> entry.getValue().build()
    ));
  }

  public static ImmutableSet<ImmutableList<Component>> validBridges(ImmutableList<Component> components) {
    ImmutableMap<Integer, ImmutableSet<Integer>> portMap = portMap(components);
    ImmutableSet.Builder<ImmutableList<Component>> bridges = ImmutableSet.builder();

    validBridgesRecursive(0, bridges, ImmutableList.of(), ImmutableSet.copyOf(components), portMap);

    return bridges.build();
  }

  private static void validBridgesRecursive(
      int inPort,
      ImmutableSet.Builder<ImmutableList<Component>> bridges,
      ImmutableList<Component> bridgeSoFar,
      ImmutableSet<Component> remainingComponents,
      ImmutableMap<Integer, ImmutableSet<Integer>> portMap
  ) {
    for (int outPort : portMap.get(inPort)) {
      Component inOutComponent = new Component(inPort, outPort);
      Component outInComponent = new Component(outPort, inPort);

      Component nextComponent;
      if (remainingComponents.contains(inOutComponent)) {
        nextComponent = inOutComponent;
      } else if (remainingComponents.contains(outInComponent)) {
        nextComponent = outInComponent;
      } else {
        continue;
      }

      ImmutableList<Component> nextBridgeSoFar = appendComponent(bridgeSoFar, nextComponent);
      bridges.add(nextBridgeSoFar);

      ImmutableSet<Component> nextRemainingComponents = remainingComponents.stream()
          .filter(c -> !c.equals(nextComponent))
          .collect(ImmutableSet.toImmutableSet());

      validBridgesRecursive(outPort, bridges, nextBridgeSoFar, nextRemainingComponents, portMap);
    }
  }

  private static ImmutableList<Component> appendComponent(ImmutableList<Component> bridgeSoFar, Component component) {
    return ImmutableList.<Component>builder()
        .addAll(bridgeSoFar)
        .add(component)
        .build();
  }

  public static int strongestBridge(ImmutableList<Component> components) {
    return validBridges(components).stream()
        .mapToInt(Day24::bridgeStrength)
        .max()
        .orElseThrow(() -> new IllegalArgumentException("No valid bridges in the given components"));
  }

  public static void main(String[] args) throws IOException {
    File file = new File(Resources.getResource("day24.txt").getFile());
    ImmutableList<Component> components = Files.readLines(file, Charsets.UTF_8).stream()
        .map(Component::fromString)
        .collect(ImmutableList.toImmutableList());

    System.out.println("Part 1: " + strongestBridge(components));
  }
}
