package crazypants.enderio.waila;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.block.BlockDarkSteelAnvil;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.liquid.AbstractTankConduit;
import crazypants.enderio.conduit.me.IMEConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.invpanel.TileInventoryPanel;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.util.IFacade;

import static crazypants.enderio.waila.IWailaInfoProvider.*;

public class WailaCompat implements IWailaDataProvider {

  private class WailaWorldWrapper extends World {
    private final World wrapped;
    
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
    public TileEntity getTileEntity(int x, int y, int z) {
      int meta = getBlockMetadata(x, y, z);
      Block block = getBlock(x, y, z);
      if(block == null || !block.hasTileEntity(meta)) {
        return null;
      }
      TileEntity te = block.createTileEntity(this, meta);
      if(te == null) {
        return null;
      }

      te.setWorldObj(this);
      te.xCoord = x;
      te.yCoord = y;
      te.zCoord = z;

      return te;
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
    
    ConfigHandler.instance().addConfig(EnderIO.MOD_NAME, "facades.hidden", EnderIO.lang.localize("waila.config.hiddenfacades"));
  }

  @Override
  public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
    MovingObjectPosition pos = accessor.getPosition();
    if(config.getConfig("facades.hidden")) {
      if(accessor.getBlock() instanceof IFacade) {
        // If facades are hidden, we need to ignore it
        if(accessor.getTileEntity() instanceof IConduitBundle && ConduitUtil.isFacadeHidden((IConduitBundle) accessor.getTileEntity(), accessor.getPlayer())) {
          return null;
        }
        IFacade bundle = (IFacade) accessor.getBlock();
        Block facade = bundle.getFacade(accessor.getWorld(), pos.blockX, pos.blockY, pos.blockZ, accessor.getSide().ordinal());
        if(facade != accessor.getBlock()) {
          ItemStack ret = facade.getPickBlock(pos, new WailaWorldWrapper(accessor.getWorld()), pos.blockX, pos.blockY, pos.blockZ, accessor.getPlayer());
          return ret;
        }
      }
    } else if(accessor.getBlock() instanceof BlockDarkSteelAnvil) {
      return accessor.getBlock().getPickBlock(accessor.getPosition(), accessor.getWorld(), accessor.getPosition().blockX, accessor.getPosition().blockY,
          accessor.getPosition().blockZ, accessor.getPlayer());

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
    World world = accessor.getWorld();
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
          + EnderIO.lang.localize("gui.machine.side", EnumChatFormatting.WHITE + EnderIO.lang.localize("gui.machine.side." + side.name().toLowerCase(Locale.US))));
      if(!(te instanceof TileInventoryPanel)) {
        currenttip.add(EnumChatFormatting.YELLOW + EnderIO.lang.localize("gui.machine.ioMode", mode.colorLocalisedName()));
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

        if(SpecialTooltipHandler.showAdvancedTooltips() && detailed) {
          adv.addDetailedEntries(itemStack, player, currenttip, false);
        } else if(detailed) { // show "<Hold Shift>"
          SpecialTooltipHandler.addShowDetailsTooltip(currenttip);
        }

        if(!SpecialTooltipHandler.showAdvancedTooltips() && basic) {
          adv.addBasicEntries(itemStack, player, currenttip, false);
        }
      } else if(block instanceof IResourceTooltipProvider) {
        SpecialTooltipHandler.INSTANCE.addInformation((IResourceTooltipProvider) block, itemStack, player, currenttip);
      }

      if(currenttip.size() > 0) {
        currenttip.add("");
      }

      info.getWailaInfo(currenttip, player, world, pos.blockX, pos.blockY, pos.blockZ);
    }

    else { 
      if(block instanceof IAdvancedTooltipProvider) {
        SpecialTooltipHandler.INSTANCE.addInformation((IAdvancedTooltipProvider) block, itemStack, player, currenttip, false);
      } else if(item instanceof IAdvancedTooltipProvider) {
        SpecialTooltipHandler.INSTANCE.addInformation((IAdvancedTooltipProvider) item, itemStack, player, currenttip, false);
      } else if(block instanceof IResourceTooltipProvider) {
        SpecialTooltipHandler.INSTANCE.addInformation((IResourceTooltipProvider) block, itemStack, player, currenttip);
      }
    }

