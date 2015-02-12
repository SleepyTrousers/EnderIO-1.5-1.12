package crazypants.enderio.waila;

import static crazypants.enderio.waila.IWailaInfoProvider.BIT_BASIC;
import static crazypants.enderio.waila.IWailaInfoProvider.BIT_COMMON;
import static crazypants.enderio.waila.IWailaInfoProvider.BIT_DETAILED;
import static crazypants.enderio.waila.IWailaInfoProvider.fmt;

import java.util.List;

import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.impl.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.block.BlockDarkSteelAnvil;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.liquid.AbstractTankConduit;
import crazypants.enderio.conduit.liquid.ConduitTank;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.enderio.power.IPowerContainer;
import crazypants.util.IFacade;
import crazypants.util.Lang;

public class WailaCompat implements IWailaDataProvider {

  private class WailaWorldWrapper extends World {
    private World wrapped;

    private WailaWorldWrapper(World wrapped) {
      super(wrapped.getSaveHandler(), wrapped.getWorldInfo().getWorldName(), wrapped.provider, new WorldSettings(wrapped.getWorldInfo()), wrapped.theProfiler);
      this.wrapped = wrapped;
      this.isRemote = wrapped.isRemote;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
      Block block = wrapped.getBlock(x, y, z);
      if(block instanceof IFacade) {
        return ((IFacade) block).getFacade(wrapped, x, y, z, -1);
      }
      return block;
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
      Block block = wrapped.getBlock(x, y, z);
      if(block instanceof IFacade) {
        return ((IFacade) block).getFacadeMetadata(wrapped, x, y, z, -1);
      }
      return wrapped.getBlockMetadata(x, y, z);
    }

    @Override
    public TileEntity getTileEntity(int p_147438_1_, int p_147438_2_, int p_147438_3_) {
      return wrapped.getTileEntity(p_147438_1_, p_147438_2_, p_147438_3_);
    }

    @Override
    protected IChunkProvider createChunkProvider() {
      return null;
    }

    @Override
    protected int func_152379_p() {
      return 0;
    }

    @Override
    public Entity getEntityByID(int p_73045_1_) {
      return null;
    }
  }

  public static final WailaCompat INSTANCE = new WailaCompat();

  private static IWailaDataAccessor _accessor = null;

  public static void load(IWailaRegistrar registrar) {
    registrar.registerStackProvider(INSTANCE, IFacade.class);
    registrar.registerStackProvider(INSTANCE, BlockDarkSteelAnvil.class);

    registrar.registerBodyProvider(INSTANCE, BlockEio.class);

    registrar.registerNBTProvider(INSTANCE, TileEntityEio.class);

    ConfigHandler.instance().addConfig(EnderIO.MOD_NAME, "facades.hidden", Lang.localize("waila.config.hiddenfacades"));
    IWailaInfoProvider.fmt.setMaximumFractionDigits(1);
  }

