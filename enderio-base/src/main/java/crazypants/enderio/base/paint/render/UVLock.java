package crazypants.enderio.base.paint.render;

import com.google.common.base.Optional;

import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class UVLock implements IModelState {

  private final IModelState parent;

  public IModelState getParent() {
    return parent;
  }

  public UVLock(IModelState parent) {
    this.parent = parent;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + 1231;
    result = prime * result + ((getParent() == null) ? 0 : getParent().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    UVLock other = (UVLock) obj;
    if (getParent() == null) {
      if (other.getParent() != null)
        return false;
    } else if (!getParent().equals(other.getParent()))
      return false;
    return true;
  }

  @Override
  public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part) {
    return parent.apply(part);
  }

}