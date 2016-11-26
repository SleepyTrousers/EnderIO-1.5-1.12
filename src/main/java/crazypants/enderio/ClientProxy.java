package crazypants.enderio;

import java.util.ArrayList;
import java.util.List;

import com.enderio.core.client.EnderCoreModConflictException;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.conduit.render.ConduitBundleRenderManager;
import crazypants.enderio.config.Config;
import crazypants.enderio.diagnostics.DebugCommand;
import crazypants.enderio.diagnostics.EnderIOCrashCallable;
import crazypants.enderio.fluid.Buckets;
import crazypants.enderio.gui.TooltipHandlerBurnTime;
import crazypants.enderio.gui.TooltipHandlerFluid;
import crazypants.enderio.gui.TooltipHandlerGrinding;
import crazypants.enderio.integration.jei.JeiAccessor;
import crazypants.enderio.item.ConduitProbeOverlayRenderer;
import crazypants.enderio.item.KeyTracker;
import crazypants.enderio.item.ToolTickHandler;
import crazypants.enderio.item.YetaWrenchOverlayRenderer;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.SoundDetector;
import crazypants.enderio.item.darksteel.upgrade.UpgradeRenderDispatcher;
import crazypants.enderio.machine.capbank.network.ClientNetworkManager;
import crazypants.enderio.machine.obelisk.render.ObeliskRenderManager;
import crazypants.enderio.machine.ranged.MarkerParticle;
import crazypants.enderio.paint.YetaUtil;
import crazypants.enderio.paint.render.PaintRegistry;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.enderio.render.IHaveTESR;
import crazypants.enderio.render.registry.ItemModelRegistry;
import crazypants.enderio.render.registry.SmartModelAttacher;
import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.teleport.telepad.TeleportEntityRenderHandler;
import crazypants.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

  @Override
  public World getClientWorld() {
    return FMLClientHandler.instance().getClient().theWorld;
  }

  @Override
  public boolean isAnEiInstalled() {
    return JeiAccessor.isJeiRuntimeAvailable();
  }

  @Override
  public EntityPlayer getClientPlayer() {
    return Minecraft.getMinecraft().thePlayer;
  }

  @Override
  public void loadIcons() {
    SmartModelAttacher.create();
    PaintRegistry.create();
  }

  @Override
  public void preInit(FMLPreInitializationEvent event) {
    super.preInit(event);

    SpecialTooltipHandler tt = SpecialTooltipHandler.INSTANCE;
    tt.addCallback(new TooltipHandlerGrinding());
    tt.addCallback(new TooltipHandlerBurnTime());
    if (Config.addFuelTooltipsToAllFluidContainers) {
      tt.addCallback(new TooltipHandlerFluid());
    }

    //conduits
    ConduitBundleRenderManager.instance.registerRenderers();

    // Fluids
    EnderIO.fluids.registerRenderers();

    // Items of blocks that use smart rendering
    SmartModelAttacher.registerBlockItemModels();

    /*
     * Most blocks register themselves with the SmartModelAttacher which will also handle their items. Those that don't need to implement IHaveRenderers and
     * have their items handled here.
     * 
     * Items that do _not_ belong to a block are handled here by either having the item implement IHaveRenderers or by registering the default renderer.
     */
    for (ModObject mo : ModObject.values()) {
      final Block block = mo.getBlock();
      if (block instanceof IHaveRenderers) {
        ((IHaveRenderers) block).registerRenderers();
      } else if (block == null) {
        final Item item = mo.getItem();
        if (item instanceof IHaveRenderers) {
          ((IHaveRenderers) item).registerRenderers();
        } else if (item != null) {
          ClientUtil.registerRenderer(item, mo.getUnlocalisedName());
        }
      }
      if (block instanceof IHaveTESR) {
        ((IHaveTESR) block).bindTileEntitySpecialRenderer();
      }
    }

    ObeliskRenderManager.INSTANCE.registerRenderers();

    // Overlays
    new YetaWrenchOverlayRenderer();
    new ConduitProbeOverlayRenderer();

    // Items
    DarkSteelItems.onClientPreInit();
    Buckets.registerRenderers();

    // Item Models
    ItemModelRegistry.create();
    // ItemModelRegistry.registerRotating("enderCrystal", 2);

    // Listeners
    if (Config.useSneakMouseWheelYetaWrench) {
      ToolTickHandler th = new ToolTickHandler();
      MinecraftForge.EVENT_BUS.register(th);
    }
    MinecraftForge.EVENT_BUS.register(TravelController.instance);
    MinecraftForge.EVENT_BUS.register(KeyTracker.instance);
    MinecraftForge.EVENT_BUS.register(SoundDetector.instance);
    MinecraftForge.EVENT_BUS.register(UpgradeRenderDispatcher.instance);
    MinecraftForge.EVENT_BUS.register(new TeleportEntityRenderHandler());
  }

  @Override
  public void init() {
    super.init();
    SmartModelAttacher.registerColoredBlocksAndItems();
    MinecraftForge.EVENT_BUS.register(ClientNetworkManager.getInstance());
  }

  @Override
  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    if (entityPlayer instanceof EntityPlayerMP) {
      return ((EntityPlayerMP) entityPlayer).interactionManager.getBlockReachDistance();
    }
    return super.getReachDistanceForPlayer(entityPlayer);
  }

  @Override
  public void setInstantConfusionOnPlayer(EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration, 1, true, true));
    Minecraft.getMinecraft().thePlayer.timeInPortal = 1;
  }

  @Override
  public long getTickCount() {
    return clientTickCount;
  }

  @Override
  protected void onClientTick() {
    if (!Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().theWorld != null) {
      ++clientTickCount;
      YetaUtil.onClientTick();
    }
  }

  @Override
  public void markBlock(World worldObj, BlockPos pos, Vector4f color) {
    Minecraft.getMinecraft().effectRenderer.addEffect(new MarkerParticle(worldObj, pos, color));
  }

  @Override
  protected void registerCommands() {
    ClientCommandHandler.instance.registerCommand(DebugCommand.CLIENT);
  }

  @Override
  public boolean isDedicatedServer() {
    return false;
  }

  @Override
  public CreativeTabs getCreativeTab(ItemStack stack) {
    return stack == null || stack.getItem() == null ? null : stack.getItem().getCreativeTab();
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    itemIn.getSubItems(itemIn, tab, subItems);
  }

  @Override
  public void stopWithErrorScreen(String... message) {
    EnderIOCrashCallable.registerStopScreenMessage(message);
    List<String> lines = new ArrayList<String>();
    for (String string : message) {
      Log.error(string);
      while (string.length() > 71) {
        lines.add(string.substring(0, 70));
        string = string.substring(70, string.length());
      }
      lines.add(string);
    }
    throw new EnderCoreModConflictException(lines.toArray(new String[0]));
  }

}
