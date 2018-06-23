package dev.jh.adventofcode;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

public class Day18Test {

  public static final Day18.State SAMPLE_STATE = new Day18.State()
      .playSound(4)
      .setRegister('a', 2)
      .setRegister('b', 3)
      .setRegister('e', -1);

  @Test
  public void example() {
    ImmutableList<String> program = ImmutableList.of(
        "set a 1",
        "add a 2",
        "mul a a",
        "mod a 5",
        "snd a",
        "set a 0",
        "rcv a",
        "jgz a -1",
        "set a 1",
        "jgz a -2"
    );

    Day18.State actualState = Day18.runSound(program);
    assertThat(actualState.recoveredFrequency).isEqualTo(4);
  }

  @Test
  public void sendExample() {
    ImmutableList<String> program = ImmutableList.of(
        "snd 1",
        "snd 2",
        "snd p",
        "rcv a",
        "rcv b",
        "rcv c",
        "rcv d"
    );

    assertThat(Day18.runSend(program)).isEqualTo(3);
  }

  @Test
  public void numberRegisterOrNumber() {
    Day18.RegisterOrNumber number = new Day18.RegisterOrNumber("1");

    assertThat(number.get(SAMPLE_STATE)).isEqualTo(1);
  }

  @Test
  public void registerMissingRegisterOrNumber() {
    Day18.RegisterOrNumber register = new Day18.RegisterOrNumber("c");

    assertThat(register.get(SAMPLE_STATE)).isEqualTo(0);
  }

  @Test
  public void registerSetRegisterOrNumber() {
    Day18.RegisterOrNumber register = new Day18.RegisterOrNumber("b");

    assertThat(register.get(SAMPLE_STATE)).isEqualTo(3);
  }

  @Test
  public void sndInstruction() {
    Day18.Instruction instruction = new Day18.SndInstruction(new Day18.RegisterOrNumber("10"));
    Day18.State resultState = instruction.apply(SAMPLE_STATE);

    assertThat(resultState.playedFrequency).isEqualTo(10);
  }

  @Test
  public void setInstruction() {
    Day18.Instruction setAToB = new Day18.SetInstruction('a', new Day18.RegisterOrNumber("b"));
    Day18.Instruction setATo10 = new Day18.SetInstruction('a', new Day18.RegisterOrNumber("10"));
    Day18.Instruction setCToD = new Day18.SetInstruction('c', new Day18.RegisterOrNumber("d"));

    assertThat(setAToB.apply(SAMPLE_STATE).registers).containsEntry('a', 3L);
    assertThat(setATo10.apply(SAMPLE_STATE).registers).containsEntry('a', 10L);
    assertThat(setCToD.apply(SAMPLE_STATE).registers).containsEntry('c', 0L);
  }

  @Test
  public void addInstruction() {
    Day18.Instruction addAAndB = new Day18.AddInstruction('a', new Day18.RegisterOrNumber("b"));
    Day18.Instruction addAAnd10 = new Day18.AddInstruction('a', new Day18.RegisterOrNumber("10"));
    Day18.Instruction addCAnd10 = new Day18.AddInstruction('c', new Day18.RegisterOrNumber("10"));
    Day18.Instruction addCAndD = new Day18.AddInstruction('c', new Day18.RegisterOrNumber("d"));

    assertThat(addAAndB.apply(SAMPLE_STATE).registers).containsEntry('a', 5L);
    assertThat(addAAnd10.apply(SAMPLE_STATE).registers).containsEntry('a', 12L);
    assertThat(addCAnd10.apply(SAMPLE_STATE).registers).containsEntry('c', 10L);
    assertThat(addCAndD.apply(SAMPLE_STATE).registers).containsEntry('c', 0L);
  }

  @Test
  public void mulInstruction() {
    Day18.Instruction mulAAndB = new Day18.MulInstruction('a', new Day18.RegisterOrNumber("b"));
    Day18.Instruction mulAAnd10 = new Day18.MulInstruction('a', new Day18.RegisterOrNumber("10"));
    Day18.Instruction mulCAnd10 = new Day18.MulInstruction('c', new Day18.RegisterOrNumber("10"));
    Day18.Instruction mulCAndD = new Day18.MulInstruction('c', new Day18.RegisterOrNumber("d"));

    assertThat(mulAAndB.apply(SAMPLE_STATE).registers).containsEntry('a', 6L);
    assertThat(mulAAnd10.apply(SAMPLE_STATE).registers).containsEntry('a', 20L);
    assertThat(mulCAnd10.apply(SAMPLE_STATE).registers).containsEntry('c', 0L);
    assertThat(mulCAndD.apply(SAMPLE_STATE).registers).containsEntry('c', 0L);
  }

  @Test
  public void modInstruction() {
    Day18.Instruction modAAndB = new Day18.ModInstruction('a', new Day18.RegisterOrNumber("b"));
    Day18.Instruction modBAndA = new Day18.ModInstruction('b', new Day18.RegisterOrNumber("a"));
    Day18.Instruction modAAnd10 = new Day18.ModInstruction('a', new Day18.RegisterOrNumber("10"));
    Day18.Instruction modCAnd10 = new Day18.ModInstruction('c', new Day18.RegisterOrNumber("10"));
    Day18.Instruction modCAndD = new Day18.ModInstruction('c', new Day18.RegisterOrNumber("d"));

    assertThat(modAAndB.apply(SAMPLE_STATE).getRegister('a')).isEqualTo(2);
    assertThat(modBAndA.apply(SAMPLE_STATE).getRegister('b')).isEqualTo(1);
    assertThat(modAAnd10.apply(SAMPLE_STATE).getRegister('a')).isEqualTo(2);
    assertThat(modCAnd10.apply(SAMPLE_STATE).getRegister('c')).isEqualTo(0);
    assertThat(modCAndD.apply(SAMPLE_STATE).getRegister('c')).isEqualTo(0);
  }

  @Test
  public void rcvInstruction()  {
    Day18.Instruction rcvA = new Day18.RcvInstruction('a');
    Day18.Instruction rcvC = new Day18.RcvInstruction('c');
    Day18.Instruction rcvE = new Day18.RcvInstruction('e');

    assertThat(rcvA.apply(SAMPLE_STATE).recoveredFrequency).isEqualTo(4);
    assertThat(rcvC.apply(SAMPLE_STATE).recoveredFrequency).isEqualTo(0);
    assertThat(rcvE.apply(SAMPLE_STATE).recoveredFrequency).isEqualTo(4);
  }

  @Test
  public void jgzInstruction() {
    Day18.Instruction jgzA = new Day18.JgzInstruction(new Day18.RegisterOrNumber("a"), new Day18.RegisterOrNumber("2"));
    Day18.Instruction jgzB = new Day18.JgzInstruction(new Day18.RegisterOrNumber("b"), new Day18.RegisterOrNumber("-2"));
    Day18.Instruction jgzC = new Day18.JgzInstruction(new Day18.RegisterOrNumber("c"), new Day18.RegisterOrNumber("2"));
    Day18.Instruction jgzE = new Day18.JgzInstruction(new Day18.RegisterOrNumber("e"), new Day18.RegisterOrNumber("2"));

    assertThat(jgzA.apply(SAMPLE_STATE).programCounter).isEqualTo(2);
    assertThat(jgzB.apply(SAMPLE_STATE).programCounter).isEqualTo(-2);
    assertThat(jgzC.apply(SAMPLE_STATE).programCounter).isEqualTo(1);
    assertThat(jgzE.apply(SAMPLE_STATE).programCounter).isEqualTo(1);
  }
}
