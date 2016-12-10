package crazypants.enderio.integration.top;

import javax.annotation.Nullable;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.api.common.util.ITankAccess.ITankData;
import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.FluidUtil.FluidAndStackResult;
import com.google.common.base.Function;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.power.PowerDisplayUtil;
import crazypants.util.CapturedMob;
import crazypants.util.NbtValue;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.ILayoutStyle;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import static crazypants.enderio.ModObject.blockTank;
import static crazypants.enderio.config.Config.topEnabled;
import static crazypants.enderio.config.Config.topShowItemCountDefault;
import static crazypants.enderio.config.Config.topShowMobsByDefault;
import static crazypants.enderio.config.Config.topShowPowerByDefault;
import static crazypants.enderio.config.Config.topShowProgressByDefault;
import static crazypants.enderio.config.Config.topShowRangeByDefault;
import static crazypants.enderio.config.Config.topShowRedstoneByDefault;
import static crazypants.enderio.config.Config.topShowSideConfigByDefault;
import static crazypants.enderio.config.Config.topShowTanksByDefault;
import static crazypants.enderio.config.Config.topShowXPByDefault;

public class TOPCompatibility implements Function<ITheOneProbe, Void>, IProbeInfoProvider, IProbeConfigProvider {

  public static ITheOneProbe probe;

  @Nullable
  @Override
  public Void apply(@Nullable ITheOneProbe theOneProbe) {
    if (topEnabled) {
      probe = theOneProbe;
      Log.info("Enabled support for The One Probe");
      probe.registerProvider(this);
      probe.registerProbeConfigProvider(this);
    } else {
      Log.info("Support for The One Probe is DISABLED by a configuration setting");
    }
    return null;
  }

  @Override
  public String getID() {
    return EnderIO.DOMAIN + ":default";
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData hitData) {
    if (probeInfo != null && world != null && blockState != null && hitData != null && (blockState.getBlock() instanceof BlockEio || blockState.getBlock() instanceof IPaintable)) {
      TileEntity tileEntity = BlockEnder.getAnyTileEntitySafe(world, hitData.getPos());
      if (tileEntity != null) {
        EioBox eiobox = new EioBox(probeInfo);

        TOPData data = new TOPData(tileEntity, hitData);

        mkOwner(mode, eiobox, data);

        mkPaint(mode, eiobox, data);

        mkProgressLine(mode, eiobox, data);

        mkRfLine(mode, eiobox, data);

        mkXPLine(mode, eiobox, data);

        mkRedstoneLine(mode, eiobox, data);

        mkSideConfigLine(mode, eiobox, data);

        mkRangeLine(mode, eiobox, data);

        mkTankLines(mode, eiobox, data);

        mkItemFillLevelLine(mode, eiobox, data);

        eiobox.finish();

        EioBox mobbox = new EioBox(probeInfo);

        mkMobsBox(mode, mobbox, world, data);

        mobbox.finish();
      }
    }
  }

  private static class EioBox {
    private final IProbeInfo probeinfo;
    private IProbeInfo eiobox;
    private boolean addMoreIndicator = false;

    public EioBox(IProbeInfo probeinfo) {
      this.probeinfo = probeinfo;
    }

    public IProbeInfo getProbeinfo() {
      return probeinfo;
    }

    public IProbeInfo get() {
      if (eiobox == null) {
        eiobox = probeinfo.vertical(probeinfo.defaultLayoutStyle().borderColor(0xffff0000));
      }
      return eiobox;
    }

    public ILayoutStyle center() {
      return probeinfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER);
    }

