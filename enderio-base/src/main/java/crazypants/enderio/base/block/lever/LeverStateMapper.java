package crazypants.enderio.base.block.lever;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.ModObject;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class LeverStateMapper extends StateMapperBase {

  @SubscribeEvent
  public static void init(@Nonnull ModelRegistryEvent event) {
    LeverStateMapper mapper = new LeverStateMapper();
    ModelLoader.setCustomStateMapper(ModObject.blockSelfResettingLever5.getBlockNN(), mapper);
    ModelLoader.setCustomStateMapper(ModObject.blockSelfResettingLever10.getBlockNN(), mapper);
    ModelLoader.setCustomStateMapper(ModObject.blockSelfResettingLever30.getBlockNN(), mapper);
    ModelLoader.setCustomStateMapper(ModObject.blockSelfResettingLever60.getBlockNN(), mapper);
    ModelLoader.setCustomStateMapper(ModObject.blockSelfResettingLever300.getBlockNN(), mapper);
  }

  @Override
  protected @Nonnull ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
    Map<IProperty<?>, Comparable<?>> map = Maps.<IProperty<?>, Comparable<?>> newLinkedHashMap(state.getProperties());
    return new ModelResourceLocation(Block.REGISTRY.getNameForObject(Blocks.LEVER), this.getPropertyString(map));
  }

}
