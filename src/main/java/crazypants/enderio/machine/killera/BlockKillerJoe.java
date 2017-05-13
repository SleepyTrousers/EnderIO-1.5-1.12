package crazypants.enderio.machine.killera;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiID;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IHaveTESR;
import crazypants.enderio.render.IRenderMapper.IBlockRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.registry.TextureRegistry;
import crazypants.enderio.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.xp.PacketExperienceContainer;
import crazypants.enderio.xp.PacketGivePlayerXP;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * Name proudly created by Xaw4
 */
public class BlockKillerJoe extends AbstractMachineBlock<TileKillerJoe> implements IHaveTESR {

  static final String USERNAME = "KillerJoe";
  public static final TextureSupplier textureHead1 = TextureRegistry.registerTexture("blocks/killerJoe_head");
  public static final TextureSupplier textureHead2 = TextureRegistry.registerTexture("blocks/killerJoe_head2");
  
  private static final Double px = 1d / 16d;
  public static final AxisAlignedBB AABB = new AxisAlignedBB(2 * px, 0 * px, 2 * px, 14 * px, 16 * px, 14 * px);

  public static BlockKillerJoe create() {
    PacketHandler.INSTANCE.registerMessage(PacketSwing.class, PacketSwing.class, PacketHandler.nextID(), Side.CLIENT);    
    PacketGivePlayerXP.register();
    PacketExperienceContainer.register();
    
    BlockKillerJoe res = new BlockKillerJoe();
    MinecraftForge.EVENT_BUS.register(res);
    res.init();
    return res;
  }

  protected BlockKillerJoe() {
    super(ModObject.blockKillerJoe, TileKillerJoe.class, new Material(MapColor.IRON) {

      @Override
      public boolean isOpaque() {
        return false;
      }

    });
    setLightOpacity(5);
    setSoundType(SoundType.GLASS);    
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return AABB;
  }

  @Override
  public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
    return Config.EXPLOSION_RESISTANT;
  }
  
  @SubscribeEvent
  public void getKillDisplayName(PlayerEvent.NameFormat nameEvt)  {
    if(nameEvt.getUsername() != null && nameEvt.getUsername().startsWith(USERNAME)) {
      nameEvt.setDisplayname(getLocalizedName());
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
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_KILLER_JOE;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }
  
  @Override
  public boolean isFullCube(IBlockState bs) {
    return false;
  }

  @Override
  protected EnumFacing getFacingForHeading(EntityLivingBase player) {
    return super.getFacingForHeading(player).getOpposite();
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileKillerJoe tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return KillerJoeRenderMapper.killerJoe;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IBlockRenderMapper getBlockRenderMapper() {
    return KillerJoeRenderMapper.killerJoe;
  }
  
  @Override
  public boolean canRenderInLayer(BlockRenderLayer layer) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileKillerJoe.class, new KillerJoeRenderer());
  }

  protected static String permissionAttacking;

  @Override
  public void init(FMLInitializationEvent event) {
    super.init(event);
    permissionAttacking = PermissionAPI.registerNode(EnderIO.DOMAIN + ".attack." + name.toLowerCase(Locale.ENGLISH), DefaultPermissionLevel.ALL,
        "Permission for the block " + name + " of Ender IO to attack entities."
            + " Note: The GameProfile will be for the block owner, the EntityPlayer in the context will be the fake player.");
  }

}
