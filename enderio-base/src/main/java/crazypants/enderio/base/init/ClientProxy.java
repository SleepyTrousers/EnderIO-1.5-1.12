package crazypants.enderio.base.init;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.enderio.core.EnderCore;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.diagnostics.EnderIOCrashCallable;
import crazypants.enderio.base.render.ICustomSubItems;
import crazypants.enderio.base.render.IDefaultRenderers;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.ranged.MarkerParticle;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class ClientProxy extends CommonProxy {

  @Override
  public World getClientWorld() {
    return Minecraft.getMinecraft().world;
  }

  @Override
  public EntityPlayer getClientPlayer() {
    return Minecraft.getMinecraft().player;
  }

  @SubscribeEvent
  public static void onModelRegistryEvent(@Nonnull ModelRegistryEvent event) {
    /*
     * Most blocks register themselves with the SmartModelAttacher which will also handle their items. Those that don't need to implement IHaveRenderers and
     * have their items handled here.
     * 
     * Items that do _not_ belong to a block are handled here by either having the item implement IHaveRenderers or by registering the default renderer.
     */
    for (IModObject mo : ModObjectRegistry.getRegistry()) {
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
  public void setInstantConfusionOnPlayer(@Nonnull EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration, 1, true, true));
    Minecraft.getMinecraft().player.timeInPortal = 1;
  }

  @Override
  public long getTickCount() {
    return TickTimer.getClientTickCount();
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
      if (string.length() > 71) {
        lines.addAll(splitString(string, 71));
      } else {
        lines.add(string);
      }
    }
    EnderCore.proxy.throwModCompatibilityError(lines.toArray(new String[lines.size()]));
  }

  private static List<String> splitString(String msg, int lineSize) {
    List<String> res = new ArrayList<>();

    Pattern p = Pattern.compile("\\b.{1," + (lineSize - 1) + "}\\b\\W?");
    Matcher m = p.matcher(msg);

    while (m.find()) {
      res.add(m.group());
    }
    return res;
  }

  @Override
  public boolean isGamePaused() {
    return Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().isGamePaused();
  }

}
