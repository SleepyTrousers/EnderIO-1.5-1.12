package crazypants.enderio.block.lever;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import crazypants.enderio.ModObject;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.model.ModelLoader;

public class LeverStateMapper extends StateMapperBase {

  public static void create() {
    LeverStateMapper mapper = new LeverStateMapper();
    ModelLoader.setCustomStateMapper(ModObject.blockSelfResettingLever10.getBlock(), mapper);
    ModelLoader.setCustomStateMapper(ModObject.blockSelfResettingLever30.getBlock(), mapper);
    ModelLoader.setCustomStateMapper(ModObject.blockSelfResettingLever60.getBlock(), mapper);
    ModelLoader.setCustomStateMapper(ModObject.blockSelfResettingLever300.getBlock(), mapper);
  }

  @Override
  protected @Nonnull ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
    Map<IProperty<?>, Comparable<?>> map = Maps.<IProperty<?>, Comparable<?>> newLinkedHashMap(state.getProperties());
    return new ModelResourceLocation(Block.REGISTRY.getNameForObject(Blocks.LEVER), this.getPropertyString(map));
  }

}
