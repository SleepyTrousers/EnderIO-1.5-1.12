package crazypants.enderio.machine.obelisk.render;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.obelisk.xp.TileExperienceObelisk;
import crazypants.enderio.render.TextureRegistry;
import crazypants.enderio.render.TextureRegistry.TextureSupplier;
import crazypants.util.ClientUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ObeliskRenderManager {

  public static final ObeliskRenderManager INSTANCE = new ObeliskRenderManager();
  
  public static ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("enderio:obelisk");
  
  private TextureSupplier[] textures;
  private TextureSupplier[] activeTextures; 
  
  private ObeliskRenderManager() {    
  }
  
  public void registerRenderers() {

    textures = new TextureSupplier[] {
        TextureRegistry.registerTexture("blocks/obeliskBottom"),
        TextureRegistry.registerTexture("blocks/blockSoulMachineTop"),
        TextureRegistry.registerTexture("blocks/blockAttractorSide"),
        TextureRegistry.registerTexture("blocks/blockAttractorSide"),
        TextureRegistry.registerTexture("blocks/blockAttractorSide"),
        TextureRegistry.registerTexture("blocks/blockAttractorSide")        
    };
    activeTextures = new TextureSupplier[] {
        TextureRegistry.registerTexture("blocks/obeliskBottom"),
        TextureRegistry.registerTexture("blocks/blockSoulMachineTop"),
        TextureRegistry.registerTexture("blocks/blockAttractorSideOn"),
        TextureRegistry.registerTexture("blocks/blockAttractorSideOn"),
        TextureRegistry.registerTexture("blocks/blockAttractorSideOn"),
        TextureRegistry.registerTexture("blocks/blockAttractorSideOn")        
    };
    
    
    if(EnderIO.blockExperianceOblisk != null) {
      ObeliskSpecialRenderer<TileExperienceObelisk> eor = new ObeliskSpecialRenderer<TileExperienceObelisk>(EnderIO.blockExperianceOblisk, new ItemStack(EnderIO.itemXpTransfer));
      ClientRegistry.bindTileEntitySpecialRenderer(TileExperienceObelisk.class, eor);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(EnderIO.blockExperianceOblisk), 0, TileExperienceObelisk.class);
      ClientUtil.registerRenderer(Item.getItemFromBlock(EnderIO.blockExperianceOblisk), ModObject.blockExperienceObelisk.unlocalisedName);
    }
    StateMapperBase ignoreState = new StateMapperBase() {
      @Override
      protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
        return MODEL_LOCATION;
      }
    };
    if(EnderIO.blockExperianceOblisk != null) {
      ModelLoader.setCustomStateMapper(EnderIO.blockExperianceOblisk, ignoreState);
    }
    MinecraftForge.EVENT_BUS.register(this);
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
