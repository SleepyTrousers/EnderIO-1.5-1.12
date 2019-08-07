package crazypants.enderio.machines.machine.obelisk.inhibitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.machines.machine.obelisk.base.AbstractBlockRangedObelisk;
import crazypants.enderio.machines.machine.obelisk.base.GuiRangedObelisk;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInhibitorObelisk extends AbstractBlockRangedObelisk<TileInhibitorObelisk> {

  public static BlockInhibitorObelisk create(@Nonnull IModObject modObject) {
    BlockInhibitorObelisk res = new BlockInhibitorObelisk(modObject);
    res.init();
    return res;
  }

  protected BlockInhibitorObelisk(@Nonnull IModObject modObject) {
    super(modObject);
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileInhibitorObelisk te) {
    return new ContainerInhibitorObelisk(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileInhibitorObelisk te) {
    return new GuiRangedObelisk(player.inventory, te, new ContainerInhibitorObelisk(player.inventory, te), "inhibitor");
  }

}
