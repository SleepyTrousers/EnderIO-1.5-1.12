package crazypants.enderio.machine.obelisk.xp;

import java.util.Random;

import com.enderio.core.common.ContainerEnder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.ContainerNoInv;
import crazypants.enderio.machine.monitor.GuiPowerMonitor;
import crazypants.enderio.machine.monitor.TilePowerMonitor;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;

public class BlockExperienceObelisk extends BlockObeliskAbstract<TileExperienceObelisk> {

  public static BlockExperienceObelisk create() {
    BlockExperienceObelisk res = new BlockExperienceObelisk();
    res.init();
    return res;
  }

  private BlockExperienceObelisk() {
    super(ModObject.blockExperienceObelisk, TileExperienceObelisk.class);
    setGuiClasses(ContainerNoInv.class, GuiExperienceObelisk.class);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
    return getIcon(blockSide, 0);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int side, int meta) {
    if(ForgeDirection.getOrientation(side) == ForgeDirection.UP) {
      return EnderIO.blockAttractor.getIcon(side, 0);
    }
    return EnderIO.blockAttractor.getOnIcon();
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_XP_OBELISK;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    ; // Has no particles
  }

}
