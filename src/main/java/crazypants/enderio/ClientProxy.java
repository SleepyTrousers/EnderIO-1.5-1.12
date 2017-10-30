package crazypants.enderio;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.EnderCoreModConflictException;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.block.coldfire.ColdFireStateMapper;
import crazypants.enderio.block.lever.LeverStateMapper;
import crazypants.enderio.config.Config;
import crazypants.enderio.diagnostics.EnderIOCrashCallable;
import crazypants.enderio.gui.IoConfigRenderer;
import crazypants.enderio.gui.TooltipHandlerBurnTime;
import crazypants.enderio.gui.TooltipHandlerFluid;
import crazypants.enderio.gui.TooltipHandlerGrinding;
import crazypants.enderio.handler.KeyTracker;
import crazypants.enderio.handler.darksteel.UpgradeRenderManager;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.init.ModObjectRegistry;
import crazypants.enderio.integration.jei.JeiAccessor;
import crazypants.enderio.item.conduitprobe.ConduitProbeOverlayRenderer;
import crazypants.enderio.item.conduitprobe.ToolTickHandler;
import crazypants.enderio.item.darksteel.upgrade.sound.SoundDetector;
import crazypants.enderio.item.yetawrench.YetaWrenchOverlayRenderer;
import crazypants.enderio.material.glass.EnderIOGlassesStateMapper;
import crazypants.enderio.paint.PaintTooltipUtil;
import crazypants.enderio.paint.YetaUtil;
import crazypants.enderio.paint.render.PaintRegistry;
import crazypants.enderio.render.IDefaultRenderers;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.enderio.render.IHaveTESR;
import crazypants.enderio.render.ranged.MarkerParticle;
import crazypants.enderio.render.registry.ItemModelRegistry;
import crazypants.enderio.render.registry.SmartModelAttacher;
import crazypants.enderio.teleport.TravelController;
import crazypants.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class ClientProxy extends CommonProxy {

  @Override
  public World getClientWorld() {
    return FMLClientHandler.instance().getClient().world;
  }

  @Override
  public boolean isAnEiInstalled() {
    return JeiAccessor.isJeiRuntimeAvailable();
  }

  @Override
  public EntityPlayer getClientPlayer() {
    return Minecraft.getMinecraft().player;
  }

  @Override
  public void loadIcons() {
    SmartModelAttacher.create();
    PaintRegistry.create();
  }

  @SubscribeEvent
  public static void onModelRegistryEvent(@Nonnull ModelRegistryEvent event) {

    // Custom state mappers
    EnderIOGlassesStateMapper.create();
    // TODO 1.11 ConduitBundleStateMapper.create();
    ColdFireStateMapper.create();
    LeverStateMapper.create();

    // Items of blocks that use smart rendering
    SmartModelAttacher.registerBlockItemModels();

    /*
     * Most blocks register themselves with the SmartModelAttacher which will also handle their items. Those that don't need to implement IHaveRenderers and
     * have their items handled here.
     * 
     * Items that do _not_ belong to a block are handled here by either having the item implement IHaveRenderers or by registering the default renderer.
     */
    for (IModObject mo : ModObjectRegistry.getObjects()) {
      final Block block = mo.getBlock();
      if (block instanceof IHaveRenderers) {
        ((IHaveRenderers) block).registerRenderers(mo);
      } else if (block instanceof IDefaultRenderers) {
        ClientUtil.registerDefaultItemRenderer(mo);
      } else if (block == null || block == Blocks.AIR) {
        final Item item = mo.getItem();
        if (item instanceof IHaveRenderers) {
          ((IHaveRenderers) item).registerRenderers(mo);
        } else if (item != null && item != Items.AIR) {
          ClientUtil.registerRenderer(item, mo.getUnlocalisedName());
        }
      }
      if (block instanceof IHaveTESR) {
        ((IHaveTESR) block).bindTileEntitySpecialRenderer();
      }
    }

    // TODO 1.11 ObeliskRenderManager.INSTANCE.registerRenderers();

  }

  @Override
  public void init(@Nonnull FMLPreInitializationEvent event) {
    super.init(event);

    SpecialTooltipHandler.addCallback(new TooltipHandlerGrinding());
    SpecialTooltipHandler.addCallback(new TooltipHandlerBurnTime());
    if (Config.addFuelTooltipsToAllFluidContainers) {
      SpecialTooltipHandler.addCallback(new TooltipHandlerFluid());
    }
    PaintTooltipUtil.create();

    IoConfigRenderer.init(event);

    // conduits
    // TODO 1.11 ConduitBundleRenderManager.instance.init(event);

    // Overlays
    MinecraftForge.EVENT_BUS.register(new YetaWrenchOverlayRenderer());
    MinecraftForge.EVENT_BUS.register(new ConduitProbeOverlayRenderer());

    // Item Models
    ItemModelRegistry.create();
    // ItemModelRegistry.registerRotating("enderCrystal", 2);

    // Listeners
    if (Config.useSneakMouseWheelYetaWrench) {
      ToolTickHandler.init(event);
    }
    MinecraftForge.EVENT_BUS.register(TravelController.instance);
    MinecraftForge.EVENT_BUS.register(KeyTracker.instance);
    MinecraftForge.EVENT_BUS.register(SoundDetector.instance);
    UpgradeRenderManager.init(event);
    // TODO 1.11 MinecraftForge.EVENT_BUS.register(new TeleportEntityRenderHandler());
  }

  @Override
  public void init(@Nonnull FMLInitializationEvent event) {
    super.init(event);
    SmartModelAttacher.registerColoredBlocksAndItems();
    // TODO 1.11 MinecraftForge.EVENT_BUS.register(ClientNetworkManager.getInstance());
  }

  @Override
  public void init(@Nonnull FMLPostInitializationEvent event) {
    super.init(event);
    // TODO 1.11 ConduitBundleRenderManager.instance.init(event);
  }

  @Override
  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    if (entityPlayer instanceof EntityPlayerMP) {
      return ((EntityPlayerMP) entityPlayer).interactionManager.getBlockReachDistance();
    }
    return super.getReachDistanceForPlayer(entityPlayer);
  }

  @Override
  public void setInstantConfusionOnPlayer(@Nonnull EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration, 1, true, true));
    Minecraft.getMinecraft().player.timeInPortal = 1;
  }

  @Override
  public long getTickCount() {
    return clientTickCount;
  }

  @Override
  protected void onClientTick() {
    if (!Minecraft.getMinecraft().isGamePaused()) {
      ++clientTickCount;
      YetaUtil.onClientTick();
    }
  }

  @Override
  public void markBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vector4f color) {
    Minecraft.getMinecraft().effectRenderer.addEffect(new MarkerParticle(world, pos, color));
  }

  @Override
  protected void registerCommands() {
  }

  @Override
  public boolean isDedicatedServer() {
    return false;
  }

  @Override
  public CreativeTabs getCreativeTab(@Nonnull ItemStack stack) {
    return stack.getItem().getCreativeTab();
  }

  @SuppressWarnings("null")
  @Override
  public void getSubItems(@Nonnull Item itemIn, @Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
    itemIn.getSubItems(itemIn, tab, subItems);
  }

  @SuppressWarnings("null")
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
