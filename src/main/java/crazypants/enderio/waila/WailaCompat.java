package crazypants.enderio.waila;

import static crazypants.enderio.waila.IWailaInfoProvider.*;

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
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.power.TileCapacitorBank;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.util.Lang;

public class WailaCompat implements IWailaDataProvider {

  public static final WailaCompat INSTANCE = new WailaCompat();

  public static void load(IWailaRegistrar registrar) {
    registrar.registerStackProvider(INSTANCE, BlockConduitBundle.class); // CHANGE BLOCK TYPE IF MORE ARE ADDED
    registrar.registerHeadProvider(INSTANCE, Block.class);
    registrar.registerBodyProvider(INSTANCE, Block.class);
    registrar.registerTailProvider(INSTANCE, Block.class);
    
    registrar.registerSyncedNBTKey("controllerStoredEnergyRF", TileCapacitorBank.class);
    
//    registrar.registerHeadProvider(INSTANCE, IInternalPowerReceptor.class);
//    registrar.registerSyncedNBTKey("*", IInternalPowerReceptor.class);

    ConfigHandler.instance().addConfig(EnderIO.MOD_NAME, "facades.hidden", "Sneaky Facades");
  }

  @Override
  public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
    if(accessor.getBlock() instanceof BlockConduitBundle && config.getConfig("facades.hidden")) {
      IConduitBundle bundle = (IConduitBundle) accessor.getTileEntity();
      if(bundle.hasFacade()) {
        return new ItemStack(bundle.getFacadeId(), 1, bundle.getFacadeMetadata());
      }
    }
    return null;
  }

  @Override
  public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
    return currenttip;
  }

  @Override
  public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

    Block                block  = accessor.getBlock();
    TileEntity           te     = accessor.getTileEntity();
    Item                 item   = Item.getItemFromBlock(block);
    EntityPlayer         player = accessor.getPlayer();
    World                world  = player.worldObj;
    MovingObjectPosition pos    = accessor.getPosition();
    
    int x = pos.blockX, y = pos.blockY, z = pos.blockZ;

    if(block instanceof AbstractMachineBlock<?>) {
      if (te != null && te instanceof AbstractMachineEntity) {
        AbstractMachineEntity machine = (AbstractMachineEntity) te;
        ForgeDirection side = accessor.getSide();
        IoMode mode = machine.getIoMode(side);
        currenttip.add(EnumChatFormatting.YELLOW + String.format(Lang.localize("gui.machine.side"), EnumChatFormatting.WHITE + Lang.localize("gui.machine.side." + side.name().toLowerCase())));
        currenttip.add(EnumChatFormatting.YELLOW + String.format(Lang.localize("gui.machine.ioMode"), mode.colorLocalisedName()));
      }
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
      
      if (currenttip.size() > 0) {
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
    
    if (te instanceof IInternalPowerReceptor && accessor.getNBTData().hasKey("storedEnergyRF")) {
      if (currenttip.size() > 4) {
        currenttip.add("");
      }
      
      IInternalPowerReceptor power = (IInternalPowerReceptor) te;
      
      int   stored      = accessor.getTileEntity() instanceof TileCapacitorBank ? power.getEnergyStored() : accessor.getNBTData().getInteger("storedEnergyRF");
      int   max         = power.getMaxEnergyStored();
      
      currenttip.add(String.format("%s%d%s / %s%d%s RF", EnumChatFormatting.WHITE, stored, EnumChatFormatting.RESET, EnumChatFormatting.WHITE, max, EnumChatFormatting.RESET));
      
    } else if (te instanceof IConduitBundle) {
      NBTTagCompound nbtRoot = accessor.getNBTData();
      short nbtVersion = nbtRoot.getShort("nbtVersion");
      NBTTagList conduitTags = (NBTTagList) nbtRoot.getTag("conduits");
      
      if(conduitTags != null) {
        for (int i = 0; i < conduitTags.tagCount(); i++) {
          NBTTagCompound conduitTag = conduitTags.getCompoundTagAt(i);
          IConduit conduit = ConduitUtil.readConduitFromNBT(conduitTag, nbtVersion);
          if(conduit != null && conduit instanceof IPowerConduit) {
            currenttip.add(String.format("%s%d%s / %s%d%s RF", EnumChatFormatting.WHITE, ((IPowerConduit)conduit).getEnergyStored(), EnumChatFormatting.RESET, EnumChatFormatting.WHITE, ((IConduitBundle)te).getMaxEnergyStored(), EnumChatFormatting.RESET));
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
}
