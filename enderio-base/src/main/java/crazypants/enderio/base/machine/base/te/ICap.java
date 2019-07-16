package crazypants.enderio.base.machine.base.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

@FunctionalInterface
public interface ICap {
  // Note: We need to drop the generics for this to work as a function
  Object getCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facingIn);

  public class List extends NNList<ICap> {
    @SafeVarargs
    public List(ICap... fillWith) {
      super(fillWith);
    }

    public <T> T first(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
      for (ICap iCap : this) {
        @SuppressWarnings("unchecked")
        T t = (T) iCap.getCapability(capability, facingIn);
        if (t != null) {
          return t;
        }
      }
      return null;
    }

  }

}