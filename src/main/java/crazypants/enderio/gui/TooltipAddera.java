package crazypants.enderio.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Keyboard;

import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.Lang;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.config.Config;
import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.fluid.IFluidCoolant;
import crazypants.enderio.fluid.IFluidFuel;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.crusher.CrusherRecipeManager;
import crazypants.enderio.machine.crusher.IGrindingMultiplier;
import crazypants.enderio.machine.generator.stirling.StirlingGeneratorContainer;
import crazypants.enderio.machine.generator.stirling.TileEntityStirlingGenerator;
import crazypants.enderio.machine.power.PowerDisplayUtil;

public class TooltipAddera {

  public static final TooltipAddera instance = new TooltipAddera();

  //TODO: Need to decouple this stuff with a callback
  private final BallTipProvider btp = new BallTipProvider();

  static {
    MinecraftForge.EVENT_BUS.register(instance);
  }

  @SubscribeEvent
  public void addTooltip(ItemTooltipEvent evt) {
    if(evt.itemStack == null) {
      return;
    }
    if(Config.addFurnaceFuelTootip) {
      if(!addStirlinGeneratorTooltip(evt)) {
        int time = TileEntityFurnace.getItemBurnTime(evt.itemStack);
        if(time > 0) {
          evt.toolTip.add(Lang.localize("tooltip.burntime") + " " + time);
        }
      }
    }

    if(Config.addDurabilityTootip) {
      addDurabilityTooltip(evt.toolTip, evt.itemStack);
    }

    if(evt.itemStack.getItem() instanceof IAdvancedTooltipProvider) {
      IAdvancedTooltipProvider ttp = (IAdvancedTooltipProvider) evt.itemStack.getItem();
      addInformation(ttp, evt.itemStack, evt.entityPlayer, evt.toolTip, false);
      return;
    } else if(evt.itemStack.getItem() instanceof IResourceTooltipProvider) {
      addInformation((IResourceTooltipProvider) evt.itemStack.getItem(), evt);
      return;
    }

    Block blk = Block.getBlockFromItem(evt.itemStack.getItem());
    if(blk instanceof IAdvancedTooltipProvider) {
      addInformation((IAdvancedTooltipProvider) blk, evt.itemStack, evt.entityPlayer, evt.toolTip, false);
      return;
    } else if(blk instanceof IResourceTooltipProvider) {
      addInformation((IResourceTooltipProvider) blk, evt);
      return;
    }

    if(Config.addFuelTooltipsToAllFluidContainers) {
      addTooltipForFluid(evt.toolTip, evt.itemStack);
    }

    IGrindingMultiplier gb = CrusherRecipeManager.getInstance().getGrindballFromStack(evt.itemStack);
    if(gb != null) {
      btp.ball = gb;
      addInformation(btp, evt.itemStack, evt.entityPlayer, evt.toolTip, false);
    }

    if(Config.addRegisterdNameTooltip) {
      UniqueIdentifier uid;
      Block block = Block.getBlockFromItem(evt.itemStack.getItem());
      if(block != null && block != Blocks.air) {
        uid = GameRegistry.findUniqueIdentifierFor(block);
      } else {
        uid = GameRegistry.findUniqueIdentifierFor(evt.itemStack.getItem());
      }
      if(uid != null) {
        evt.toolTip.add(EnumChatFormatting.AQUA + Lang.localize("tooltip.uid") + " " + uid.toString() + " " + Lang.localize("tooltip.meta") + " " + evt.itemStack.getItemDamage());
      }
    }

    if(Config.addOreDictionaryTooltips) {
      int[] ids = OreDictionary.getOreIDs(evt.itemStack);
      if(ids != null && ids.length > 0) {
        if(ids.length == 1) {
          evt.toolTip.add(EnumChatFormatting.AQUA + Lang.localize("tooltip.oredict") + " " + OreDictionary.getOreName(ids[0]));
        } else {
          evt.toolTip.add(EnumChatFormatting.AQUA + Lang.localize("tooltip.oredict"));
          for (int id : ids) {
            String name = OreDictionary.getOreName(id);
            evt.toolTip.add(EnumChatFormatting.AQUA + "  " + name);
          }
        }
      }

    }
  }

