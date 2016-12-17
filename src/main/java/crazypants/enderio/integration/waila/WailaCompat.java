package crazypants.enderio.integration.waila;

import java.util.List;
import java.util.Locale;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.block.BlockDarkSteelAnvil;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.liquid.AbstractTankConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.invpanel.TileInventoryPanel;
import crazypants.enderio.machine.painter.blocks.BlockPaintedPressurePlate;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.IPaintable.IBlockPaintableBlock;
import crazypants.enderio.paint.YetaUtil;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.enderio.power.PowerDisplayUtil;
import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.impl.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fluids.FluidStack;

import static crazypants.enderio.ModObject.itemLiquidConduit;
import static crazypants.enderio.ModObject.itemPowerConduit;
import static crazypants.enderio.integration.waila.IWailaInfoProvider.BIT_BASIC;
import static crazypants.enderio.integration.waila.IWailaInfoProvider.BIT_COMMON;
import static crazypants.enderio.integration.waila.IWailaInfoProvider.BIT_DETAILED;
public class WailaCompat implements IWailaDataProvider {

  private class WailaWorldWrapper extends World {
    
    private final World wrapped;
    
    private WailaWorldWrapper(World wrapped) {
      //super(wrapped.getSaveHandler(), wrapped.getWorldInfo().getWorldName(), wrapped.provider, new WorldSettings(wrapped.getWorldInfo()), wrapped.theProfiler);
      super(wrapped.getSaveHandler(), wrapped.getWorldInfo(), wrapped.provider, wrapped.theProfiler, wrapped.isRemote);
      this.wrapped = wrapped;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
      IBlockState bs = wrapped.getBlockState(pos);
      Block block = bs.getBlock();
      if (block instanceof IPaintable.IBlockPaintableBlock) {
        return ((IPaintable.IBlockPaintableBlock) block).getPaintSource(bs, wrapped, pos);
      }
      return bs;
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
      
      IBlockState bs = getBlockState(pos);
      Block block = bs.getBlock();
      if(block == null || !block.hasTileEntity(bs)) {
        return null;
      }
      TileEntity te = block.createTileEntity(this, bs);
      if(te == null) {
        return null;
      }
      te.setWorldObj(this);
      te.setPos(pos);
      return te;
    }

    @Override
    protected IChunkProvider createChunkProvider() {
      return null;
    }

    @Override
    public Entity getEntityByID(int p_73045_1_) {
      return null;
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
      return true;
    }
    
  }

  public static final WailaCompat INSTANCE = new WailaCompat();

  private static IWailaDataAccessor _accessor = null;

  public static void load(IWailaRegistrar registrar) {
    registrar.registerStackProvider(INSTANCE, BlockDarkSteelAnvil.class);

    registrar.registerBodyProvider(INSTANCE, BlockEio.class);
    registrar.registerBodyProvider(INSTANCE, BlockPaintedPressurePlate.class);

    registrar.registerNBTProvider(INSTANCE, TileEntityEio.class);
    
    ConfigHandler.instance().addConfig(EnderIO.MOD_NAME, "facades.hidden", EnderIO.lang.localize("waila.config.hiddenfacades"));
  }

  @Override
  public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
    BlockPos pos = accessor.getPosition();
    if(config.getConfig("facades.hidden")) {
      if (accessor.getBlock() instanceof IBlockPaintableBlock) {
        // If facades are hidden, we need to ignore it
        if(accessor.getTileEntity() instanceof IConduitBundle && YetaUtil.isFacadeHidden((IConduitBundle) accessor.getTileEntity(), accessor.getPlayer())) {
          return null;
        }
        IBlockPaintableBlock bundle = (IBlockPaintableBlock) accessor.getBlock();
        IBlockState facade = bundle.getPaintSource(accessor.getBlockState(), accessor.getWorld(), pos);
        if(facade != null && facade.getBlock() != accessor.getBlock()) {
          ItemStack ret = facade.getBlock().getPickBlock(facade, accessor.getMOP(), new WailaWorldWrapper(accessor.getWorld()), pos, accessor.getPlayer());
          return ret;
        }
      }
    } else if(accessor.getBlock() instanceof BlockDarkSteelAnvil) {
      return accessor.getBlock().getPickBlock(accessor.getBlockState(), accessor.getMOP(), accessor.getWorld(), accessor.getPosition(), accessor.getPlayer());

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
    BlockPos pos = accessor.getPosition();
    
    World world = accessor.getWorld();
    IBlockState bs = world.getBlockState(pos);
    Block block = bs.getBlock();
    TileEntity te = world.getTileEntity(pos);
    Item item = Item.getItemFromBlock(block);

    // let's get rid of WAILA's default RF stuff, only supported on WAILA 1.5.9+
    ((ITaggedList<String, String>) currenttip).removeEntries("RFEnergyStorage");

    if(te instanceof IIoConfigurable && block == accessor.getBlock()) {
      IIoConfigurable machine = (IIoConfigurable) te;
      EnumFacing side = accessor.getSide();
      IoMode mode = machine.getIoMode(side);
      currenttip.add(TextFormatting.YELLOW
          + EnderIO.lang.localize("gui.machine.side", TextFormatting.WHITE + EnderIO.lang.localize("gui.machine.side." + side.name().toLowerCase(Locale.US))));
      if(!(te instanceof TileInventoryPanel)) {
        currenttip.add(TextFormatting.YELLOW + EnderIO.lang.localize("gui.machine.ioMode", mode.colorLocalisedName()));
      }
    }

    if(block instanceof IWailaInfoProvider) {
      IWailaInfoProvider info = (IWailaInfoProvider) block;

      if(block instanceof IAdvancedTooltipProvider) {
        int mask = info.getDefaultDisplayMask(world, pos.getX(), pos.getY(), pos.getZ());
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

      info.getWailaInfo(currenttip, player, world, pos.getX(), pos.getY(), pos.getZ());
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

        // Why do we dump the TEs state into NBT to get these values? We have the TE and could ask it directly.
        int stored = accessor.getNBTData().getInteger("storedEnergyRF");
        int max = accessor.getNBTData().getInteger("maxStoredRF");

        currenttip.add(String.format("%s%s%s / %s%s%s %s", TextFormatting.WHITE, PowerDisplayUtil.formatPower(stored), TextFormatting.RESET,
                TextFormatting.WHITE, PowerDisplayUtil.formatPower(max), TextFormatting.RESET, PowerDisplayUtil.abrevation()));
      }
    }

    return currenttip;
  }

