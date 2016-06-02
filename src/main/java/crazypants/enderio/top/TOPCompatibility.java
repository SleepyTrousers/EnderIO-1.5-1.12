package crazypants.enderio.top;

import java.util.List;

import javax.annotation.Nullable;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.client.render.BoundingBox;
import com.google.common.base.Function;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.RedstoneControlMode.IconHolder;
import crazypants.enderio.machine.obelisk.spawn.AbstractMobObelisk;
import crazypants.enderio.machine.obelisk.spawn.AbstractMobObelisk.SpawnObeliskAction;
import crazypants.enderio.machine.ranged.IRanged;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.util.CapturedMob;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.IProbeConfigProvider;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class TOPCompatibility implements Function<ITheOneProbe, Void>, IProbeInfoProvider, IProbeConfigProvider {

  public static ITheOneProbe probe;

  @Nullable
  @Override
  public Void apply(@Nullable ITheOneProbe theOneProbe) {
    probe = theOneProbe;
    Log.info("Enabled support for The One Probe");
    probe.registerProvider(this);
    probe.registerProbeConfigProvider(this);
    return null;
  }

  @Override
  public String getID() {
    return EnderIO.DOMAIN + ":default";
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
    if (probeInfo != null && world != null && blockState != null && data != null && blockState.getBlock() instanceof BlockEio) {
      TileEntity tileEntity = world.getTileEntity(data.getPos());
      if (tileEntity != null) {
        boolean active = true;
        float progress = -1;
        int rf = -1, maxrf = 0;
        String range = null;

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
            eiobox.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).item(new ItemStack(Items.CLOCK)).progress(
                (int) (progress * 100), 100,
                probeInfo.defaultProgressStyle().suffix(EnderIO.lang.localize("top.suffix.percent")).filledColor(0xffffb600).alternateFilledColor(0xffffb600));
          } else if (maxrf > 0 && rf == 0) {
            eiobox.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).item(new ItemStack(Items.CLOCK))
                .text(EnderIO.lang.localize("top.progress.outofpower"));
          } else {
            eiobox.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).item(new ItemStack(Items.CLOCK))
                .text(EnderIO.lang.localize("top.progress.idle"));
          }
        } else {
          if (active) {
            eiobox.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).item(new ItemStack(Items.CLOCK))
                .text(EnderIO.lang.localize("top.machine.active"));
          } else {
            eiobox.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).item(new ItemStack(Items.CLOCK))
                .text(EnderIO.lang.localize("top.machine.idle"));
          }
        }

        if (maxrf > 0) {
          if (rf > 0) {
            eiobox.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).item(new ItemStack(Items.REDSTONE)).progress(rf, maxrf,
                probeInfo.defaultProgressStyle().suffix(EnderIO.lang.localize("top.suffix.rf")).filledColor(0xffd63223).alternateFilledColor(0xffd63223));
          } else {
            eiobox.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).item(new ItemStack(Items.REDSTONE))
                .text(EnderIO.lang.localize("top.machine.outofpower"));
          }
        }

        if (tileEntity instanceof IRedstoneModeControlable) {
          IRedstoneModeControlable te = (IRedstoneModeControlable) tileEntity;
          RedstoneControlMode redstoneControlMode = te.getRedstoneControlMode();
          boolean redstoneControlStatus = te.getRedstoneControlStatus();
          IconHolder iconHolder = RedstoneControlMode.IconHolder.getFromMode(redstoneControlMode);
          IWidgetIcon icon = iconHolder.getIcon();
          ResourceLocation texture = icon.getMap().getTexture();
          int x = icon.getX();
          int y = icon.getY();
          int width = icon.getWidth();
          int height = icon.getHeight();
          String tooltip = iconHolder.getTooltip();

          eiobox.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
              .icon(texture, x, y, width, height, probeInfo.defaultIconStyle().width(20).height(20))
              .text(EnderIO.lang.localize("top.redstone." + redstoneControlStatus, tooltip));
        }

        if (range != null) {
          eiobox.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).item(new ItemStack(Blocks.STONE))
              .text(EnderIO.lang.localize("top.header.range", range));
        }

        if (tileEntity instanceof AbstractMobObelisk) {
          AbstractMobObelisk te = (AbstractMobObelisk) tileEntity;
          List<CapturedMob> mobsInFilter = te.getMobsInFilter();
          SpawnObeliskAction spawnObeliskAction = te.getSpawnObeliskAction();

          IProbeInfo mobbox = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(0xffff0000));

          mobbox.text(EnderIO.lang.localize("top.header.action", spawnObeliskAction.getActionString()));

          if (mobsInFilter.isEmpty()) {
            mobbox.text("nothing");
          } else if (mobsInFilter.size() <= 4) {
            for (CapturedMob capturedMob : mobsInFilter) {
              mobbox.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).entity(capturedMob.getEntity(world, false))
                  .text(capturedMob.getDisplayName());
            }
          } else {
            IProbeInfo mobList = mobbox.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
            int count = 0;
            for (CapturedMob capturedMob : mobsInFilter) {
              if (count++ >= 4) {
                mobList = mobbox.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                count = 0;
              }
              mobList.entity(capturedMob.getEntity(world, false));
            }
          }
        }

      } // end eio te
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

  @Override
  public void getProbeConfig(IProbeConfig config, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
  }

  @Override
  public void getProbeConfig(IProbeConfig config, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
    if (config != null && blockState != null && blockState.getBlock() instanceof BlockEio) {
      config.setRFMode(0);
    }
  }

}
