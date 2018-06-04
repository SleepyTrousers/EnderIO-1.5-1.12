package crazypants.enderio.base.machine.render;

import java.util.EnumMap;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.render.property.EnumRenderPart;
import crazypants.enderio.base.render.property.IOMode.EnumIOMode;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderMappers {

  public static final @Nonnull MachineRenderMapper FRONT_MAPPER = new MachineRenderMapper(null);

  public static final @Nonnull MachineRenderMapper FRONT_MAPPER_NO_IO = new MachineRenderMapper(null) {

    @Override
    protected EnumMap<EnumFacing, EnumIOMode> renderIO(@Nonnull AbstractMachineEntity tileEntity, @Nonnull AbstractMachineBlock<?> block) {
      return null;
    }

  };

  public static final @Nonnull MachineRenderMapper BODY_MAPPER = new MachineRenderMapper(EnumRenderPart.BODY);

  public static final @Nonnull MachineRenderMapper SIMPLE_BODY_MAPPER = new MachineRenderMapper(EnumRenderPart.SIMPLE_BODY);

  public static final @Nonnull MachineRenderMapper ENHANCED_BODY_MAPPER = new MachineRenderMapper(EnumRenderPart.ENHANCED_BODY);

  public static final @Nonnull MachineRenderMapper SOUL_MAPPER = new MachineRenderMapper(EnumRenderPart.SOUL);

}
