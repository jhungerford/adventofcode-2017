package dev.jh.adventofcode;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;

import static dev.jh.adventofcode.Day22.Direction.*;

public class Day22 {

  public static class Node {
    public final int row;
    public final int column;

    public Node(int row, int column) {
      this.row = row;
      this.column = column;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Node)) {
        return false;
      }
      Node node = (Node) o;
      return row == node.row &&
          column == node.column;
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
  }

  public static class Grid {
    public final ImmutableSet<Node> infectedNodes;

    public Grid(ImmutableSet<Node> infectedNodes) {
      this.infectedNodes = infectedNodes;
    }

    public static Grid fromLines(ImmutableList<String> lines) {
      int offset = lines.size() / 2;

      ImmutableSet.Builder<Node> infected = ImmutableSet.builder();
      for (int row = 0; row < lines.size(); row ++) {
        String line = lines.get(row);
        for (int column = 0; column < line.length(); column ++) {
          if (line.charAt(column) == '#') {
            infected.add(new Node(row - offset, column - offset));
          }
        }
      }

      return new Grid(infected.build());
    }
  }

  private static final ImmutableMap<Direction, Direction> LEFT_DIRECTIONS = ImmutableMap.of(
      UP, LEFT,
      DOWN, RIGHT,
      LEFT, DOWN,
      RIGHT, UP
  );

  private static final ImmutableMap<Direction, Direction> RIGHT_DIRECTIONS = ImmutableMap.of(
      UP, RIGHT,
      DOWN, LEFT,
      LEFT, UP,
      RIGHT, DOWN
  );

  private static final ImmutableMap<Direction, Direction> REVERSE_DIRECTIONS = ImmutableMap.of(
          UP, DOWN,
          DOWN, UP,
          LEFT, RIGHT,
          RIGHT, LEFT
  );

  public enum Direction {
    UP(node -> new Node(node.row - 1, node.column)),
    DOWN(node -> new Node(node.row + 1, node.column)),
    LEFT(node -> new Node(node.row, node.column - 1)),
    RIGHT(node -> new Node(node.row, node.column + 1));

    Direction(UnaryOperator<Node> step) {
      this.step = step;
    }

    public final UnaryOperator<Node> step;
  }

  public static class Virus {
    public static final Virus INITIAL = new Virus(new Node(0, 0), UP);

    public final Node position;
    public final Direction direction;

    public Virus(Node position, Direction direction) {
      this.position = position;
      this.direction = direction;
    }

    public Virus move(Direction newDirection) {
      return new Virus(newDirection.step.apply(position), newDirection);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Virus)) {
        return false;
      }
      Virus virus = (Virus) o;
      return Objects.equals(position, virus.position) &&
          direction == virus.direction;
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

  public static class State {
    public final Grid grid;
    public final Virus virus;

    public State(Grid grid, Virus virus) {
      this.grid = grid;
      this.virus = virus;
    }

    public State step() {
      boolean infected = isInfected();

      // 1. Turn right if the current node is infected, left otherwise
      Direction newDirection = infected
          ? RIGHT_DIRECTIONS.get(virus.direction)
          : LEFT_DIRECTIONS.get(virus.direction);

      // 2. Infect the current node if it's clean, clean it otherwise
      ImmutableSet<Node> newInfected;
      if (infected) {
        newInfected = grid.infectedNodes.stream()
            .filter(node -> !node.equals(virus.position))
            .collect(ImmutableSet.toImmutableSet());
      } else {
        newInfected = ImmutableSet.<Node>builder()
            .addAll(grid.infectedNodes)
            .add(virus.position)
            .build();
      }

      // 3. Virus moves forward one step in the direction it's facing
      return new State(new Grid(newInfected), virus.move(newDirection));
    }

    public boolean isInfected() {
      return grid.infectedNodes.contains(virus.position);
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
      return Objects.equals(grid, state.grid) &&
          Objects.equals(virus, state.virus);
    }

    @Override
    public int hashCode() {
      return Objects.hash(grid, virus);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("grid", grid)
          .add("virus", virus)
          .toString();
    }
  }

  public static class EvolvedState {
    private final Set<Node> weakened;
    private final Set<Node> infected;
    private final Set<Node> flagged;
    private Virus virus;


    public EvolvedState() {
      this.weakened = new HashSet<>();
      this.infected = new HashSet<>();
      this.flagged = new HashSet<>();
      this.virus = Virus.INITIAL;
    }

    public static EvolvedState fromGrid(Grid grid) {
      EvolvedState state = new EvolvedState();
      state.infected.addAll(grid.infectedNodes);

      return state;
    }

    /**
     * Performs one burst, modifying this EvolvedState for performance.
     *
     * @return This modified evolved state.
     */
    public EvolvedState stepModify() {
      // 1. Turn.  Clean -> left, Weakened -> don't turn, Infected -> right, Flagged -> reverse
      Direction newDirection;
      if (isInfected()) {
        newDirection = RIGHT_DIRECTIONS.get(virus.direction);
      } else if (isFlagged()) {
        newDirection = REVERSE_DIRECTIONS.get(virus.direction);
      } else if (isWeakened()) {
        newDirection = virus.direction;
      } else {
        newDirection = LEFT_DIRECTIONS.get(virus.direction);
      }

      // 2. Modify node.  Clean -> Weakened, Weakened -> Infected, Infected -> Flagged, Flagged -> Clean
      if (isWeakened()) { // Weakened -> Infected
        weakened.remove(virus.position);
        infected.add(virus.position);

      } else if (isInfected()) { // Infected -> Flagged
        infected.remove(virus.position);
        flagged.add(virus.position);

      } else if (isFlagged()) { // Flagged -> Clean
        flagged.remove(virus.position);

      } else { // Clean -> Weakened
        weakened.add(virus.position);
      }

      // 3. Move forward one node
      virus = virus.move(newDirection);

      return this;
    }

    public boolean isWeakened() {
      return weakened.contains(virus.position);
    }

    public boolean isInfected() {
      return infected.contains(virus.position);
    }

    public boolean isFlagged() {
      return flagged.contains(virus.position);
    }
  }

  public static int countInfections(Grid grid, int bursts) {
    State state = new State(grid, Virus.INITIAL);
    int numInfections = 0;

    for (int i = 0; i < bursts; i ++) {
      if (!state.isInfected()) {
        numInfections ++;
      }
      state = state.step();
    }

    return numInfections;
  }

  public static int countEvolvedInfections(Grid grid, int bursts) {
    EvolvedState state = EvolvedState.fromGrid(grid);

    int numInfections = 0;
    for (int i = 0; i < bursts; i ++) {
      if (state.isWeakened()) {
        numInfections ++;
      }

      state = state.stepModify();
    }

    return numInfections;
  }

  public static void main(String[] args) throws IOException {
    File file = new File(Resources.getResource("day22.txt").getFile());
    ImmutableList<String> lines = ImmutableList.copyOf(Files.readLines(file, Charsets.UTF_8));
    Grid grid = Grid.fromLines(lines);

    System.out.println("Part 1: " + countInfections(grid, 10000));
    System.out.println("Part 2: " + countEvolvedInfections(grid, 10000000));
  }
}
