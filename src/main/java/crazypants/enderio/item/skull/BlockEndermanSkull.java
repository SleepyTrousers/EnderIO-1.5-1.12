package crazypants.enderio.item.skull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockEndermanSkull extends BlockEio {

  public static int renderId = -1;

  public enum SkullType {
    
    BASE("base",false),
    REANIMATED("reanimated",true),
    TORMENTED("tormented",false),
    REANIMATED_TORMENTED("reanimatedTormented",true);
    
    final String name;
    final boolean showEyes;
    
    
    SkullType(String name, boolean showEyes) {
      this.name = name;
      this.showEyes = showEyes;
    }
  }
  
  public static BlockEndermanSkull create() {
    BlockEndermanSkull res = new BlockEndermanSkull();
    res.init();
    return res;
  }

  IIcon frontIcon;
  IIcon frontIconEyes;
  IIcon sideIcon;
  IIcon topIcon;

  private BlockEndermanSkull() {
    super(ModObject.blockEndermanSkull.unlocalisedName, TileEndermanSkull.class, Material.circuits);
    setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
  }
  
  

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, ItemEndermanSkull.class, name);    
    GameRegistry.registerTileEntity(teClass, name + "TileEntity");    
  }



  @Override
  public void registerBlockIcons(IIconRegister iIconRegister) {
    frontIcon = iIconRegister.registerIcon("enderio:endermanSkullFront");
    frontIconEyes = iIconRegister.registerIcon("enderio:endermanSkullFrontEyes");
    sideIcon = iIconRegister.registerIcon("enderio:endermanSkullSide");
    topIcon = iIconRegister.registerIcon("enderio:endermanSkullTop");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int side, int meta) {
    ForgeDirection orint = ForgeDirection.getOrientation(side);
    if(orint == ForgeDirection.NORTH) {
      meta = MathHelper.clamp_int(meta, 0, SkullType.values().length - 1);
      return SkullType.values()[meta].showEyes ? frontIconEyes : frontIcon;
    }
    if(orint == ForgeDirection.UP || orint == ForgeDirection.DOWN || orint == ForgeDirection.SOUTH) {
      return topIcon;
    }
    return sideIcon;
  }

  public int getRenderType() {
    return renderId;
  }

  public boolean isOpaqueCube() {
    return false;
  }

  public boolean renderAsNormalBlock() {
    return false;
  }

  @SideOnly(Side.CLIENT)
  public String getItemIconName() {
    return "enderio:endermanSkull";
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
    
    int inc = MathHelper.floor_double((double)(player.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
    float facingYaw = -22.5f * inc;   
    TileEndermanSkull te = (TileEndermanSkull) world.getTileEntity(x, y, z);
    te.setYaw(facingYaw);
    if(world.isRemote) {
      return;
    }
    world.setBlockMetadataWithNotify(x, y, z, stack.getItemDamage(), 2);
    world.markBlockForUpdate(x, y, z);
  }

  @Override
  public int damageDropped(int meta) {
    return meta;
  }
  
  public boolean rotateBlock(World w, int x, int y, int z, ForgeDirection axis) {
	  TileEndermanSkull skull=(TileEndermanSkull) w.getTileEntity(x, y, z);
	  return skull.rotate(axis);
  }
}
