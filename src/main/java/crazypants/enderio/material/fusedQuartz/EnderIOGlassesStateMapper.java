package crazypants.enderio.material.fusedQuartz;

import java.util.Map;

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
    ModelLoader.setCustomStateMapper(ModObject.blockFusedQuartz.getBlock(), mapper);
    for (FusedQuartzType glasstype : FusedQuartzType.values()) {
      ModelLoader.setCustomStateMapper(glasstype.getBlock(), mapper);
    }
  }

  @Override
  protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
    Map<IProperty<?>, Comparable<?>> map = Maps.<IProperty<?>, Comparable<?>> newLinkedHashMap(state.getProperties());

    map.remove(BlockColored.COLOR);

    return new ModelResourceLocation(Block.REGISTRY.getNameForObject(ModObject.blockFusedQuartz.getBlock()), this.getPropertyString(map));
  }

}
