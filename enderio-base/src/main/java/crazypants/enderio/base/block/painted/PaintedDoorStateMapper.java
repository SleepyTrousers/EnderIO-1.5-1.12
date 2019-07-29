package crazypants.enderio.base.block.painted;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.ModObject;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class PaintedDoorStateMapper {

  @SubscribeEvent
  public static void init(@Nonnull ModelRegistryEvent event) {
    final StateMap doorMapper = (new StateMap.Builder()).ignore(new IProperty[] { BlockDoor.POWERED }).build();
    ModelLoader.setCustomStateMapper(ModObject.blockPaintedDarkSteelDoor.getBlockNN(), doorMapper);
    ModelLoader.setCustomStateMapper(ModObject.blockPaintedIronDoor.getBlockNN(), doorMapper);
    ModelLoader.setCustomStateMapper(ModObject.blockPaintedWoodenDoor.getBlockNN(), doorMapper);
  }

}
