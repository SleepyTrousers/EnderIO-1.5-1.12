package crazypants.enderio.material.glass;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import crazypants.enderio.ModObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.model.ModelLoader;

public class EnderIOGlassesStateMapper extends StateMapperBase {

  public static void create() {
    EnderIOGlassesStateMapper mapper = new EnderIOGlassesStateMapper();
    for (FusedQuartzType glasstype : FusedQuartzType.values()) {
      ModelLoader.setCustomStateMapper(glasstype.getBlock(), mapper);
    }
  }

  @Override
  protected @Nonnull ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
    Map<IProperty<?>, Comparable<?>> map = Maps.<IProperty<?>, Comparable<?>> newLinkedHashMap(state.getProperties());

    map.remove(BlockColored.COLOR);

    return new ModelResourceLocation(Block.REGISTRY.getNameForObject(ModObject.blockFusedQuartz.getBlockNN()), this.getPropertyString(map));
  }

}