  private static boolean addStirlinGeneratorTooltip(ItemTooltipEvent evt) {
    if(evt.entityPlayer != null && evt.entityPlayer.openContainer instanceof StirlingGeneratorContainer) {
      AbstractMachineEntity te = ((StirlingGeneratorContainer)evt.entityPlayer.openContainer).getTileEntity();
      if(te instanceof TileEntityStirlingGenerator) {
        TileEntityStirlingGenerator gen = (TileEntityStirlingGenerator) te;
        int burnTime = gen.getBurnTime(evt.itemStack);
        if(burnTime <= 0) {
          return false;
        }

        int rate = gen.getPowerUsePerTick();

        String msg = String.format("%s %s %s %s %s %s%s",
                Lang.localize("power.generates"),
                PowerDisplayUtil.formatPower((long)burnTime * rate),
                PowerDisplayUtil.abrevation(),
                Lang.localize("power.generation_rate"),
                PowerDisplayUtil.formatPower(rate),
                PowerDisplayUtil.abrevation(),
                PowerDisplayUtil.perTickStr());

        evt.toolTip.add(msg);
        return true;
      }
    }
    return false;
  }

  public static void addDurabilityTooltip(List<String> toolTip, ItemStack itemStack) {
    if(!itemStack.isItemStackDamageable()) {
      return;
    }
    Item item = itemStack.getItem();
    if(item instanceof ItemTool || item instanceof ItemArmor ||
        item instanceof ItemSword || item instanceof ItemHoe || item instanceof ItemBow) {
      toolTip.add(ItemUtil.getDurabilityString(itemStack));
    }
  }

