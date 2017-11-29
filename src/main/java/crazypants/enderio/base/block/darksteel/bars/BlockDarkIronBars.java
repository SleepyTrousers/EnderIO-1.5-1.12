package crazypants.enderio.base.block.darksteel.bars;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.block.darksteel.BlastResistantItemBlock;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IDefaultRenderers;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class BlockDarkIronBars extends BlockPane implements IDefaultRenderers, IModObject.WithBlockItem {

  public static BlockDarkIronBars create(@Nonnull IModObject modObject) {
    return new BlockDarkIronBars(modObject);
  }

  protected BlockDarkIronBars(@Nonnull IModObject modObject) {
    super(Material.IRON, true);
    setResistance(2000.0F); // TNT Proof
    setHardness(5.0F);
    setSoundType(SoundType.METAL);
    modObject.apply(this);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new BlastResistantItemBlock(this));
  }

}
