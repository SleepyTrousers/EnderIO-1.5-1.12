package crazypants.enderio.waila;

import static crazypants.enderio.waila.IWailaInfoProvider.BIT_BASIC;
import static crazypants.enderio.waila.IWailaInfoProvider.BIT_COMMON;
import static crazypants.enderio.waila.IWailaInfoProvider.BIT_DETAILED;
import static crazypants.enderio.waila.IWailaInfoProvider.fmt;
import info.jbcs.minecraft.chisel.api.IFacade;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.impl.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.EnderIO;
import crazypants.enderio.block.BlockDarkSteelAnvil;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.power.TileCapacitorBank;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.util.Lang;

public class WailaCompat implements IWailaDataProvider {

  public static final WailaCompat INSTANCE = new WailaCompat();
  
  private static IWailaDataAccessor _accessor = null;
  
  public static void load(IWailaRegistrar registrar) {
    registrar.registerStackProvider(INSTANCE, IFacade.class);
    registrar.registerStackProvider(INSTANCE, BlockDarkSteelAnvil.class);

    registrar.registerHeadProvider(INSTANCE, Block.class);
    registrar.registerBodyProvider(INSTANCE, Block.class);
    registrar.registerTailProvider(INSTANCE, Block.class);

    registrar.registerSyncedNBTKey("controllerStoredEnergyRF", TileCapacitorBank.class);

    //    registrar.registerHeadProvider(INSTANCE, IInternalPowerReceptor.class);
    //    registrar.registerSyncedNBTKey("*", IInternalPowerReceptor.class);

    ConfigHandler.instance().addConfig(EnderIO.MOD_NAME, "facades.hidden", "Sneaky Facades");
    IWailaInfoProvider.fmt.setMaximumFractionDigits(1);
  }

