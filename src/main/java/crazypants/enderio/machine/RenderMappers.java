package crazypants.enderio.machine;

import crazypants.enderio.render.EnumRenderPart;
import crazypants.enderio.render.IRenderMapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderMappers {

  public static final IRenderMapper FRONT_MAPPER  = new MachineRenderMapper(null);
  
  public static final IRenderMapper BODY_MAPPER = new MachineRenderMapper(EnumRenderPart.BODY);
  
  public static final IRenderMapper SOUL_MAPPER = new MachineRenderMapper(EnumRenderPart.SOUL);
  
}
