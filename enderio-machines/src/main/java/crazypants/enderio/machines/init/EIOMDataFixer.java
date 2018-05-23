package crazypants.enderio.machines.init;

import javax.annotation.Nonnull;

import crazypants.enderio.machines.EnderIOMachines;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixableData;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class EIOMDataFixer {

  static boolean ENABLED = false;

  public static void register() {
    if (ENABLED) {
      ModFixs fixs = FMLCommonHandler.instance().getDataFixer().init(EnderIOMachines.MODID, 1);
      fixs.registerFix(FixTypes.CHUNK, new TankBlockStateFixer());
      fixs.registerFix(FixTypes.ITEM_INSTANCE, new TankItemDamageFixer());
    }
  }

  private static final class TankItemDamageFixer implements IFixableData {
    @Override
    public int getFixVersion() {
      return 0;
    }

    @Override
    public @Nonnull NBTTagCompound fixTagCompound(@Nonnull NBTTagCompound compound) {
      String nameOld = MachineObject.block_tank.getRegistryName().toString();
      String nameNew = MachineObject.block_tank.getRegistryName().toString();

      if (nameOld.equals(compound.getString("id")) && compound.getShort("Damage") > 0) {
        compound.setString("id", nameNew);
        compound.setShort("Damage", (short) 0);
      }

      return compound;
    }
  }

  private static final class TankBlockStateFixer implements IFixableData {
    @Override
    public int getFixVersion() {
      return 0;
    }

    @Override
    public @Nonnull NBTTagCompound fixTagCompound(@Nonnull NBTTagCompound compound) {
      final Block blockOld = MachineObject.block_tank.getBlockNN();
      final int blockIDOld = Block.getIdFromBlock(blockOld);
      final int stateOld = blockOld.getMetaFromState(blockOld.getDefaultState());

      final Block blockNew = MachineObject.block_tank.getBlockNN();
      final int blockIDNew = Block.getIdFromBlock(blockNew);
      final int stateNew = blockNew.getMetaFromState(blockNew.getDefaultState());

      final NBTTagCompound level = compound.getCompoundTag("Level");
      final NBTTagList sections = level.getTagList("Sections", 10);

      for (int l = 0; l < sections.tagCount(); ++l) {
        final NBTTagCompound section = sections.getCompoundTagAt(l);
        final byte[] blockIDs = section.getByteArray("Blocks");
        final NibbleArray data = new NibbleArray(section.getByteArray("Data"));
        final NibbleArray blockIdExtension = section.hasKey("Add", 7) ? new NibbleArray(section.getByteArray("Add")) : null;

        for (int i = 0; i < blockIDs.length; ++i) {
          final int extension = blockIdExtension == null ? 0 : blockIdExtension.getFromIndex(i);
          final int blockID = extension << 8 | (blockIDs[i] & 255);
          if (blockID == blockIDOld && data.getFromIndex(i) != stateOld) {
            blockIDs[i] = (byte) (blockIDNew & 0xFF);
            if (blockIdExtension != null) {
              blockIdExtension.setIndex(i, (blockIDNew >> 8) & 0xF);
            }
            data.setIndex(i, stateNew);
          }
        }
      }

      return compound;
    }
  }

}
