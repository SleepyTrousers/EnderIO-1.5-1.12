package crazypants.enderio.machine.obelisk.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;
import crazypants.enderio.machine.obelisk.attractor.TileAttractor;
import crazypants.enderio.machine.obelisk.aversion.AversionObeliskRenderer;
import crazypants.enderio.machine.obelisk.aversion.TileAversionObelisk;
import crazypants.enderio.machine.obelisk.inhibitor.TileInhibitorObelisk;
import crazypants.enderio.machine.obelisk.weather.TileWeatherObelisk;
import crazypants.enderio.machine.obelisk.weather.WeatherObeliskSpecialRenderer;
import crazypants.enderio.machine.obelisk.xp.TileExperienceObelisk;
import crazypants.enderio.material.Material;
import crazypants.enderio.render.TextureRegistry;
import crazypants.enderio.render.TextureRegistry.TextureSupplier;
import crazypants.util.ClientUtil;

@SideOnly(Side.CLIENT)
public class ObeliskRenderManager {

  public static final ObeliskRenderManager INSTANCE = new ObeliskRenderManager();

  public static ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("enderio:obelisk");

  private TextureSupplier[] textures;
  private TextureSupplier[] activeTextures;

  private ObeliskRenderManager() {
  }

  public void registerRenderers() {

    textures = new TextureSupplier[] { TextureRegistry.registerTexture("blocks/obeliskBottom"), TextureRegistry.registerTexture("blocks/blockSoulMachineTop"),
        TextureRegistry.registerTexture("blocks/blockAttractorSide"), TextureRegistry.registerTexture("blocks/blockAttractorSide"),
        TextureRegistry.registerTexture("blocks/blockAttractorSide"), TextureRegistry.registerTexture("blocks/blockAttractorSide") };
    activeTextures = new TextureSupplier[] { TextureRegistry.registerTexture("blocks/obeliskBottom"),
        TextureRegistry.registerTexture("blocks/blockSoulMachineTop"), TextureRegistry.registerTexture("blocks/blockAttractorSideOn"),
        TextureRegistry.registerTexture("blocks/blockAttractorSideOn"), TextureRegistry.registerTexture("blocks/blockAttractorSideOn"),
        TextureRegistry.registerTexture("blocks/blockAttractorSideOn") };

    StateMapperBase ignoreState = new StateMapperBase() {
      @Override
      protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
        return MODEL_LOCATION;
      }
    };

    BlockObeliskAbstract<? extends AbstractMachineEntity> block;

    block = EnderIO.blockExperianceOblisk;
    if (block != null) {
      ObeliskSpecialRenderer<TileExperienceObelisk> eor = new ObeliskSpecialRenderer<TileExperienceObelisk>(block, new ItemStack(EnderIO.itemXpTransfer));
      registerRenderer(block, TileExperienceObelisk.class, eor, ignoreState);

    }
    block = EnderIO.blockAttractor;
    if (block != null) {
      ObeliskSpecialRenderer<TileAttractor> eor = new ObeliskSpecialRenderer<TileAttractor>(block,
          new ItemStack(EnderIO.itemMaterial, 1, Material.ATTRACTOR_CRYSTAL.ordinal()));
      registerRenderer(block, TileAttractor.class, eor, ignoreState);
    }

    block = EnderIO.blockSpawnGuard;
    if (block != null) {
      AversionObeliskRenderer eor = new AversionObeliskRenderer();
      registerRenderer(block, TileAversionObelisk.class, eor, ignoreState);
    }

    block = EnderIO.blockWeatherObelisk;
    if (block != null) {
      ObeliskSpecialRenderer<TileWeatherObelisk> eor = new WeatherObeliskSpecialRenderer(new ItemStack(Items.fireworks));
      registerRenderer(block, TileWeatherObelisk.class, eor, ignoreState);
    }
    
    block = EnderIO.blockInhibitorObelisk;
    if (block != null) {
      ObeliskSpecialRenderer<TileInhibitorObelisk> eor = new ObeliskSpecialRenderer<TileInhibitorObelisk>(block, new ItemStack(Items.ender_pearl));
      registerRenderer(block, TileInhibitorObelisk.class, eor, ignoreState);
    }

    MinecraftForge.EVENT_BUS.register(this);
  }

  private <T extends AbstractMachineEntity> void registerRenderer(BlockObeliskAbstract<? extends AbstractMachineEntity> block, Class<T> tileClass,
      TileEntitySpecialRenderer<? super T> specialRenderer, IStateMapper ignoreState) {
    ClientRegistry.bindTileEntitySpecialRenderer(tileClass, specialRenderer);
    ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(block), 0, tileClass);
    ClientUtil.registerRenderer(Item.getItemFromBlock(block), block.getName());
    ModelLoader.setCustomStateMapper(block, ignoreState);
  }

  public TextureSupplier[] getTextures() {
    return textures;
  }

  public TextureSupplier[] getActiveTextures() {
    return activeTextures;
  }

  @SubscribeEvent
  public void onModelBakeEvent(ModelBakeEvent event) {
    IBakedModel defaultBakedModel = event.modelRegistry.getObject(MODEL_LOCATION);
    ObeliskBakedModel model = new ObeliskBakedModel(defaultBakedModel);
    event.modelRegistry.putObject(MODEL_LOCATION, model);
  }

  @SubscribeEvent
  public void onIconLoad(TextureStitchEvent.Pre event) {
    ObeliskModelQuads.INSTANCE.invalidate();
    ObeliskModelQuads.INSTANCE_ACTIVE.invalidate();
  }

}