  public static void addTooltipForFluid(List list, ItemStack stk) {
    FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stk);
    if(fluidStack == null) {
      return;
    }
    addTooltipForFluid(list, fluidStack.getFluid());
  }

  public static void addTooltipForFluid(List list, Fluid fluid) {
    if(fluid != null) {
      IFluidFuel fuel = FluidFuelRegister.instance.getFuel(fluid);
      if(fuel != null) {
        if(showAdvancedTooltips()) {
          list.add(Lang.localize("fuel.tooltip.heading"));
          list.add(EnumChatFormatting.ITALIC + " " + PowerDisplayUtil.formatPowerPerTick(fuel.getPowerPerCycle()));
          list.add(EnumChatFormatting.ITALIC + " " + fuel.getTotalBurningTime() + " " + Lang.localize("fuel.tooltip.burnTime"));
        } else {
          addShowDetailsTooltip(list);
        }
      } else {
        IFluidCoolant coolant = FluidFuelRegister.instance.getCoolant(fluid);
        if(coolant != null) {
          if(showAdvancedTooltips()) {
            list.add(Lang.localize("coolant.tooltip.heading"));
            list.add(EnumChatFormatting.ITALIC + " "
                + PowerDisplayUtil.formatPowerFloat(coolant.getDegreesCoolingPerMB(100) * 1000) + " "
                + Lang.localize("coolant.tooltip.degreesPerBucket")
                );
          } else {
            addShowDetailsTooltip(list);
          }
        }
      }

    }
  }

  public static void addInformation(IResourceTooltipProvider item, ItemTooltipEvent evt) {
    addInformation(item, evt.itemStack, evt.entityPlayer, evt.toolTip);
  }

  public static void addInformation(IResourceTooltipProvider tt, ItemStack itemstack, EntityPlayer entityplayer, List list) {
    String name = tt.getUnlocalizedNameForTooltip(itemstack);
    if(showAdvancedTooltips()) {
      addCommonTooltipFromResources(list, name);
      addDetailedTooltipFromResources(list, name);
    } else {
      addBasicTooltipFromResources(list, name);
      addCommonTooltipFromResources(list, name);
      if(hasDetailedTooltip(tt, itemstack)) {
        addShowDetailsTooltip(list);
      }
    }
  }

  public static void addInformation(IAdvancedTooltipProvider tt, ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    tt.addCommonEntries(itemstack, entityplayer, list, flag);
    if(showAdvancedTooltips()) {
      tt.addDetailedEntries(itemstack, entityplayer, list, flag);
    } else {
      tt.addBasicEntries(itemstack, entityplayer, list, flag);
      if(hasDetailedTooltip(tt, itemstack, entityplayer, flag)) {
        addShowDetailsTooltip(list);
      }
    }
  }

  private static final List<String> throwaway = new ArrayList<String>();

  private static boolean hasDetailedTooltip(IResourceTooltipProvider tt, ItemStack stack) {
    throwaway.clear();
    String name = tt.getUnlocalizedNameForTooltip(stack);
    addDetailedTooltipFromResources(throwaway, name);
    return !throwaway.isEmpty();
  }

  private static boolean hasDetailedTooltip(IAdvancedTooltipProvider tt, ItemStack stack, EntityPlayer player, boolean flag) {
    throwaway.clear();
    tt.addDetailedEntries(stack, player, throwaway, flag);
    return !throwaway.isEmpty();
  }

  public static void addShowDetailsTooltip(List list) {
    list.add(EnumChatFormatting.WHITE + "" + EnumChatFormatting.ITALIC + Lang.localize("item.tooltip.showDetails"));
  }

  public static boolean showAdvancedTooltips() {
    return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
  }

  public static void addDetailedTooltipFromResources(List list, String unlocalizedName) {
    addTooltipFromResources(list, unlocalizedName.concat(".tooltip.detailed.line"));
  }

  public static void addBasicTooltipFromResources(List list, String unlocalizedName) {
    addTooltipFromResources(list, unlocalizedName.concat(".tooltip.basic.line"));
  }

  public static void addCommonTooltipFromResources(List list, String unlocalizedName) {
    addTooltipFromResources(list, unlocalizedName.concat(".tooltip.common.line"));
  }

  public static void addTooltipFromResources(List list, String keyBase) {
    boolean done = false;
    int line = 1;
    while (!done) {
      String key = keyBase + line;
      String val = Lang.localize(key, false);
      if(val == null || val.trim().length() < 0 || val.equals(key) || line > 12) {
        done = true;
      } else {
        list.add(val);
        line++;
      }
    }
  }

  private static String getUnlocalizedNameForTooltip(ItemStack itemstack) {
    String unlocalizedNameForTooltip = null;
    if (itemstack.getItem() instanceof IResourceTooltipProvider) {
      unlocalizedNameForTooltip = ((IResourceTooltipProvider)itemstack.getItem()).getUnlocalizedNameForTooltip(itemstack);
    }
    if(unlocalizedNameForTooltip == null) {
      unlocalizedNameForTooltip = itemstack.getItem().getUnlocalizedName(itemstack);
    }
    return unlocalizedNameForTooltip;
  }

  public static void addCommonTooltipFromResources(List list, ItemStack itemstack) {
    if(itemstack.getItem() == null) {
      return;
    }
    addCommonTooltipFromResources(list, getUnlocalizedNameForTooltip(itemstack));
  }

  public static void addDetailedTooltipFromResources(List list, ItemStack itemstack) {
    if(itemstack.getItem() == null) {
      return;
    }
    addDetailedTooltipFromResources(list, getUnlocalizedNameForTooltip(itemstack));
  }

  private static class BallTipProvider implements IAdvancedTooltipProvider {

    IGrindingMultiplier ball;

    @Override
    @SideOnly(Side.CLIENT)
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
      if(ball == null) {
        return;
      }
      list.add(EnumChatFormatting.BLUE + Lang.localize("darkGrindingBall.tooltip.detailed.line1"));
      list.add(EnumChatFormatting.GRAY + Lang.localize("darkGrindingBall.tooltip.detailed.line2") + toPercent(ball.getGrindingMultiplier()));
      list.add(EnumChatFormatting.GRAY + Lang.localize("darkGrindingBall.tooltip.detailed.line3") + toPercent(ball.getChanceMultiplier()));
      list.add(EnumChatFormatting.GRAY + Lang.localize("darkGrindingBall.tooltip.detailed.line4") + toPercent(1 - ball.getPowerMultiplier()));
    }

    private String toPercent(float fl) {
      fl = fl * 100;
      int per = Math.round(fl);
      return " " + per + "%";
    }
  }

}
