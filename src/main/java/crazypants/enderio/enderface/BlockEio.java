package crazypants.enderio.enderface;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.Log;

public abstract class BlockEio extends Block {

  protected final Class<? extends TileEntity> teClass;
  protected final String name;

  protected BlockEio(String name, Class<? extends TileEntity> teClass) {
    super(Material.rock);
    this.teClass = teClass;
    this.name = name;
    setHardness(0.5F);
    setBlockName(name);
    setStepSound(Block.soundTypeMetal);
    setCreativeTab(EnderIOTab.tabEnderIO);

  }

  protected void init() {
    GameRegistry.registerBlock(this, name);
    if(teClass != null) {
      GameRegistry.registerTileEntity(teClass, name + "TileEntity");
    }
  }

  @Override
  public boolean hasTileEntity(int metadata) {
    return teClass != null;
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    if(teClass != null) {
      try {
        return teClass.newInstance();
      } catch (Exception e) {
        Log.error("Could not create tile entity for block " + name + " for class " + teClass);
      }
    }
    return null;
  }

  @Override
  public void registerBlockIcons(IIconRegister iIconRegister) {
    blockIcon = iIconRegister.registerIcon("enderio:" + name);
  }

}
