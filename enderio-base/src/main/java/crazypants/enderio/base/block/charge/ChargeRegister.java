package crazypants.enderio.base.block.charge;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

public class ChargeRegister {

  public static final @Nonnull ChargeRegister instance = new ChargeRegister();

  private final @Nonnull NNList<ICharge> charges = new NNList<ICharge>();

  private ChargeRegister() {
  }

  public void registerCharge(@Nonnull ICharge charge) {
    charge.setID(charges.size());
    charges.add(charge);
  }

  public @Nonnull ICharge getCharge(int id) {
    return charges.get(id);
  }

}
