package crazypants.enderio.base.block.coldfire;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.ModObject;
import net.minecraft.block.Block;
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
public class ColdFireStateMapper extends StateMapperBase {

  @SubscribeEvent
  public static void init(@Nonnull ModelRegistryEvent event) {
    ColdFireStateMapper mapper = new ColdFireStateMapper();
    ModelLoader.setCustomStateMapper(ModObject.blockColdFire.getBlockNN(), mapper);
  }

  @Override
  protected @Nonnull ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
    return new ModelResourceLocation(Block.REGISTRY.getNameForObject(Blocks.FIRE), this.getPropertyString(state.getProperties()));
  }

}
