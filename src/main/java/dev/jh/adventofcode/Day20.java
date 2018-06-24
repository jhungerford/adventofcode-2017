package dev.jh.adventofcode;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20 {

  public static final Vector3D ORIGIN = new Vector3D(0, 0, 0);

  public static class Vector3D {
    public final int x;
    public final int y;
    public final int z;

    public Vector3D(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public Vector3D plus(Vector3D other) {
      return new Vector3D(
          x + other.x,
          y + other.y,
          z + other.z
      );
    }

    public int distance(Vector3D other) {
      return Math.abs(x - other.x)
          + Math.abs(y - other.y)
          + Math.abs(z - other.z);
    }

    public int magnitude() {
      return Math.abs(x) + Math.abs(y) + Math.abs(z);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Vector3D)) {
        return false;
      }
      Vector3D vector3D = (Vector3D) o;
      return x == vector3D.x &&
          y == vector3D.y &&
          z == vector3D.z;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("x", x)
          .add("y", y)
          .add("z", z)
          .toString();
    }
  }

  public static class Particle {
    // p=<-717,-4557,2578>, v=<153,21,30>, a=<-8,8,-7>
    private static final Pattern PATTERN = Pattern.compile(
        "^p=<(-?\\d+),(-?\\d+),(-?\\d+)>, "
            + "v=<(-?\\d+),(-?\\d+),(-?\\d+)>, "
            + "a=<(-?\\d+),(-?\\d+),(-?\\d+)>$");

    public final int id;
    public final Vector3D position;
    public final Vector3D velocity;
    public final Vector3D acceleration;

    public Particle(int id, Vector3D position, Vector3D velocity, Vector3D acceleration) {
      this.id = id;
      this.position = position;
      this.velocity = velocity;
      this.acceleration = acceleration;
    }

    public Particle update() {
      Vector3D newVelocity = velocity.plus(acceleration);
      Vector3D newPosition = position.plus(newVelocity);

      return new Particle(
          id,
          newPosition,
          newVelocity,
          acceleration
      );
    }

    public static Particle fromString(int id, String str) {
      Matcher matcher = PATTERN.matcher(str);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid particle: '" + str + "'");
      }

      return new Particle(
          id,
          new Vector3D(
              Integer.parseInt(matcher.group(1)),
              Integer.parseInt(matcher.group(2)),
              Integer.parseInt(matcher.group(3))
          ),
          new Vector3D(
              Integer.parseInt(matcher.group(4)),
              Integer.parseInt(matcher.group(5)),
              Integer.parseInt(matcher.group(6))
          ),
          new Vector3D(
              Integer.parseInt(matcher.group(7)),
              Integer.parseInt(matcher.group(8)),
              Integer.parseInt(matcher.group(9))
          )
      );
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Particle)) {
        return false;
      }
      Particle particle = (Particle) o;
      return id == particle.id &&
          Objects.equals(position, particle.position) &&
          Objects.equals(velocity, particle.velocity) &&
          Objects.equals(acceleration, particle.acceleration);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, position, velocity, acceleration);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("id", id)
          .add("position", position)
          .add("velocity", velocity)
          .add("acceleration", acceleration)
          .toString();
    }
  }

  /**
   * Returns the particle that will stay closest to the origin in the long-term.
   *
   * @param particles Particles to simulate
   * @return The id of the particle that will stay closes to the origin long-term.
   */
  public static int closestParticle(ImmutableSet<Particle> particles) {
    // Keep simulating the particles until the closest particle also has
    // the slowest velocity and acceleration away from the origin

    ImmutableSet<Particle> closest = closestParticles(particles);
    while (closest.size() != 1) {
      particles = particles.parallelStream()
          .map(Particle::update)
          .collect(ImmutableSet.toImmutableSet());

      closest = closestParticles(particles);
    }

    return closest.iterator().next().id;
  }

  private static ImmutableSet<Particle> closestParticles(ImmutableSet<Particle> particles) {
    ImmutableSet<Particle> closestParticles = lowest(particles, particle -> particle.position.distance(ORIGIN));
    ImmutableSet<Particle> slowestParticles = lowest(particles, particle -> particle.velocity.magnitude());
    ImmutableSet<Particle> draggiestParticles = lowest(particles, particle -> particle.acceleration.magnitude());

    return ImmutableSet.copyOf(Sets.intersection(Sets.intersection(
        closestParticles,
        slowestParticles),
        draggiestParticles
    ));
  }

  private static ImmutableSet<Particle> lowest(ImmutableSet<Particle> particles, ToIntFunction<Particle> mapper) {
    int value = particles.stream()
        .mapToInt(mapper)
        .min()
        .getAsInt();

    return particles.stream()
        .filter(particle -> mapper.applyAsInt(particle) == value)
        .collect(ImmutableSet.toImmutableSet());
  }

  public static void main(String[] args) throws IOException {
    File file = new File(Resources.getResource("day20.txt").getFile());
    ImmutableList<String> lines = ImmutableList.copyOf(Files.readLines(file, Charsets.UTF_8));

    ImmutableSet.Builder<Particle> particlesBuilder = ImmutableSet.builder();
    for (int i = 0; i < lines.size(); i ++) {
      particlesBuilder.add(Particle.fromString(i, lines.get(i)));
    }

    ImmutableSet<Particle> particles = particlesBuilder.build();

    System.out.println("Part 1: " + closestParticle(particles));
  }
}