    @SuppressWarnings("unused")
    public ILayoutStyle right() {
      return probeinfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_BOTTOMRIGHT);
    }

    public void addMore() {
      addMoreIndicator = true;
    }

    public void finish() {
      if (eiobox != null) {
        if (addMoreIndicator) {
          addIcon(addIcon(get().horizontal(center()), IconEIO.TOP_NOICON, 0), IconEIO.TOP_MORE, 0);
        } else {
          addIcon(addIcon(get().horizontal(center()), IconEIO.TOP_NOICON, 0), IconEIO.TOP_NOMORE, 0);
        }
      } else if (addMoreIndicator) {
        addIcon(addIcon(probeinfo.vertical().horizontal(center()), IconEIO.TOP_NOICON_WIDE, 0), IconEIO.TOP_MORE, 0);
      }
    }
  }

  private void mkMobsBox(ProbeMode mode, EioBox mobbox, World world, TOPData data) {
    if (data.hasMobs) {
      if (mode != ProbeMode.NORMAL || topShowMobsByDefault) {
        mobbox.get().text(TextFormatting.YELLOW + EnderIO.lang.localize("top.action.header", data.mobAction));

        if (data.mobs.isEmpty()) {
          mobbox.get().text(TextFormatting.DARK_RED + EnderIO.lang.localize("top.action.none"));
        } else if (data.mobs.size() <= 4) {
          for (CapturedMob capturedMob : data.mobs) {
            mobbox.get().horizontal(mobbox.center()).entity(capturedMob.getEntity(world, false)).text(capturedMob.getDisplayName());
          }
        } else {
          IProbeInfo mobList = mobbox.get().horizontal(mobbox.center());
          int count = 0;
          for (CapturedMob capturedMob : data.mobs) {
            if (count++ >= 4) {
              mobList = mobbox.get().horizontal(mobbox.center());
              count = 0;
          }
            mobList.entity(capturedMob.getEntity(world, false));
        }
        }
      } else {
        mobbox.addMore();
      }
    }
  }

  private void mkRangeLine(ProbeMode mode, EioBox eiobox, TOPData data) {
    if (data.hasRange) {
      if (mode != ProbeMode.NORMAL || topShowRangeByDefault) {
        int sizeX = (int) data.bounds.sizeX();
        int sizeY = (int) data.bounds.sizeY();
        int sizeZ = (int) data.bounds.sizeZ();

        addIcon(eiobox.get().horizontal(eiobox.center()), IconEIO.SHOW_RANGE).text(
            TextFormatting.YELLOW + EnderIO.lang.localize("top.range.header", TextFormatting.WHITE + EnderIO.lang.localize("top.range", sizeX, sizeY, sizeZ)));
      } else {
        eiobox.addMore();
      }
    }
  }

  private void mkSideConfigLine(ProbeMode mode, EioBox eiobox, TOPData data) {
    if (data.hasIOMode) {
      if (mode != ProbeMode.NORMAL || topShowSideConfigByDefault) {
        addIcon(eiobox.get().horizontal(eiobox.center()), IconEIO.IO_CONFIG_UP).vertical(eiobox.getProbeinfo().defaultLayoutStyle().spacing(-1))
            .text(TextFormatting.YELLOW
                + EnderIO.lang.localize("gui.machine.side", TextFormatting.WHITE + EnderIO.lang.localize("gui.machine.side." + data.sideName)))
            .text(TextFormatting.YELLOW + EnderIO.lang.localize("gui.machine.ioMode", data.ioMode.colorLocalisedName()));
      } else {
        eiobox.addMore();
      }
    }
  }

  private void mkRedstoneLine(ProbeMode mode, EioBox eiobox, TOPData data) {
    if (data.hasRedstone) {
      if (mode != ProbeMode.NORMAL || topShowRedstoneByDefault) {
        addIcon(eiobox.get().horizontal(eiobox.center()), data.redstoneIcon).vertical(eiobox.getProbeinfo().defaultLayoutStyle().spacing(-1))
            .text(data.redstoneTooltip).text(TextFormatting.YELLOW
                + EnderIO.lang.localize("top.redstone.header", TextFormatting.WHITE + EnderIO.lang.localize("top.redstone." + data.redstoneControlStatus)));
      } else {
        eiobox.addMore();
      }
    }
  }

  private void mkPaint(ProbeMode mode, EioBox eiobox, TOPData data) {
    if (data.isPainted) {
      IProbeInfo info = eiobox.get().horizontal(eiobox.center()).item(new ItemStack(Items.PAINTING))
          .vertical(eiobox.getProbeinfo().defaultLayoutStyle().spacing(-1)).text(TextFormatting.YELLOW + EnderIO.lang.localize("top.paint.header"));
      if (data.paint2 != null) {
        info.horizontal(eiobox.center()).item(data.paint2).text(data.paint2.getDisplayName());
      }
      if (data.paint1 != null) {
        info.horizontal(eiobox.center()).item(data.paint1).text(data.paint1.getDisplayName());
      }
    }
  }

  private void mkOwner(ProbeMode mode, EioBox eiobox, TOPData data) {
    if (mode == ProbeMode.DEBUG && data.owner != null) {
      ItemStack skull = new ItemStack(Items.SKULL, 1, 2);
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setString("SkullOwner", data.owner.getPlayerName());
      skull.setTagCompound(nbt);
      eiobox.get().horizontal(eiobox.center()).item(skull).vertical(eiobox.getProbeinfo().defaultLayoutStyle().spacing(-1))
          .text(TextFormatting.YELLOW + EnderIO.lang.localize("top.owner.header")).text(data.owner.getPlayerName());
    }
  }

  private void mkRfLine(ProbeMode mode, EioBox eiobox, TOPData data) {
    if (data.hasRF) {
      if (mode != ProbeMode.NORMAL || topShowPowerByDefault) {
        IProbeInfo rfLine = eiobox.get().horizontal(eiobox.center()).item(new ItemStack(Items.REDSTONE));
        if (data.hasRFIO) {
          rfLine = rfLine.vertical();
        }
        if (data.isPowered) {
          rfLine.progress(data.rf, data.maxrf, eiobox.getProbeinfo().defaultProgressStyle().suffix(EnderIO.lang.localize("top.suffix.rf"))
              .filledColor(0xffd63223).alternateFilledColor(0xffd63223));
        } else {
          rfLine.text(TextFormatting.DARK_RED + EnderIO.lang.localize("top.machine.outofpower"));
        }
        if (data.hasRFIO) {
          rfLine = rfLine.horizontal();
          rfLine.vertical(eiobox.getProbeinfo().defaultLayoutStyle().spacing(-1))//
              .text(TextFormatting.YELLOW + EnderIO.lang.localize("top.rf.header.avg"))
              .text(TextFormatting.YELLOW + EnderIO.lang.localize("top.rf.header.maxin"))
              .text(TextFormatting.YELLOW + EnderIO.lang.localize("top.rf.header.maxout"));

          String line1 = EnderIO.lang.localize("top.rf.value",
              (data.avgRF == 0 ? TextFormatting.WHITE : data.avgRF > 0 ? TextFormatting.GREEN + "+" : TextFormatting.RED)
                  + PowerDisplayUtil.formatPower(data.avgRF));
          String line2 = EnderIO.lang.localize("top.rf.value", TextFormatting.WHITE + PowerDisplayUtil.formatPower(data.maxRFIn));
          String line3 = EnderIO.lang.localize("top.rf.value", TextFormatting.WHITE + PowerDisplayUtil.formatPower(data.maxRFOut));
          rfLine = rfLine.vertical(eiobox.getProbeinfo().defaultLayoutStyle().spacing(-1)).text(line1).text(line2).text(line3);
        }
      } else {
        eiobox.addMore();
      }
    }
  }

  private void mkXPLine(ProbeMode mode, EioBox eiobox, TOPData data) {
    if (data.hasXP) {
      if (mode != ProbeMode.NORMAL || topShowXPByDefault) {
        // We need to put the number of levels in as "current" value for it to be displayed as text. To make the progress bar scale to the partial level, we set
        // the "max" value in a way that is in the same ratio to the number of levels as the xp needed for the next level is to the current xp. If the bar
        // should be empty but we do have at least one level in, there will be a small error, as (levels/Integer.MAX_VALUE) > 0.
        int scalemax = data.xpBarScaled > 0 ? data.experienceLevel * 100 / data.xpBarScaled : Integer.MAX_VALUE;
        eiobox.get().horizontal(eiobox.center()).item(new ItemStack(Items.EXPERIENCE_BOTTLE)).progress(data.experienceLevel, scalemax,
            eiobox.getProbeinfo().defaultProgressStyle().suffix(EnderIO.lang.localize("top.suffix.levels")).filledColor(0xff00FF0F)
                .alternateFilledColor(0xff00AA0A).borderColor(0xff00AA0A));
      } else {
        eiobox.addMore();
      }
    }
  }

  private void mkItemFillLevelLine(ProbeMode mode, EioBox eiobox, TOPData data) {
    if (data.hasItemFillLevel) {
      if (mode != ProbeMode.NORMAL || topShowItemCountDefault) {
        eiobox.get().horizontal(eiobox.center()).item(new ItemStack(Blocks.CHEST)).progress(data.fillCur, data.fillMax,
            eiobox.getProbeinfo().defaultProgressStyle().suffix(EnderIO.lang.localize("top.suffix.items")).filledColor(0xfff8f83c)
                .alternateFilledColor(0xffcfac0b)
                .borderColor(0xffcfac0b));
      } else {
        eiobox.addMore();
      }
    }
  }

  private void mkTankLines(ProbeMode mode, EioBox eiobox, TOPData data) {
    if (data.tankData != null && !data.tankData.isEmpty()) {
      if (mode != ProbeMode.NORMAL || topShowTanksByDefault) {
        for (ITankData tank : data.tankData) {
          ItemStack stack = new ItemStack(blockTank.getBlock());
          String content1 = null;
          String content2 = null;
          final FluidStack fluid = tank.getContent();
          if (fluid != null) {
            FluidStack fluid2 = fluid.copy();
            fluid2.amount = fluid.amount * 16000 / tank.getCapacity();
            FluidAndStackResult fillContainer = FluidUtil.tryFillContainer(stack, fluid2);
            if (fillContainer.result.itemStack != null) {
              stack = fillContainer.result.itemStack;
              NbtValue.FAKE.setInt(stack, 1);
            }
            content1 = fluid.getLocalizedName();
            content2 = EnderIO.lang.localize("top.tank.content", fluid.amount, tank.getCapacity());
          } else {
            content1 = EnderIO.lang.localize("top.tank.content.empty");
            content2 = EnderIO.lang.localize("top.tank.content", 0, tank.getCapacity());
          }
          switch (tank.getTankType()) {
          case INPUT:
            content1 = TextFormatting.YELLOW + EnderIO.lang.localize("top.tank.header.input", TextFormatting.WHITE + content1);
            break;
          case OUTPUT:
            content1 = TextFormatting.YELLOW + EnderIO.lang.localize("top.tank.header.output", TextFormatting.WHITE + content1);
            break;
          case STORAGE:
            content1 = TextFormatting.YELLOW + EnderIO.lang.localize("top.tank.header.storage", TextFormatting.WHITE + content1);
            break;
          }

          eiobox.get().horizontal(eiobox.center()).item(stack).vertical(eiobox.getProbeinfo().defaultLayoutStyle().spacing(-1)).text(content1).text(content2);

        }
      } else {
        eiobox.addMore();
      }
    }
  }


  /**
   * @return true if some information was hidden
   */
  private void mkProgressLine(ProbeMode mode, EioBox eiobox, TOPData data) {
    if (data.progressResult != TOPData.ProgressResult.NONE) {
      if (mode != ProbeMode.NORMAL || topShowProgressByDefault || data.progressResult == TOPData.ProgressResult.PROGRESS_NO_POWER) {
        final IProbeInfo progressLine = eiobox.get().horizontal(eiobox.center()).item(new ItemStack(Items.CLOCK));
        switch (data.progressResult) {
        case PROGRESS:
          progressLine.progress((int) (data.progress * 100), 100, eiobox.getProbeinfo().defaultProgressStyle()
              .suffix(EnderIO.lang.localize("top.suffix.percent")).filledColor(0xffffb600).alternateFilledColor(0xffffb600));
          break;
        case PROGRESS_NO_POWER:
          progressLine.text(TextFormatting.DARK_RED + EnderIO.lang.localize("top.progress.outofpower"));
          break;
        case PROGRESS_ACTIVE:
        case NO_PROGRESS_ACTIVE:
          progressLine.text(EnderIO.lang.localize("top.machine.active"));
          break;
        case PROGRESS_IDLE:
        case NO_PROGRESS_IDLE:
          progressLine.text(EnderIO.lang.localize("top.machine.idle"));
          break;
        case NONE:
          break;
        }
      } else {
        eiobox.addMore();
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

  @Override
  public void getProbeConfig(IProbeConfig config, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
  }

  @Override
  public void getProbeConfig(IProbeConfig config, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
    if (config != null && blockState != null && blockState.getBlock() instanceof BlockEio) {
      config.setRFMode(0);
    }
  }

  private static IProbeInfo addIcon(IProbeInfo probeInfo, IWidgetIcon icon) {
    return addIcon(probeInfo, icon, 4);
  }

  private static IProbeInfo addIcon(IProbeInfo probeInfo, IWidgetIcon icon, int border) {
    ResourceLocation texture = icon.getMap().getTexture();
    int x = icon.getX();
    int y = icon.getY();
    int width = icon.getWidth();
    int height = icon.getHeight();

    return probeInfo.icon(texture, x, y, width, height, probeInfo.defaultIconStyle().width(width + border).height(height + border));
  }

}