  @Override
  public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
    MovingObjectPosition pos = accessor.getPosition();
    if(config.getConfig("facades.hidden")) {
      if(accessor.getBlock() instanceof IFacade) {
        IFacade bundle = (IFacade) accessor.getBlock();
        Block facade = bundle.getFacade(accessor.getWorld(), pos.blockX, pos.blockY, pos.blockZ, accessor.getSide().ordinal());
        if (facade != null) {
          return facade.getPickBlock(pos, accessor.getWorld(), pos.blockX, pos.blockY, pos.blockZ);
        }
      }
    } else if(accessor.getBlock() instanceof BlockDarkSteelAnvil) {
      return accessor.getBlock().getPickBlock(accessor.getPosition(), accessor.getWorld(), accessor.getPosition().blockX, accessor.getPosition().blockY,
          accessor.getPosition().blockZ);
    }
    return null;
  }

  @Override
  public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
    return currenttip;
  }

  @Override
  public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
	
	_accessor = accessor;
	
    Block block = accessor.getBlock();
    TileEntity te = accessor.getTileEntity();
    Item item = Item.getItemFromBlock(block);
    EntityPlayer player = accessor.getPlayer();
    World world = player.worldObj;
    MovingObjectPosition pos = accessor.getPosition();

    int x = pos.blockX, y = pos.blockY, z = pos.blockZ;

    if(te instanceof IIoConfigurable) {
      IIoConfigurable machine = (IIoConfigurable) te;
      ForgeDirection side = accessor.getSide();
      IoMode mode = machine.getIoMode(side);
      currenttip.add(EnumChatFormatting.YELLOW
          + String.format(Lang.localize("gui.machine.side"), EnumChatFormatting.WHITE + Lang.localize("gui.machine.side." + side.name().toLowerCase())));
      currenttip.add(EnumChatFormatting.YELLOW + String.format(Lang.localize("gui.machine.ioMode"), mode.colorLocalisedName()));
    }

    if(block instanceof IWailaInfoProvider) {
      IWailaInfoProvider info = (IWailaInfoProvider) block;

      if(block instanceof IAdvancedTooltipProvider) {
        int mask = info.getDefaultDisplayMask(world, pos.blockX, pos.blockY, pos.blockZ);
        boolean basic = (mask & BIT_BASIC) == BIT_BASIC;
        boolean common = (mask & BIT_COMMON) == BIT_COMMON;
        boolean detailed = (mask & BIT_DETAILED) == BIT_DETAILED;

        IAdvancedTooltipProvider adv = (IAdvancedTooltipProvider) block;

        if(common) {
          adv.addCommonEntries(itemStack, player, currenttip, false);
        }

        if(TooltipAddera.showAdvancedTooltips() && detailed) {
          adv.addDetailedEntries(itemStack, player, currenttip, false);
        } else if(detailed) { // show "<Hold Shift>"
          TooltipAddera.addShowDetailsTooltip(currenttip);
        }

        if(!TooltipAddera.showAdvancedTooltips() && basic) {
          adv.addBasicEntries(itemStack, player, currenttip, false);
        }
      } else if(block instanceof IResourceTooltipProvider) {
        TooltipAddera.addInformation((IResourceTooltipProvider) block, itemStack, player, currenttip);
      }

      if(currenttip.size() > 0) {
        currenttip.add("");
      }

      info.getWailaInfo(currenttip, player, world, pos.blockX, pos.blockY, pos.blockZ);
    }

    else {
      if(block instanceof IAdvancedTooltipProvider) {
        TooltipAddera.addInformation((IAdvancedTooltipProvider) block, itemStack, player, currenttip, false);
      } else if(item instanceof IAdvancedTooltipProvider) {
        TooltipAddera.addInformation((IAdvancedTooltipProvider) item, itemStack, player, currenttip, false);
      } else if(block instanceof IResourceTooltipProvider) {
        TooltipAddera.addInformation((IResourceTooltipProvider) block, itemStack, player, currenttip);
      }
    }

    if(te instanceof IInternalPoweredTile && accessor.getNBTData().hasKey("storedEnergyRF") && !(te instanceof TileCapBank)) {
      IInternalPoweredTile power = (IInternalPoweredTile) te;

      if(power.displayPower()) {

        if(currenttip.size() > 4) {
          currenttip.add("");
        }

        int stored = accessor.getTileEntity() instanceof TileCapacitorBank ? power.getEnergyStored() : accessor.getNBTData().getInteger("storedEnergyRF");
        int max = power.getMaxEnergyStored();

        currenttip.add(String.format("%s%s%s / %s%s%s RF", EnumChatFormatting.WHITE, fmt.format(stored), EnumChatFormatting.RESET, EnumChatFormatting.WHITE,
            fmt.format(max),
            EnumChatFormatting.RESET));
      }
    } else if(te instanceof IConduitBundle && itemStack != null && itemStack.getItem() == EnderIO.itemPowerConduit) {
      NBTTagCompound nbtRoot = accessor.getNBTData();
      short nbtVersion = nbtRoot.getShort("nbtVersion");
      NBTTagList conduitTags = (NBTTagList) nbtRoot.getTag("conduits");

      if(conduitTags != null) {
        for (int i = 0; i < conduitTags.tagCount(); i++) {
          NBTTagCompound conduitTag = conduitTags.getCompoundTagAt(i);
          IConduit conduit = ConduitUtil.readConduitFromNBT(conduitTag, nbtVersion);
          if(conduit != null && conduit instanceof IPowerConduit) {
            currenttip.add(String.format("%s%s%s / %s%s%s RF", EnumChatFormatting.WHITE, fmt.format(((IPowerConduit) conduit).getEnergyStored()), EnumChatFormatting.RESET,
                EnumChatFormatting.WHITE, fmt.format(((IConduitBundle) te).getMaxEnergyStored()), EnumChatFormatting.RESET));
          }
        }
      }
    }

    return currenttip;
  }

  @Override
  public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
    return currenttip;
  }

  public static NBTTagCompound getNBTData()
  {
	  return _accessor.getNBTData();
  }
}