    if(te instanceof IConduitBundle) {
      getWailaBodyConduitBundle(itemStack, currenttip);

    } else if(te instanceof IInternalPoweredTile && block == accessor.getBlock() && !(te instanceof TileCapBank)) {
      IInternalPoweredTile power = (IInternalPoweredTile) te;

      if(power.displayPower()) {

        if(currenttip.size() > 4) {
          currenttip.add("");
        }

        int stored = accessor.getNBTData().getInteger("storedEnergyRF");
        int max = accessor.getNBTData().getInteger("maxStoredRF");

        currenttip.add(String.format("%s%s%s / %s%s%s %s", EnumChatFormatting.WHITE, PowerDisplayUtil.formatPower(stored), EnumChatFormatting.RESET,
                EnumChatFormatting.WHITE, PowerDisplayUtil.formatPower(max), EnumChatFormatting.RESET, PowerDisplayUtil.abrevation()));
      }
    }

    return currenttip;
  }

  private void getWailaBodyConduitBundle(ItemStack itemStack, List<String> currenttip) {
    if(itemStack == null) {
      return;
    }

    if(itemStack.getItem() == EnderIO.itemPowerConduit) {
      NBTTagCompound nbtRoot = _accessor.getNBTData();
      if(nbtRoot.hasKey("storedEnergyRF")) {
        int stored = nbtRoot.getInteger("storedEnergyRF");
        int max = nbtRoot.getInteger("maxStoredRF");
        currenttip.add(String.format("%s%s%s / %s%s%s %s", EnumChatFormatting.WHITE, PowerDisplayUtil.formatPower(stored), EnumChatFormatting.RESET,
            EnumChatFormatting.WHITE, PowerDisplayUtil.formatPower(max), EnumChatFormatting.RESET, PowerDisplayUtil.abrevation()));
      }

    } else if(itemStack.getItem() == EnderIO.itemLiquidConduit) {
      NBTTagCompound nbtRoot = _accessor.getNBTData();
      if(nbtRoot.hasKey("fluidLocked") && nbtRoot.hasKey("FluidName")) {
        boolean fluidTypeLocked = nbtRoot.getBoolean("fluidLocked");
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbtRoot);
        String lockedStr = fluidTypeLocked ? EnderIO.lang.localize("itemLiquidConduit.lockedWaila") : "";
        String fluidName = fluid.getLocalizedName();
        int fluidAmount = fluid.amount;
        if(fluidAmount > 0) {
          // NOTE: using PowerDisplayUtil.formatPower here to handle the non breaking space issue
          currenttip.add(String.format("%s%s%s%s %s%s%s %s", lockedStr,
              EnumChatFormatting.WHITE, fluidName, EnumChatFormatting.RESET,
              EnumChatFormatting.WHITE, PowerDisplayUtil.formatPower(fluidAmount), EnumChatFormatting.RESET,
              Fluids.MB()));
        } else if(fluidTypeLocked) {
          currenttip.add(String.format("%s%s%s%s", lockedStr,
              EnumChatFormatting.WHITE, fluidName, EnumChatFormatting.RESET));
        }
      }

    } else if(itemStack.getItem() == EnderIO.itemMEConduit) {
      NBTTagCompound nbtRoot = _accessor.getNBTData();
      if(nbtRoot.hasKey("isDense")) {
        boolean isDense = nbtRoot.getBoolean("isDense");
        int channelsInUse = nbtRoot.getInteger("channelsInUse");
        currenttip.add(MessageFormat.format(EnderIO.lang.localize("itemMEConduit.channelsUsed"),
                channelsInUse, isDense ? 32 : 8));
      }
    }
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
    if(te instanceof IConduitBundle) {
      IConduitBundle icb = (IConduitBundle) te;
      IPowerConduit pc = icb.getConduit(IPowerConduit.class);
      if(pc != null) {
        tag.setInteger("storedEnergyRF", pc.getEnergyStored());
        tag.setInteger("maxStoredRF", pc.getMaxEnergyStored());
      }
      AbstractTankConduit atc = icb.getConduit(AbstractTankConduit.class);
      if(atc != null) {
        FluidStack fluid = atc.getTank().getFluid();
        if(fluid != null) {
          tag.setBoolean("fluidLocked", atc.isFluidTypeLocked());
          fluid.writeToNBT(tag);
        }
      }
      IMEConduit mec = icb.getConduit(IMEConduit.class);
      if(mec != null) {
        tag.setInteger("channelsInUse", mec.getChannelsInUse());
        tag.setBoolean("isDense", mec.isDense());
      }
    } else if(te instanceof IInternalPoweredTile) {
      IInternalPoweredTile ipte = (IInternalPoweredTile) te;
      tag.setInteger("storedEnergyRF", ipte.getEnergyStored());
      tag.setInteger("maxStoredRF", ipte.getMaxEnergyStored());
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