  private void getWailaBodyConduitBundle(ItemStack itemStack, List<String> currenttip) {
    if(itemStack == null) {
      return;
    }

    if (itemStack.getItem() == itemPowerConduit.getItem()) {
      NBTTagCompound nbtRoot = _accessor.getNBTData();
      if(nbtRoot.hasKey("storedEnergyRF")) {
        int stored = nbtRoot.getInteger("storedEnergyRF");
        int max = nbtRoot.getInteger("maxStoredRF");
        currenttip.add(String.format("%s%s%s / %s%s%s %s", TextFormatting.WHITE, PowerDisplayUtil.formatPower(stored), TextFormatting.RESET,
            TextFormatting.WHITE, PowerDisplayUtil.formatPower(max), TextFormatting.RESET, PowerDisplayUtil.abrevation()));
      }
      if(nbtRoot.hasKey("maxStoredRF")) {
        int max = nbtRoot.getInteger("maxStoredRF");
        currenttip.add(String.format("%s %s %s", "Max", PowerDisplayUtil.formatPower(max), PowerDisplayUtil.abrevation() + PowerDisplayUtil.perTickStr()));
      }

    } else if (itemStack.getItem() == itemLiquidConduit.getItem()) {
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
              TextFormatting.WHITE, fluidName, TextFormatting.RESET,
              TextFormatting.WHITE, PowerDisplayUtil.formatPower(fluidAmount), TextFormatting.RESET,
              Fluids.MB()));
        } else if(fluidTypeLocked) {
          currenttip.add(String.format("%s%s%s%s", lockedStr,
              TextFormatting.WHITE, fluidName, TextFormatting.RESET));
        }
      }
    }
  }

  @Override
  public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
    return currenttip;
  }

  @Override
  public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
    if(te instanceof IWailaNBTProvider) {
      ((IWailaNBTProvider) te).getData(tag);
    }
    if(te instanceof IConduitBundle) {
      IConduitBundle icb = (IConduitBundle) te;
      IPowerConduit pc = icb.getConduit(IPowerConduit.class);
      if(pc != null ) {
        tag.setInteger("maxStoredRF", pc.getMaxEnergyStored(null));
        if(icb.displayPower()) {
          tag.setInteger("storedEnergyRF", pc.getEnergyStored(null));
        }
      }
      

      AbstractTankConduit atc = icb.getConduit(AbstractTankConduit.class);
      if(atc != null) {
        FluidStack fluid = atc.getTank().getFluid();
        if(fluid != null) {
          tag.setBoolean("fluidLocked", atc.isFluidTypeLocked());
          fluid.writeToNBT(tag);
        }
      }
    } else if(te instanceof IInternalPoweredTile) {
      IInternalPoweredTile ipte = (IInternalPoweredTile) te;
      tag.setInteger("storedEnergyRF", ipte.getEnergyStored(null));
      tag.setInteger("maxStoredRF", ipte.getMaxEnergyStored(null));
    }

    tag.setInteger("x", pos.getX());
    tag.setInteger("y", pos.getY());
    tag.setInteger("z", pos.getZ());
    return tag;
  }

  public static NBTTagCompound getNBTData() {
    return _accessor.getNBTData();
  }

  
}
