package crazypants.enderio.machine;

import java.util.Collections;
import java.util.List;

import crazypants.enderio.render.EnumRenderPart;
import crazypants.enderio.render.IRenderMapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderMappers {

  public static final IRenderMapper FRONT_MAPPER  = new MachineRenderMapper(null);
  
  public static final IRenderMapper FRONT_MAPPER_NO_IO  = new MachineRenderMapper(null) {

    @Override
    protected List<IBlockState> renderIO(TileEntity tileEntity, Block block) {      
      return Collections.emptyList();
    }
    
  };
  
  public static final IRenderMapper BODY_MAPPER = new MachineRenderMapper(EnumRenderPart.BODY);
  
  public static final IRenderMapper SOUL_MAPPER = new MachineRenderMapper(EnumRenderPart.SOUL);
  
}
