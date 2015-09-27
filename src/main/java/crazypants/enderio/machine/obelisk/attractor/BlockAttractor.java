package crazypants.enderio.machine.obelisk.attractor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;

public class BlockAttractor extends BlockObeliskAbstract<TileAttractor> {
  
  public static BlockAttractor create() {
    BlockAttractor res = new BlockAttractor();
    res.init();
    MinecraftForge.EVENT_BUS.register(new EndermanFixer());
    return res;
  }
  
  protected BlockAttractor() {
    super(ModObject.blockAttractor, TileAttractor.class);
    setGuiClasses(ContainerAttractor.class, GuiAttractor.class);
  }

  @SideOnly(Side.CLIENT)
  public IIcon getOnIcon() {
    return iconBuffer[0][6];
  }

  @Override
  protected int getGuiId() {    
    return GuiHandler.GUI_ID_ATTRACTOR;
  }
}
