package crazypants.enderio.base.init;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.EnderCore;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.block.coldfire.ColdFireStateMapper;
import crazypants.enderio.base.block.lever.LeverStateMapper;
import crazypants.enderio.base.diagnostics.EnderIOCrashCallable;
import crazypants.enderio.base.gui.IoConfigRenderer;
import crazypants.enderio.base.gui.tooltip.TooltipHandlerBurnTime;
import crazypants.enderio.base.gui.tooltip.TooltipHandlerFluid;
import crazypants.enderio.base.gui.tooltip.TooltipHandlerGrinding;
import crazypants.enderio.base.handler.KeyTracker;
import crazypants.enderio.base.integration.jei.JeiAccessor;
import crazypants.enderio.base.item.conduitprobe.ConduitProbeOverlayRenderer;
import crazypants.enderio.base.item.darksteel.upgrade.sound.SoundDetector;
import crazypants.enderio.base.item.yetawrench.YetaWrenchOverlayRenderer;
import crazypants.enderio.base.material.glass.EnderIOGlassesStateMapper;
import crazypants.enderio.base.paint.PaintTooltipUtil;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.render.ICustomSubItems;
import crazypants.enderio.base.render.IDefaultRenderers;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.ranged.MarkerParticle;
import crazypants.enderio.base.render.registry.ItemModelRegistry;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.base.teleport.TravelController;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
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

  @SubscribeEvent
  public static void onModelRegistryEvent(@Nonnull ModelRegistryEvent event) {

    // Custom state mappers
    EnderIOGlassesStateMapper.create();
    ColdFireStateMapper.create();
    LeverStateMapper.create();
    final StateMap doorMapper = (new StateMap.Builder()).ignore(new IProperty[] { BlockDoor.POWERED }).build();
    ModelLoader.setCustomStateMapper(ModObject.blockDarkSteelDoor.getBlockNN(), doorMapper);
    ModelLoader.setCustomStateMapper(ModObject.blockPaintedDarkSteelDoor.getBlockNN(), doorMapper);
    ModelLoader.setCustomStateMapper(ModObject.blockPaintedIronDoor.getBlockNN(), doorMapper);
    ModelLoader.setCustomStateMapper(ModObject.blockPaintedWoodenDoor.getBlockNN(), doorMapper);

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
      if (block instanceof ICustomSubItems) {
        // NOP, handled by SmartModelAttacher
      } else if (block instanceof IHaveRenderers) {
        ((IHaveRenderers) block).registerRenderers(mo);
      } else if (block instanceof IDefaultRenderers) {
        ClientUtil.registerDefaultItemRenderer(mo);
      } else if (block == null || block == Blocks.AIR) {
        final Item item = mo.getItem();
        if (item instanceof ICustomSubItems) {
          // NOP, handled by SmartModelAttacher
        } else if (item instanceof IHaveRenderers) {
          ((IHaveRenderers) item).registerRenderers(mo);
        } else if (item != null && item != Items.AIR) {
          ClientUtil.registerDefaultItemRenderer(mo);
        }
      }
      if (block instanceof IHaveTESR) {
        ((IHaveTESR) block).bindTileEntitySpecialRenderer();
      }
    }
  }

  @Override
  public void init(@Nonnull FMLPreInitializationEvent event) {
    super.init(event);

    SpecialTooltipHandler.addCallback(new TooltipHandlerGrinding());
    SpecialTooltipHandler.addCallback(new TooltipHandlerBurnTime());
    SpecialTooltipHandler.addCallback(new TooltipHandlerFluid());
    PaintTooltipUtil.create();

    IoConfigRenderer.init(event);

    // Overlays
    MinecraftForge.EVENT_BUS.register(new YetaWrenchOverlayRenderer());
    MinecraftForge.EVENT_BUS.register(new ConduitProbeOverlayRenderer());

    // Item Models
    ItemModelRegistry.create();
    // ItemModelRegistry.registerRotating("enderCrystal", 2);

    // Listeners
    MinecraftForge.EVENT_BUS.register(TravelController.instance);
    MinecraftForge.EVENT_BUS.register(KeyTracker.instance);
    MinecraftForge.EVENT_BUS.register(SoundDetector.instance);
  }

  @Override
  public void init(@Nonnull FMLInitializationEvent event) {
    super.init(event);
    SmartModelAttacher.registerColoredBlocksAndItems();
  }

  @Override
  public void init(@Nonnull FMLPostInitializationEvent event) {
    super.init(event);
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
    EnderCore.proxy.throwModCompatibilityError(lines.toArray(new String[lines.size()]));
  }

}
