package dev.jh.adventofcode;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

public class Day20Test {

  private static final Day20.Particle PARTICLE0 = new Day20.Particle(
      0,
      new Day20.Vector3D(-717, -4557, 2578),
      new Day20.Vector3D(153, 21, 30),
      new Day20.Vector3D(-8, 8, -7)
  );

  @Test
  public void particleFromString() {
    assertThat(Day20.Particle.fromString(0, "p=<-717,-4557,2578>, v=<153,21,30>, a=<-8,8,-7>")).isEqualTo(PARTICLE0);
  }

  @Test
  public void update() {
    assertThat(PARTICLE0.update()).isEqualTo(new Day20.Particle(
        PARTICLE0.id,
        new Day20.Vector3D(-572, -4528, 2601),
        new Day20.Vector3D(145, 29, 23),
        PARTICLE0.acceleration
    ));
  }

  @Test
  public void vectorPlus() {
    assertThat(new Day20.Vector3D(1, 2, 3).plus(new Day20.Vector3D(4, 5, 6)))
        .isEqualTo(new Day20.Vector3D(5, 7, 9));
  }

  @Test
  public void vectorDistance() {
    assertThat(new Day20.Vector3D(1, 2, -3).distance(new Day20.Vector3D(4, -1, 0))).isEqualTo(9);
  }

  @Test
  public void closestParticle() {
    ImmutableSet<Day20.Particle> particles = ImmutableSet.of(
        Day20.Particle.fromString(0, "p=<3,0,0>, v=<2,0,0>, a=<-1,0,0>"),
        Day20.Particle.fromString(1, "p=<4,0,0>, v=<0,0,0>, a=<-2,0,0>")
    );

    assertThat(Day20.closestParticle(particles)).isEqualTo(0);
  }

  @Test
  public void closestParticleTie() {
    // Three particles have the same acceleration magnitude, but one started further away and one started slower.
    ImmutableSet<Day20.Particle> particles = ImmutableSet.of(
        Day20.Particle.fromString(0, "p=<3,0,0>, v=<-2,0,0>, a=<-1,0,0>"),
        Day20.Particle.fromString(1, "p=<0,4,0>, v=<0,0,0>, a=<0,1,0>"),
        Day20.Particle.fromString(2, "p=<4,0,4>, v=<0,0,1>, a=<0,0,-1>")
    );

    assertThat(Day20.closestParticle(particles)).isEqualTo(2);
  }
}
