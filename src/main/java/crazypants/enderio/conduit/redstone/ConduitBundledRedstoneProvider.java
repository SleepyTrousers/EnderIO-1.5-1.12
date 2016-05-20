package crazypants.enderio.conduit.redstone;

public class ConduitBundledRedstoneProvider { //TODO: 1.9 Computer Craft implements IBundledRedstoneProvider {
  
//  @Override
//  public int getBundledRedstoneOutput( World world, BlockPos pos, EnumFacing side ) {
//  
//    TileEntity inputTE = world.getTileEntity(pos);
//    if (inputTE != null && inputTE instanceof IConduitBundle) {
//      IConduitBundle bundle = ((IConduitBundle) inputTE);
//      if (bundle.hasType(IInsulatedRedstoneConduit.class)) {
//        IInsulatedRedstoneConduit conduit = bundle.getConduit(IInsulatedRedstoneConduit.class);
//        Set<Signal> networkOutputs = conduit.getNetworkOutputs(null);
//        int out = 0;
//        for (Signal signal : networkOutputs) {
//          int index = signal.color.ordinal();
//          if (signal.strength != 0) {
//            out |= (1 << 15 - index);
//          }
//        }
//        return out;
//      }
//      return 0;
//    }
//    return -1;
//  }

  public static void register() {
//    ComputerCraftAPI.registerBundledRedstoneProvider(new ConduitBundledRedstoneProvider());
  }
}
