package crazypants.enderio.machine.killera;

import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.xp.PacketExperianceContainer;
import crazypants.enderio.xp.PacketGivePlayerXP;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Name proudly created by Xaw4
 */
public class BlockKillerJoe extends AbstractMachineBlock<TileKillerJoe> {

  static final String USERNAME = "KillerJoe";
  
  public static BlockKillerJoe create() {
    PacketHandler.INSTANCE.registerMessage(PacketSwing.class, PacketSwing.class, PacketHandler.nextID(), Side.CLIENT);    
    PacketGivePlayerXP.register();
    PacketExperianceContainer.register();
    
    BlockKillerJoe res = new BlockKillerJoe();
    MinecraftForge.EVENT_BUS.register(res);
    res.init();
    return res;
  }

  protected BlockKillerJoe() {
    super(ModObject.blockKillerJoe, TileKillerJoe.class);
    setStepSound(Block.soundTypeGlass);    
  }

  @Override
  public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
    return 2000;
  }
  
  @SubscribeEvent
  public void getKillDisplayName(PlayerEvent.NameFormat nameEvt)  {
    if(nameEvt.username != null && nameEvt.username.startsWith(USERNAME)) {
      nameEvt.displayname = getLocalizedName();
    }
  }
  
  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new ContainerKillerJoe(player.inventory, (TileKillerJoe) world.getTileEntity(new BlockPos(x, y, z)));
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GuiKillerJoe(player.inventory, (TileKillerJoe) world.getTileEntity(new BlockPos(x, y, z)));
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_KILLER_JOE;
  }

  @Override
  public int getRenderType() {
    return 2;
  }
  
  @Override
  protected void registerInSmartModelAttacher() {   
  }


  @Override
  public boolean isOpaqueCube() {
    return false;
  }
  
  @Override
  protected EnumFacing getFacingForHeading(int heading) {
    switch (heading) {
    case 0:
      return EnumFacing.SOUTH;
    case 1:
      return EnumFacing.WEST;
    case 2:
      return EnumFacing.NORTH;      
    case 3:
    default:
      return EnumFacing.EAST;    
    }
  }
  
}
