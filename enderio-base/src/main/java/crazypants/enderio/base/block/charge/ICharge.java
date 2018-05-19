package crazypants.enderio.base.block.charge;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICharge {

  void setID(int id);

  int getID();

  void explode(@Nonnull EntityPrimedCharge entity);

  @SideOnly(Side.CLIENT)
  void explodeEffect(@Nonnull World world, double x, double y, double z);

  @Nonnull
  Block getBlock();

}
