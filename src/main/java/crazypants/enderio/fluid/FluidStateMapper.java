package crazypants.enderio.fluid;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {

  public final @Nonnull Fluid fluid;
  public final @Nonnull ModelResourceLocation location;

  public FluidStateMapper(@Nonnull Fluid fluid) {
    this.fluid = fluid;
    location = new ModelResourceLocation(EnderIO.DOMAIN + ":fluids", fluid.getName());
  }

  @Override
  protected @Nonnull ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
    return location;
  }

  @Override
  public @Nonnull ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
    return location;
  }
}