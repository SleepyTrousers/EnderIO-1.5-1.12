//package crazypants.enderio.conduits.integration.computercraft;
//
//import javax.annotation.Nonnull;
//
//import com.enderio.core.common.util.DyeColor;
//
//import crazypants.enderio.base.conduit.IConduitBundle;
//import crazypants.enderio.base.conduit.redstone.signals.Signal;
//import crazypants.enderio.conduits.conduit.redstone.IRedstoneConduit;
//import crazypants.enderio.conduits.conduit.redstone.RedstoneConduitNetwork;
//import dan200.computercraft.api.redstone.IBundledRedstoneProvider;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.EnumFacing;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
//public class ConduitBundledRedstoneProvider implements IBundledRedstoneProvider {
//
//  @Override
//  public int getBundledRedstoneOutput(@Nonnull World world, @Nonnull BlockPos blockPos, @Nonnull EnumFacing enumFacing) {
//    TileEntity te = world.getTileEntity(blockPos);
//    if (!(te instanceof IConduitBundle)) {
//      return -1;
//    }
//
//    IConduitBundle bundle = (IConduitBundle) te;
//    IRedstoneConduit conduit = bundle.getConduit(IRedstoneConduit.class);
//    RedstoneConduitNetwork network = conduit.getNetwork();
//
//    int out = 0;
//
//    if (network != null) {
//      for (DyeColor color : DyeColor.values()) {
//        Signal output = network.getBundledSignal().getSignal(color);
//        out |= (output.getStrength() == 0 ? 0 : 1) << (15 - color.ordinal());
//      }
//    }
//    return out;
//  }
//}
