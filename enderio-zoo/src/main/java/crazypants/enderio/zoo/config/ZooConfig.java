package crazypants.enderio.zoo.config;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class ZooConfig {

  public static final IValueFactory F = Config.F.section("zoo");

  public static final IValue<Float> lootModifierFakePlayer = F.make("lootModifierFakePlayer", .5f, //
      "Adjust the probability of 'player kill' loot drops for fake players (machines). Does not affect normal drops or XP.").setRange(0, 1).sync();

  public static final IValueFactory F0 = F.section(".mobs");
  public static final IValueFactory CREEPER = F0.section(".concussion_creeper");

  public static final IValue<Integer> explosionRange = CREEPER.make("explosionRange", 5, //
      "The range of the 'teleport explosion'.").setRange(1, 99).sync();
  public static final IValue<Float> teleportRange = CREEPER.make("teleportRange", 32f, //
      "Sets the max range entites can be telported when the creeper explodes.").setRange(1.5, 160).sync();
  public static final IValue<Integer> confusionDuration = CREEPER.make("confusionDuration", 100, //
      "Sets the durtaion in ticks of the confusion effect applied on explosion.").setRange(20 * 1, 20 * 30).sync();
  public static final IValue<Double> creeperHealth = CREEPER.make("health", 20d, //
      "Health of Concussion Creeper.").setRange(1, 200).sync();

  public static final IValueFactory SLIME = F0.section(".dire_slime");

  public static final IValue<Boolean> direSlimeEnabled = SLIME.make("spawnDireSlimes", true, //
      "Should Dire Slimes be spawned when breaking a dirt block with the wrong tool?").sync();
  public static final IValue<Boolean> direSlimeEnabledHand = SLIME.make("spawnDireSlimesEmptyHand", false, //
      "Should Dire Slimes be spawned when breaking a dirt block with an empty hand?").sync();

  public static final IValue<Float> direSlime1Health = SLIME.make("direSlime1Health", 4f, //
      "Base health of the Dire Slime (small).").setRange(1, 99).sync();
  public static final IValue<Float> direSlime2Health = SLIME.make("direSlime2Health", 8f, //
      "Base health of the Dire Slime (medium).").setRange(1, 99).sync();
  public static final IValue<Float> direSlime3Health = SLIME.make("direSlime3Health", 20f, //
      "Base health of the Dire Slime (big).").setRange(1, 99).sync();

  public static final IValue<Float> direSlime1AttackDamage = SLIME.make("direSlime1AttackDamage", 3f, //
      "Base attack damage of the Dire Slime (small).").setRange(1, 99).sync();
  public static final IValue<Float> direSlime2AttackDamage = SLIME.make("direSlime2AttackDamage", 5f, //
      "Base attack damage of the Dire Slime (medium).").setRange(1, 99).sync();
  public static final IValue<Float> direSlime3AttackDamage = SLIME.make("direSlime3AttackDamage", 8f, //
      "Base attack damage of the Dire Slime (large).").setRange(1, 99).sync();

  public static final IValue<Float> direSlime1Chance = SLIME.make("direSlime1Chance", .2f, //
      "The chance that a Dire Slime will be spawned (0 = never, 1 = always).").setRange(0, 1).sync();
  public static final IValue<Float> direSlime2Chance = SLIME.make("direSlime2Chance", .4f, //
      "The chance that a medium Dire Slime will spawn when a small Dire Slime is killed (eg 0.12 for a 12% chance).").setRange(0, 1).sync();
  public static final IValue<Float> direSlime3Chance = SLIME.make("direSlime3Chance", .2f, //
      "The chance that a large Dire Slime will spawn when a medium Dire Slime is killed (eg 0.12 for a 12% chance).").setRange(0, 1).sync();

  public static final IValueFactory VOID = F0.section(".void_slime");

  public static final IValue<Float> voidSlimeHealth = SLIME.make("dvoidSlimeHealth", 10f, //
      "Base health of the Void Slime.").setRange(1, 99).sync();
  public static final IValue<Float> voidSlimeAttackDamage = SLIME.make("voidSlimeAttackDamage", 3f, //
      "Base attack damage of the Void Slime.").setRange(1, 99).sync();
  public static final IValue<Integer> voidSlimeRange = SLIME.make("voidSlimeRange", 8, //
      "Darkness range of the Void Slime.").setRange(4, 32).sync();

  public static final IValueFactory WOLF = F0.section(".dire_wolf");

  public static final IValue<Boolean> packAttackEnabled = WOLF.make("packAttackEnabled", true, //
      "When true all nearby dire wolves will join an attack.").sync();
  public static final IValue<Integer> packAttackRange = WOLF.make("packAttackRange", 16, //
      "What is 'nearby' for dire wolves to join an attack.").setRange(0, 32).sync();
  public static final IValue<Float> howlVolume = WOLF.make("howlVolumeMultiplier", 8f, //
      "The volume multiplier for the dire wolf's howl.").setRange(0, 99);
  public static final IValue<Float> howlChance = WOLF.make("howlChance", .05f, //
      "The chance a dire wolf will howl when it is asked to play a sound.").setRange(0, 1);
  public static final IValue<Integer> howlPackSize = WOLF.make("howlPackSize", 8, //
      "The amount of other dire wolves that will \"join in\" with the initial howl, per pack howl.").setRange(1, 10);
  public static final IValue<Float> howlPackChance = WOLF.make("howlPackChance", .6f, //
      "The chance that when a dire wolf howls, nearby dire wolves will \"join in\" to a pack howl.").setRange(0, 1);
  public static final IValue<Double> wolfAggressionRange = WOLF.make("aggressionRange", 3d, //
      "If a player gets within this range they will be attacked.").setRange(0, 32).sync();
  public static final IValue<Double> wolfHealth = WOLF.make("health", 20d, //
      "Health of Dire Wolves.").setRange(1, 200).sync();
  public static final IValue<Double> wolfAttackDamage = WOLF.make("attackDamage", 10d, //
      "Base attack damage of Dire Wolves.").setRange(1, 200).sync();

  public static final IValueFactory MINI = F0.section(".endermini");

  public static final IValue<Boolean> attackPlayers = MINI.make("attackPlayers", false, //
      "When true an Enderminy will attack a player if it looks at them, otherwise they are neutral mobs.").sync();
  public static final IValue<Boolean> attackCreepers = MINI.make("attackCreepers", true, //
      "When true Enderminies will attack creepers.").sync();
  public static final IValue<Boolean> spawnInLitAreas = MINI.make("spawnInLitAreas", false, //
      "When true enderminies will spawn in well lit areas, when false they will only spawn in dark areas.").sync();
  public static final IValue<Boolean> spawnOnlyOnGrass = MINI.make("spawnOnlyOnGrass", true, //
      "When true enderminies will spawn only on (vanilla) grass blocks.").sync();
  public static final IValue<Integer> spawnMinY = MINI.make("spawnMinY", 0, //
      "The minimum Y level at which enderminies will spawn.").setRange(0, 255).sync();
  public static final IValue<Boolean> miniPackAttackEnabled = MINI.make("packAttackEnabled", true, //
      "When true attacking one Enderminy will cause other Enderminies who witness the attack to attack the player as well.").sync();
  public static final IValue<Double> miniHealth = MINI.make("health", 20d, //
      "Health of Enderminies.").setRange(1, 200).sync();
  public static final IValue<Double> miniAttackDamage = MINI.make("attackDamage", 10d, //
      "Base attack damage of Enderminies.").setRange(1, 200).sync();

  public static final IValueFactory KNIGHT = F0.section(".fallen_knight");

  public static final IValue<Double> fallenKnightChargeSpeed = KNIGHT.make("chargeSpeed", 1.2, //
      "The speed at which a knight will charge its target.").setRange(0, 10).sync();
  public static final IValue<Double> fallenKnightFollowRange = KNIGHT.make("followRange", 40.0, //
      "Follow range of a knight.").setRange(1, 100).sync();
  public static final IValue<Integer> fallenKnightRangedMinAttackPause = KNIGHT.make("rangedMinAttackPause", 20, //
      "The min number of ticks between ranged attacks.").setRange(1, 200).sync();
  public static final IValue<Integer> fallenKnightRangedMaxAttackPause = KNIGHT.make("rangedMaxAttackPause", 60, //
      "The max number of ticks between ranged attacks.").setRange(1, 200).sync();
  public static final IValue<Float> fallenKnightRangedMaxRange = KNIGHT.make("rangedMaxRange", 15f, //
      "The max attack range when using a bow.").setRange(1, 200).sync();
  public static final IValue<Boolean> fallKnightMountedArchersMaintainDistance = KNIGHT.make("mountedArchersMaintainDistance", true, //
      "When true mounted archer knights will attempt to keep distance between themselves and their target.").sync();
  public static final IValue<Boolean> fallenKnightArchersSwitchToMelee = KNIGHT.make("archersSwitchToMelee", true, //
      "When true archer knights will switch to a sword when target is within melee range. "
          + "Doesn't apply to mounted archers if fallKnightMountedArchersMaintainDistance is true")
      .sync();
  public static final IValue<Float> fallenKnightChanceMounted = KNIGHT.make("chanceMounted", .75f, //
      "The chance a spawned knight will be mounted.").setRange(0, 1).sync();
  public static final IValue<Float> fallenKnightChancePerArmorPiece = KNIGHT.make("chancePerArmorPiece", .7f, //
      "The chance each armor piece has of being added to a spawned knight.").setRange(0, 1).sync();
  public static final IValue<Float> fallenKnightChancePerArmorPieceHard = KNIGHT.make("chancePerArmorPieceHard", .9f, //
      "The chance each armor piece has of being added to a spawned knight when difficulty is set to hard.").setRange(0, 1).sync();
  public static final IValue<Float> fallenKnightRangedRatio = KNIGHT.make("chanceArchers", .25f, //
      "The precentage of spawned knoghts equipped with bows.").setRange(0, 1).sync();
  public static final IValue<Float> fallenKnightChanceAgentOfShield = KNIGHT.make("chanceShield", .3f, //
      "The chance a shield will be equipped.").setRange(0, 1).sync();

  public static final IValue<Float> fallenKnightChanceArmorUpgrade = KNIGHT.make("chanceArmorUpgrade", .2f, //
      "The chance the type of armor equipped will be improved.").setRange(0, 1).sync();
  public static final IValue<Float> fallenKnightChanceArmorUpgradeHard = KNIGHT.make("chanceArmorUpgradeHard", .4f, //
      "The chance the type of armor equipped will be improved when difficulty is set to hard.").setRange(0, 1).sync();

  public static final IValue<Double> fallenKnightHealth = KNIGHT.make("health", 20d, //
      "Health of Fallen Knights.").setRange(1, 200).sync();
  public static final IValue<Double> fallenKnightAttackDamage = KNIGHT.make("attackDamage", 4d, //
      "Base attack damage of Fallen Knights.").setRange(1, 200).sync();

  public static final IValueFactory MOUNT = F0.section(".fallen_mount");

  public static final IValue<Double> fallenMountChargeSpeed = MOUNT.make("chargeSpeed", 2.5, //
      "The speed at which a mount will charge its target. 0 to disable.").setRange(0, 10).sync();
  public static final IValue<Double> fallenMountHealth = MOUNT.make("health", 30d, //
      "Base health of the mount.").setRange(1, 200).sync();
  public static final IValue<Double> fallenMountAttackDamage = MOUNT.make("attackDamage", 4d, //
      "Base attack damage of Fallen Mounts.").setRange(1, 200).sync();
  public static final IValue<Float> fallenMountChanceArmored = MOUNT.make("chanceArmored", .5f, //
      "The chance a spawned mount will be armored.").setRange(0, 1).sync();
  public static final IValue<Float> fallenMountChanceArmoredHard = MOUNT.make("chanceArmoredHard", .9f, //
      "The chance a spawned mount will be armored when difficulty is set to hard.").setRange(0, 1).sync();
  public static final IValue<Float> fallenMountChanceArmorUpgrade = MOUNT.make("chanceArmorUpgrade", .01f, //
      "The chance the type of armor equipped will be improved.").setRange(0, 1).sync();
  public static final IValue<Float> fallenMountChanceArmorUpgradeHard = MOUNT.make("chanceArmorUpgradeHard", .05f, //
      "The chance the type of armor equipped will be improved when difficulty is set to hard.").setRange(0, 1).sync();
  public static final IValue<Boolean> fallenMountShadedByRider = MOUNT.make("shadedByRider", true, //
      "When true a mount will not burn in the sun unless its rider is.").sync();

  public static final IValueFactory OWL = F0.section(".owl");

  public static final IValue<Double> owlAggressionRange = OWL.make("aggressionRange", 12d, //
      "If a spider gets within this range (horizontal) they will be attacked.").setRange(0, 32).sync();
  public static final IValue<Double> owlAggressionRangeVertical = OWL.make("aggressionRangeVertical", 24d, //
      "If a spider gets within this range (vertical) they will be attacked.").setRange(0, 32).sync();
  public static final IValue<Float> owlSpiderDamageMultiplier = OWL.make("spiderDamageMultiplier", 2f, //
      "Damage multiplier against spiders.").setRange(0, 10).sync();
  public static final IValue<Float> owlHootVolumeMultiplier = OWL.make("hootVolumeMultiplier", .8f, //
      "Adjusts the owls' hoot volume. Higher value is louder.").setRange(0, 2);
  public static final IValue<Integer> owlHootInterval = OWL.make("hootInterval", 1000, //
      "Aprox. number of ticks between hoots.").setMin(1);
  public static final IValue<Integer> owlTimeBetweenEggsMin = OWL.make("timeBetweenEggsMin", 12000, //
      "Min ticks between egg laying.").setMin(1).sync();
  public static final IValue<Integer> owlTimeBetweenEggsMax = OWL.make("timeBetweenEggsMax", 24000, //
      "Max ticks between egg laying.").setMin(1).sync();

  public static final IValue<Double> owlHealth = OWL.make("health", 10d, //
      "Health of Owls.").setRange(1, 200).sync();
  public static final IValue<Double> OwlDamage = OWL.make("attackDamage", 4d, //
      "Base attack damage of Owls.").setRange(1, 200).sync();

  public static final IValueFactory CAT = F0.section(".wither_cat");

  public static final IValue<Double> witherCatHealth = CAT.make("health", 12d, //
      "Base health of the wither cat.").setRange(1, 200).sync();
  public static final IValue<Double> witherCatHealthBonusAngry = CAT.make("healthBonusAngry", 18d, //
      "Health bonus for an angry wither cat.").setRange(0, 200).sync();
  public static final IValue<Double> witherCatAttackDamage = CAT.make("attackDamage", 3d, //
      "Base attack damage of the wither cat.").setRange(1, 200).sync();
  public static final IValue<Double> witherCatAttackDamageBonusAngry = CAT.make("attackDamageBonusAngry", 6d, //
      "Attack damage bonus for an angry wither cat.").setRange(0, 200).sync();
  public static final IValue<Double> witherCatAttackDamageBonusHard = CAT.make("attackDamageBonusHard", 2d, //
      "Attack damage bonus for a wither cat when difficulty is set to hard.").setRange(0, 200).sync();

  public static final IValueFactory WITCH = F0.section(".wither_witch");

  public static final IValue<Boolean> witherCatEnabled = WITCH.make("spawnWitherCats", true, //
      "Should Wither Cats spawn for Wither Witches?").sync();
  public static final IValue<Integer> witherCatMinimum = WITCH.make("spawnWitherCatsMin", 1, //
      "How many Wither Cats should a Wither Witche at least have?").sync();
  public static final IValue<Integer> witherCatMaximum = WITCH.make("spawnWitherCatsMax", 2, //
      "How many Wither Cats should a Wither Witche at most have?").sync();

  public static final IValue<Double> witherWitchHealth = WITCH.make("health", 30d, //
      "Health of Wither Witches.").setRange(1, 200).sync();

  public static final IValueFactory LOVE = F0.section(".love_child");

  public static final IValue<Float> attackTeleportChance = LOVE.make("attackTeleportChance", .05f, //
      "The chance a Love Child will teleport an attacker away.").setRange(0, 1);
  public static final IValue<Float> attackTeleportDistance = LOVE.make("attackTeleportDistance", 8f, //
      "The maximum distance a Love Child will teleport an attacker away.").setRange(1.5, 64);
  public static final IValue<Float> defendTeleportChance = LOVE.make("defendTeleportChance", .25f, //
      "The chance a Love Child will teleport away when attacked.").setRange(0, 1);
  public static final IValue<Float> defendTeleportDistance = LOVE.make("defendTeleportDistance", 8f, //
      "The maximum distance a Love Child will teleport away when attacked.").setRange(1.5, 64);
  public static final IValue<Double> loveChildHealth = LOVE.make("health", 25d, //
      "Health of Love Children.").setRange(1, 200).sync();
  public static final IValue<Double> loveChildAttackDamage = LOVE.make("attackDamage", 10d, //
      "Base attack damage of Love Children.").setRange(1, 200).sync();
  public static final IValue<Double> loveChildSpeed = LOVE.make("speed", 0.2d, //
      "Movement speeds of Love Children. (Zombie: 0.23)").setRange(0.02, 2).sync();
  public static final IValue<Double> loveChildArmor = LOVE.make("armor", 3d, //
      "Armor of Love Children. (Zombie: 2)").setRange(0, 20).sync();

  public static final IValue<SkullDrop> loveSkullDrop = LOVE.make("skullDrop", SkullDrop.ZOMBIE, //
      "Type of skull Love Children should drop (ZOMBIE/ENDERMAN/NONE). Note that there is an " + //
          "additional Enderman skull in the loot table.")
      .sync();

  public static final IValue<Boolean> loveSummonAid = LOVE.make("summonAid", true, //
      "Should Love Children summon aid like vanilla Zombies do?").sync();

  public static enum SkullDrop {
    NONE,
    ZOMBIE,
    ENDERMAN;
  }

  public static final IValueFactory SQUID = F0.section(".epic_squid");

  public static final IValue<Double> epicSquidHealth = SQUID.make("health", 50d, //
      "Health of Epic Squids.").setRange(1, 200).sync();
  public static final IValue<Double> epicSquidAttackDamage = SQUID.make("attackDamage", 10d, //
      "Base attack damage of Epic Squids.").setRange(1, 200).sync();

}
