package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.ValueFactory;
import crazypants.enderio.machines.config.Config;

public final class TranceiverConfig {

  public static final ValueFactory F = Config.F.section("tranceiver");

  public static final IValue<Double> energyLoss = F.make("energyLoss", 0.1, //
      "Amount of energy lost when transfered by Dimensional Transceiver; 0 is no loss, 1 is 100% loss.").setMin(0).sync();

  public static final IValue<Integer> bucketEnergyCost = F.make("bucketEnergyCost", 100, //
      "The energy cost of transporting a bucket of fluid via a Dimensional Transceiver.").setMin(0).sync();

}
