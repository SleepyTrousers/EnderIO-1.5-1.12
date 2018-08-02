package crazypants.enderio.base.block.darksteel.bars;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.render.IDefaultRenderers;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockEndIronBars extends BlockPane implements IDefaultRenderers, IModObject.WithBlockItem {

  public static BlockEndIronBars create(@Nonnull IModObject modObject) {
    return new BlockEndIronBars(modObject);
  }

  protected BlockEndIronBars(@Nonnull IModObject modObject) {
    super(Material.IRON, true);
    setResistance(1000.0F);
    setHardness(5.0F);
    setSoundType(SoundType.METAL);
    modObject.apply(this);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }
}
