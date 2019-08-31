package crazypants.enderio.machines.machine.obelisk.render;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.material.material.Material;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.machine.obelisk.attractor.TileAttractor;
import crazypants.enderio.machines.machine.obelisk.aversion.AversionObeliskRenderer;
import crazypants.enderio.machines.machine.obelisk.aversion.TileAversionObelisk;
import crazypants.enderio.machines.machine.obelisk.inhibitor.TileInhibitorObelisk;
import crazypants.enderio.machines.machine.obelisk.relocator.RelocatorObeliskRenderer;
import crazypants.enderio.machines.machine.obelisk.relocator.TileRelocatorObelisk;
import crazypants.enderio.machines.machine.obelisk.weather.TileWeatherObelisk;
import crazypants.enderio.machines.machine.obelisk.weather.WeatherObeliskSpecialRenderer;
import crazypants.enderio.machines.machine.obelisk.xp.TileExperienceObelisk;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.init.MachineObject.block_attractor_obelisk;
import static crazypants.enderio.machines.init.MachineObject.block_aversion_obelisk;
import static crazypants.enderio.machines.init.MachineObject.block_experience_obelisk;
import static crazypants.enderio.machines.init.MachineObject.block_inhibitor_obelisk;
import static crazypants.enderio.machines.init.MachineObject.block_relocator_obelisk;
import static crazypants.enderio.machines.init.MachineObject.block_weather_obelisk;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = EnderIOMachines.MODID, value = Side.CLIENT)
public class ObeliskRenderManager {

  public static final @Nonnull ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("enderio:obelisk");

  private static final @Nonnull TextureSupplier[] textures = { TextureRegistry.registerTexture("blocks/obelisk_bottom"),
      TextureRegistry.registerTexture("blocks/block_soul_machine_top"), TextureRegistry.registerTexture("blocks/block_attractor_side"),
      TextureRegistry.registerTexture("blocks/block_attractor_side"), TextureRegistry.registerTexture("blocks/block_attractor_side"),
      TextureRegistry.registerTexture("blocks/block_attractor_side") };
  private static final @Nonnull TextureSupplier[] activeTextures = { TextureRegistry.registerTexture("blocks/obelisk_bottom"),
      TextureRegistry.registerTexture("blocks/block_soul_machine_top"), TextureRegistry.registerTexture("blocks/block_attractor_side_on"),
      TextureRegistry.registerTexture("blocks/block_attractor_side_on"), TextureRegistry.registerTexture("blocks/block_attractor_side_on"),
      TextureRegistry.registerTexture("blocks/block_attractor_side_on") };

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onModelRegister(ModelRegistryEvent event) {
    Block block;

    block = block_experience_obelisk.getBlock();
    if (block != null) {
      ObeliskSpecialRenderer<TileExperienceObelisk> eor = new ObeliskSpecialRenderer<TileExperienceObelisk>(new ItemStack(ModObject.itemXpTransfer.getItemNN()),
          block);
      registerRenderer(block, TileExperienceObelisk.class, eor);

    }
    block = block_attractor_obelisk.getBlock();
    if (block != null) {
      ObeliskSpecialRenderer<TileAttractor> eor = new ObeliskSpecialRenderer<TileAttractor>(Material.ATTRACTOR_CRYSTAL.getStack(), block);
      registerRenderer(block, TileAttractor.class, eor);
    }

    block = block_aversion_obelisk.getBlock();
    if (block != null) {
      AversionObeliskRenderer eor = new AversionObeliskRenderer();
      registerRenderer(block, TileAversionObelisk.class, eor);
    }

    block = block_relocator_obelisk.getBlock();
    if (block != null) {
      RelocatorObeliskRenderer eor = new RelocatorObeliskRenderer();
      registerRenderer(block, TileRelocatorObelisk.class, eor);
    }

    block = block_weather_obelisk.getBlock();
    if (block != null) {
      ObeliskSpecialRenderer<TileWeatherObelisk> eor = new WeatherObeliskSpecialRenderer(new ItemStack(Items.FIREWORKS));
      registerRenderer(block, TileWeatherObelisk.class, eor);
    }

    block = block_inhibitor_obelisk.getBlock();
    if (block != null) {
      ObeliskSpecialRenderer<TileInhibitorObelisk> eor = new ObeliskSpecialRenderer<TileInhibitorObelisk>(new ItemStack(Items.ENDER_PEARL), block);
      registerRenderer(block, TileInhibitorObelisk.class, eor);
    }
  }

  private static <T extends AbstractMachineEntity> void registerRenderer(@Nonnull Block block, Class<T> tileClass,
      TileEntitySpecialRenderer<? super T> specialRenderer) {
    ClientRegistry.bindTileEntitySpecialRenderer(tileClass, specialRenderer);
    ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(block), 0, tileClass);
  }

  public static @Nonnull TextureSupplier[] getTextures() {
    return textures;
  }

  public static @Nonnull TextureSupplier[] getActiveTextures() {
    return activeTextures;
  }

}
