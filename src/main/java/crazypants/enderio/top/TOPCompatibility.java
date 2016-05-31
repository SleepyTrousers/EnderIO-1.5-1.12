package crazypants.enderio.top;

import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.client.render.BoundingBox;
import com.google.common.base.Function;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.ranged.IRanged;
import crazypants.enderio.power.IInternalPoweredTile;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TOPCompatibility implements Function<ITheOneProbe, Void>, IProbeInfoProvider {

  public static ITheOneProbe probe;

  @Nullable
  @Override
  public Void apply(@Nullable ITheOneProbe theOneProbe) {
    probe = theOneProbe;
    Log.info("Enabled support for The One Probe");
    probe.registerProvider(this);
    return null;
  }

  @Override
  public String getID() {
    return EnderIO.DOMAIN + ":default";
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
    if (blockState.getBlock() instanceof BlockEio) {
      boolean active = true;
      float progress = -1;
      int rf = -1, maxrf = 0;
      String range = null;

      TileEntity tileEntity = world.getTileEntity(data.getPos());

      if (tileEntity != null) {
        if (tileEntity instanceof AbstractMachineEntity) {
          AbstractMachineEntity te = (AbstractMachineEntity) tileEntity;
          active = te.isActive();
        }
        if (tileEntity instanceof IInternalPoweredTile) {
          IInternalPoweredTile te = (IInternalPoweredTile) tileEntity;
          if (te.displayPower()) {
            maxrf = te.getMaxEnergyStored();
            rf = te.getEnergyStored();
          }
        }
        if (tileEntity instanceof IProgressTile) {
          IProgressTile progressTile = (IProgressTile) tileEntity;
          progress = progressTile.getProgress();
          if (tileEntity instanceof AbstractPoweredTaskEntity) {
            AbstractPoweredTaskEntity te = (AbstractPoweredTaskEntity) tileEntity;
            if (te.getCurrentTask() instanceof ContinuousTask) {
              progress = -1;
            }
          }
        }
        if (tileEntity instanceof IRanged) {
          IRanged te = (IRanged) tileEntity;
          BoundingBox bounds = te.getBounds();
          int sizeX = (int) bounds.sizeX();
          int sizeY = (int) bounds.sizeY();
          int sizeZ = (int) bounds.sizeZ();
          range = sizeX + "x" + sizeY + "x" + sizeZ;
        }

        IProbeInfo eiobox = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(0xffff0000));

        if (progress > 0) {
          if (active) {
            eiobox.horizontal().item(new ItemStack(Items.CLOCK)).progress((int) (progress * 100), 100, probeInfo.defaultProgressStyle().suffix("%"));
          } else {
            eiobox.horizontal().item(new ItemStack(Items.CLOCK)).text("idle");
          }
        } else {
          if (active) {
            eiobox.horizontal().item(new ItemStack(Items.CLOCK)).text("active");
          } else {
            eiobox.horizontal().item(new ItemStack(Items.CLOCK)).text("idle");
          }
        }

        if (rf >= 0 && maxrf > 0) {
          eiobox.horizontal().item(new ItemStack(Items.REDSTONE)).progress(rf, maxrf, probeInfo.defaultProgressStyle().suffix("RF"));
        }

        if (range != null) {
          eiobox.horizontal().item(new ItemStack(Blocks.STONE)).text("Range: " + range);
        }

      }
    }

  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    return super.equals(obj);
  }

}
