package crazypants.enderio.zoo.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;

public final class ZooConfig {

  public static final IValueFactory F = Config.F.section("zoo");

  public static final IValueFactory F0 = Config.F.section(".mobs");
  public static final IValueFactory F1 = F0.section(".concussion_creeper");

  public static final IValue<Integer> explosionRange = F1.make("explosionRange", 5, //
      "The range of the 'teleport explosion'.").setRange(1, 99).sync();
  public static final IValue<Float> teleportRange = F1.make("teleportRange", 32f, //
      "Sets the max range entites can be telported when the creeper explodes.").setRange(1, 160).sync();
  public static final IValue<Integer> confusionDuration = F1.make("confusionDuration", 100, //
      "Sets the durtaion in ticks of the confusion effect applied on explosion.").setRange(20 * 1, 20 * 30).sync();

  public static final IValueFactory F2 = F0.section(".dire_slime");

  public static final IValue<Float> direSlime1Health = F2.make("direSlime1Health", 4f, //
      "Base health of the Dire Slime (small).").setRange(1, 99).sync();
  public static final IValue<Float> direSlime2Health = F2.make("direSlime2Health", 8f, //
      "Base health of the Dire Slime (medium).").setRange(1, 99).sync();
  public static final IValue<Float> direSlime3Health = F2.make("direSlime3Health", 20f, //
      "Base health of the Dire Slime (big).").setRange(1, 99).sync();

  public static final IValue<Float> direSlime1AttackDamage = F2.make("direSlime1AttackDamage", 3f, //
      "Base attack damage of the Dire Slime (small).").setRange(1, 99).sync();
  public static final IValue<Float> direSlime2AttackDamage = F2.make("direSlime2AttackDamage", 5f, //
      "Base attack damage of the Dire Slime (medium).").setRange(1, 99).sync();
  public static final IValue<Float> direSlime3AttackDamage = F2.make("direSlime3AttackDamage", 8f, //
      "Base attack damage of the Dire Slime (large).").setRange(1, 99).sync();

  public static final IValue<Float> direSlime1Chance = F1.make("direSlime1Chance", .2f, //
      "The chance that a Dire Slime will be spawned (0 = never, 1 = always).").setRange(0, 1).sync();
  public static final IValue<Float> direSlime2Chance = F1.make("direSlime2Chance", .4f, //
      "The chance that a medium Dire Slime will spawn when a small Dire Slime is killed (eg 0.12 for a 12% chance).").setRange(0, 1).sync();
  public static final IValue<Float> direSlime3Chance = F1.make("direSlime3Chance", .2f, //
      "The chance that a large Dire Slime will spawn when a medium Dire Slime is killed (eg 0.12 for a 12% chance).").setRange(0, 1).sync();

}
