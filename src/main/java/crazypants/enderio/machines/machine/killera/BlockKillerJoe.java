package crazypants.enderio.machines.machine.killera;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper.IBlockRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
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
  public static final TextureSupplier textureHead1 = TextureRegistry.registerTexture("blocks/killer_joe_head");
  public static final TextureSupplier textureHead2 = TextureRegistry.registerTexture("blocks/killer_joe_head2");

  private static final Double px = 1d / 16d;
  @Nonnull
  public static final AxisAlignedBB AABB = new AxisAlignedBB(2 * px, 0 * px, 2 * px, 14 * px, 16 * px, 14 * px);

  public static BlockKillerJoe create() {
    PacketHandler.INSTANCE.registerMessage(PacketSwing.class, PacketSwing.class, PacketHandler.nextID(), Side.CLIENT);

    BlockKillerJoe res = new BlockKillerJoe();
    MinecraftForge.EVENT_BUS.register(res);
    res.init();
    return res;
  }

  protected BlockKillerJoe() {
    super(MachineObject.block_killer_joe, TileKillerJoe.class, new Material(MapColor.IRON) {

      @Override
      public boolean isOpaque() {
        return false;
      }

    });
    setLightOpacity(5);
    setSoundType(SoundType.GLASS);
  }

  @Override
  public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
    return AABB;
  }

  @Override
  public float getExplosionResistance(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Entity exploder, @Nonnull Explosion explosion) {
    return Config.EXPLOSION_RESISTANT;
  }

  @SubscribeEvent
  public void getKillDisplayName(PlayerEvent.NameFormat nameEvt) {
    if (nameEvt.getUsername() != null && nameEvt.getUsername().startsWith(USERNAME)) {
      nameEvt.setDisplayname(getLocalizedName());
    }
  }

  @Override
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileKillerJoe te = getTileEntity(world, pos);
    if (te != null) {
      return new ContainerKillerJoe(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileKillerJoe te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiKillerJoe(player.inventory, te);
    }
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return GuiID.GUI_ID_KILLER_JOE;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileKillerJoe tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return KillerJoeRenderMapper.killerJoe;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IBlockRenderMapper getBlockRenderMapper() {
    return KillerJoeRenderMapper.killerJoe;
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileKillerJoe.class, new KillerJoeRenderer());
  }

  protected static String permissionAttacking;

  @Override
  public void init(@Nonnull IModObject mo, @Nonnull FMLInitializationEvent event) {
    super.init(mo, event);
    permissionAttacking = PermissionAPI.registerNode(EnderIO.DOMAIN + ".attack." + getRegistryName().getResourcePath().toLowerCase(Locale.ENGLISH),
        DefaultPermissionLevel.ALL, "Permission for the block " + getRegistryName() + " of Ender IO to attack entities."
            + " Note: The GameProfile will be for the block owner, the EntityPlayer in the context will be the fake player.");
  }

}
