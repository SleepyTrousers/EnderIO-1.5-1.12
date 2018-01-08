package crazypants.enderio.base.block.coldfire;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.ModObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.model.ModelLoader;

public class ColdFireStateMapper extends StateMapperBase {

  public static void create() {
    ColdFireStateMapper mapper = new ColdFireStateMapper();
    ModelLoader.setCustomStateMapper(ModObject.blockColdFire.getBlockNN(), mapper);
  }

  @Override
  protected @Nonnull ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
    return new ModelResourceLocation(Block.REGISTRY.getNameForObject(Blocks.FIRE), this.getPropertyString(state.getProperties()));
  }

}