  // IGNORE deprecation, the new method requires forge 1234 which is too new for cauldron!
  @SuppressWarnings("deprecation")
  @Override
  public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
    MovingObjectPosition pos = accessor.getPosition();
    if(config.getConfig("facades.hidden")) {
      if(accessor.getBlock() instanceof IFacade) {
        IFacade bundle = (IFacade) accessor.getBlock();
        Block facade = bundle.getFacade(accessor.getWorld(), pos.blockX, pos.blockY, pos.blockZ, accessor.getSide().ordinal());
        if(facade != null) {
          ItemStack ret = facade.getPickBlock(pos, new WailaWorldWrapper(accessor.getWorld()), pos.blockX, pos.blockY, pos.blockZ);
          return ret;
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

  @SuppressWarnings("unchecked")
  @Override
  public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

    _accessor = accessor;

    EntityPlayer player = accessor.getPlayer();
    MovingObjectPosition pos = accessor.getPosition();
    int x = pos.blockX, y = pos.blockY, z = pos.blockZ;
    World world = new WailaWorldWrapper(player.worldObj);
    Block block = world.getBlock(x, y, z);
    TileEntity te = world.getTileEntity(x, y, z);
    Item item = Item.getItemFromBlock(block);

    // let's get rid of WAILA's default RF stuff, only supported on WAILA 1.5.9+
    ((ITaggedList<String, String>) currenttip).removeEntries("RFEnergyStorage");

    if(te instanceof IIoConfigurable && block == accessor.getBlock()) {
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

    if(te instanceof IConduitBundle && itemStack != null && itemStack.getItem() == EnderIO.itemPowerConduit) {
      NBTTagCompound nbtRoot = accessor.getNBTData();
      short nbtVersion = nbtRoot.getShort("nbtVersion");
      NBTTagList conduitTags = (NBTTagList) nbtRoot.getTag("conduits");

      if(conduitTags != null) {
        for (int i = 0; i < conduitTags.tagCount(); i++) {
          NBTTagCompound conduitTag = conduitTags.getCompoundTagAt(i);
          IConduit conduit = ConduitUtil.readConduitFromNBT(conduitTag, nbtVersion);
          if(conduit instanceof IPowerConduit) {
            currenttip.add(String.format("%s%s%s / %s%s%s RF", EnumChatFormatting.WHITE, fmt.format(((IPowerConduit) conduit).getEnergyStored()),
                EnumChatFormatting.RESET,
                EnumChatFormatting.WHITE, fmt.format(((IConduitBundle) te).getMaxEnergyStored()), EnumChatFormatting.RESET));
          }
        }
      }
    } else if(te instanceof IConduitBundle && itemStack != null && itemStack.getItem() == EnderIO.itemLiquidConduit) {
      NBTTagCompound nbtRoot = accessor.getNBTData();
      short nbtVersion = nbtRoot.getShort("nbtVersion");
      NBTTagList conduitTags = (NBTTagList) nbtRoot.getTag("conduits");

      if(conduitTags != null) {
        for (int i = 0; i < conduitTags.tagCount(); i++) {
          NBTTagCompound conduitTag = conduitTags.getCompoundTagAt(i);
          IConduit conduit = ConduitUtil.readConduitFromNBT(conduitTag, nbtVersion);
          if(conduit instanceof AbstractTankConduit) {
            AbstractTankConduit tankConduit = (AbstractTankConduit) conduit;
            ConduitTank tank = tankConduit.getTank();
            if(tank.getFluid() != null) {
              String lockedStr = tankConduit.isFluidTypeLocked() ? Lang.localize("itemLiquidConduit.lockedWaila") : "";
              String fluidName = tank.getFluid().getLocalizedName();
              int fluidAmount = tank.getFluidAmount();
              if(fluidAmount > 0) {
                currenttip.add(String.format("%s%s%s%s %s%s%s %s", lockedStr,
                    EnumChatFormatting.WHITE, fluidName, EnumChatFormatting.RESET,
                    EnumChatFormatting.WHITE, fmt.format(fluidAmount), EnumChatFormatting.RESET,
                    Fluids.MB()));
              } else if(tankConduit.isFluidTypeLocked()) {
                currenttip.add(String.format("%s%s%s%s", lockedStr,
                    EnumChatFormatting.WHITE, fluidName, EnumChatFormatting.RESET));
              }
            }
            break;
          }
        }
      }
    } else if(te instanceof IInternalPoweredTile && block == accessor.getBlock() && !(te instanceof TileCapBank)) {
      IInternalPoweredTile power = (IInternalPoweredTile) te;

      if(power.displayPower()) {

        if(currenttip.size() > 4) {
          currenttip.add("");
        }

        int stored = accessor.getNBTData().getInteger("storedEnergyRF");
        int max = accessor.getNBTData().getInteger("maxStoredRF");

        currenttip.add(String.format("%s%s%s / %s%s%s RF", EnumChatFormatting.WHITE, fmt.format(stored), EnumChatFormatting.RESET, EnumChatFormatting.WHITE,
            fmt.format(max),
            EnumChatFormatting.RESET));
      }
    }

    return currenttip;
  }

  @Override
  public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
    return currenttip;
  }

  @Override
  public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
    if(te instanceof IWailaNBTProvider) {
      ((IWailaNBTProvider) te).getData(tag);
    }
    if(te instanceof IInternalPoweredTile) {
      tag.setInteger("storedEnergyRF", ((IPowerContainer) te).getEnergyStored());
      tag.setInteger("maxStoredRF", ((IInternalPoweredTile) te).getMaxEnergyStored());
    }
    if(te instanceof IConduitBundle) {
      te.writeToNBT(tag);
    }

    tag.setInteger("x", x);
    tag.setInteger("y", y);
    tag.setInteger("z", z);
    return tag;
  }

  public static NBTTagCompound getNBTData() {
    return _accessor.getNBTData();
  }
}
