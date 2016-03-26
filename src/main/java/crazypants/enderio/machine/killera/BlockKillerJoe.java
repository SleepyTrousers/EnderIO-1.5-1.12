package crazypants.enderio.machine.killera;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper.IBlockRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.TextureRegistry;
import crazypants.enderio.render.TextureRegistry.TextureSupplier;
import crazypants.enderio.xp.PacketExperianceContainer;
import crazypants.enderio.xp.PacketGivePlayerXP;

/**
 * Name proudly created by Xaw4
 */
public class BlockKillerJoe extends AbstractMachineBlock<TileKillerJoe> {

  static final String USERNAME = "KillerJoe";
  public static final TextureSupplier textureHead1 = TextureRegistry.registerTexture("blocks/killerJoe_head");
  public static final TextureSupplier textureHead2 = TextureRegistry.registerTexture("blocks/killerJoe_head2");
  
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

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileKillerJoe tileEntity) {
    blockStateWrapper.addCacheKey(0);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getRenderMapper() {
    return KillerJoeRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IBlockRenderMapper getBlockRenderMapper() {
    return KillerJoeRenderMapper.instance;
  }
  
  @Override
  public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
    return true;
  }


}
