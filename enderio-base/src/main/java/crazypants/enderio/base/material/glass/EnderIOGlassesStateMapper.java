package crazypants.enderio.base.material.glass;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.ModObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class EnderIOGlassesStateMapper extends StateMapperBase {

  @SubscribeEvent
  public static void init(@Nonnull ModelRegistryEvent event) {
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
