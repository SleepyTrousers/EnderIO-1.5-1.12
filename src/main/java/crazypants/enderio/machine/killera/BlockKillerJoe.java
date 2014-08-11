package crazypants.enderio.machine.killera;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.tank.BlockTank;
import crazypants.enderio.machine.tank.PacketTank;
import crazypants.enderio.machine.tank.TileTank;
import crazypants.enderio.network.PacketHandler;

/**
 * Name proudly created by Xaw4
 */
public class BlockKillerJoe extends AbstractMachineBlock<TileKillerJoe> {

  public static BlockKillerJoe create() {
    //PacketHandler.INSTANCE.registerMessage(PacketTank.class, PacketTank.class, PacketHandler.nextID(), Side.CLIENT);
    BlockKillerJoe res = new BlockKillerJoe();
    res.init();
    return res;
  }

  protected BlockKillerJoe() {
    super(ModObject.blockKillerJoe, TileKillerJoe.class);
    setStepSound(Block.soundTypeGlass);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  protected int getGuiId() {
    return 0;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return "enderio:blankMachinePanel";
  }
  
  @Override
  public boolean isOpaqueCube() {
    return false;
  }
  
}
