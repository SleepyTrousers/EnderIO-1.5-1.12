package crazypants.enderio.machine.obelisk.xp;

import crazypants.enderio.GuiID;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.machine.obelisk.AbstractBlockObelisk;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockExperienceObelisk extends AbstractBlockObelisk<TileExperienceObelisk> {

  public static BlockExperienceObelisk create(@Nonnull IModObject modObject) {
    BlockExperienceObelisk res = new BlockExperienceObelisk(modObject);
    res.init();
    return res;
  }

  private BlockExperienceObelisk(@Nonnull IModObject modObject) {
    super(modObject, TileExperienceObelisk.class);
  }

  @Override
  protected boolean isActive(@Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos) {
    return true;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileExperienceObelisk te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerExperienceObelisk(te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileExperienceObelisk te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiExperienceObelisk(player.inventory, te);
    }
    return null;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_XP_OBELISK;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(IBlockState bs, World world, BlockPos pos, Random rand) {
    // Has no particles
  }

}
