package crazypants.enderio.base.machine.base.te;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

@FunctionalInterface
public interface ICap {
  // Note: We need to drop the generics for this to work as a function
  Object getCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facingIn);

  public static final @Nonnull Object DENY = new Object();
  public static final Object NEXT = null;

  public static @Nonnull Function<EnumFacing, Object> facedOnly(@Nonnull NNFunction func) {
    return facing -> facing != null ? func.apply(facing) : DENY;
  }

  @FunctionalInterface
  public interface NNFunction {
    Object apply(@Nonnull EnumFacing facing);
  }

  public class List {

    private Map<Capability<?>, NNList<Function<EnumFacing, Object>>> primaries;
    private final NNList<ICap> secondaries = new NNList<>();

    public List(ICap... fillWith) {
      secondaries.addAll(fillWith);
    }

    public List(Capability<?> capability, @Nonnull Function<EnumFacing, Object> func) {
      add(capability, func);
    }

    public void add(ICap cap) {
      secondaries.add(0, cap);
    }

    public void add(Capability<?> capability, @Nonnull Function<EnumFacing, Object> func) {
      if (capability != null) {
        getList(capability).add(0, func);
      }
    }

    private NNList<Function<EnumFacing, Object>> getList(Capability<?> capability) {
      return (primaries = NullHelper.first(primaries, IdentityHashMap::new)).computeIfAbsent(capability, unused -> new NNList<>());
    }

    @SuppressWarnings("unchecked")
    public <T> T first(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
      for (Function<EnumFacing, Object> cap : getList(capability)) {
        Object t = cap.apply(facingIn);
        if (t != NEXT) {
          return t != DENY ? (T) t : null;
        }
      }
      for (ICap iCap : secondaries) {
        Object t = iCap.getCapability(capability, facingIn);
        if (t != NEXT) {
          return t != DENY ? (T) t : null;
        }
      }
      return null;
    }

  }

}