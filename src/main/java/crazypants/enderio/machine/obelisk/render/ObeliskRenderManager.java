package crazypants.enderio.machine.obelisk.render;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.obelisk.AbstractBlockObelisk;
import crazypants.enderio.machine.obelisk.attractor.TileAttractor;
import crazypants.enderio.machine.obelisk.aversion.AversionObeliskRenderer;
import crazypants.enderio.machine.obelisk.aversion.TileAversionObelisk;
import crazypants.enderio.machine.obelisk.inhibitor.TileInhibitorObelisk;
import crazypants.enderio.machine.obelisk.relocator.RelocatorObeliskRenderer;
import crazypants.enderio.machine.obelisk.relocator.TileRelocatorObelisk;
import crazypants.enderio.machine.obelisk.weather.TileWeatherObelisk;
import crazypants.enderio.machine.obelisk.weather.WeatherObeliskSpecialRenderer;
import crazypants.enderio.machine.obelisk.xp.TileExperienceObelisk;
import crazypants.enderio.material.Material;
import crazypants.enderio.render.registry.TextureRegistry;
import crazypants.enderio.render.registry.TextureRegistry.TextureSupplier;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ObeliskRenderManager {

  public static final ObeliskRenderManager INSTANCE = new ObeliskRenderManager();

  public static ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("enderio:obelisk");

  private TextureSupplier[] textures = { TextureRegistry.registerTexture("blocks/obeliskBottom"),
      TextureRegistry.registerTexture("blocks/blockSoulMachineTop"), TextureRegistry.registerTexture("blocks/blockAttractorSide"),
      TextureRegistry.registerTexture("blocks/blockAttractorSide"), TextureRegistry.registerTexture("blocks/blockAttractorSide"),
      TextureRegistry.registerTexture("blocks/blockAttractorSide") };
  private TextureSupplier[] activeTextures = { TextureRegistry.registerTexture("blocks/obeliskBottom"),
      TextureRegistry.registerTexture("blocks/blockSoulMachineTop"), TextureRegistry.registerTexture("blocks/blockAttractorSideOn"),
      TextureRegistry.registerTexture("blocks/blockAttractorSideOn"), TextureRegistry.registerTexture("blocks/blockAttractorSideOn"),
      TextureRegistry.registerTexture("blocks/blockAttractorSideOn") };

  private ObeliskRenderManager() {
  }

  public void registerRenderers() {
    AbstractBlockObelisk<? extends AbstractMachineEntity> block;

    block = EnderIO.blockExperianceOblisk;
    if (block != null) {
      ObeliskSpecialRenderer<TileExperienceObelisk> eor = new ObeliskSpecialRenderer<TileExperienceObelisk>(new ItemStack(EnderIO.itemXpTransfer),
          EnderIO.blockExperianceOblisk);
      registerRenderer(block, TileExperienceObelisk.class, eor);

    }
    block = EnderIO.blockAttractor;
    if (block != null) {
      ObeliskSpecialRenderer<TileAttractor> eor = new ObeliskSpecialRenderer<TileAttractor>(new ItemStack(EnderIO.itemMaterial, 1,
          Material.ATTRACTOR_CRYSTAL.ordinal()), EnderIO.blockAttractor);
      registerRenderer(block, TileAttractor.class, eor);
    }

    block = EnderIO.blockSpawnGuard;
    if (block != null) {
      AversionObeliskRenderer eor = new AversionObeliskRenderer();
      registerRenderer(block, TileAversionObelisk.class, eor);
    }

    block = EnderIO.blockSpawnRelocator;
    if (block != null) {
      RelocatorObeliskRenderer eor = new RelocatorObeliskRenderer();
      registerRenderer(block, TileRelocatorObelisk.class, eor);
    }

    block = EnderIO.blockWeatherObelisk;
    if (block != null) {
      ObeliskSpecialRenderer<TileWeatherObelisk> eor = new WeatherObeliskSpecialRenderer(new ItemStack(Items.FIREWORKS));
      registerRenderer(block, TileWeatherObelisk.class, eor);
    }

    block = EnderIO.blockInhibitorObelisk;
    if (block != null) {
      ObeliskSpecialRenderer<TileInhibitorObelisk> eor = new ObeliskSpecialRenderer<TileInhibitorObelisk>(new ItemStack(Items.ENDER_PEARL),
          EnderIO.blockInhibitorObelisk);
      registerRenderer(block, TileInhibitorObelisk.class, eor);
    }
  }

  private <T extends AbstractMachineEntity> void registerRenderer(AbstractBlockObelisk<? extends AbstractMachineEntity> block, Class<T> tileClass,
      TileEntitySpecialRenderer<? super T> specialRenderer) {
    ClientRegistry.bindTileEntitySpecialRenderer(tileClass, specialRenderer);
    ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(block), 0, tileClass);
  }

  public TextureSupplier[] getTextures() {
    return textures;
  }

  public TextureSupplier[] getActiveTextures() {
    return activeTextures;
  }

}
