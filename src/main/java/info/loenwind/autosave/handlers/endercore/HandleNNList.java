package info.loenwind.autosave.handlers.endercore;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.java.HandleAbstractList;

public class HandleNNList<E extends Object> extends HandleAbstractList<E> {

  protected HandleNNList(IHandler<E> elemHandler) {
    super(elemHandler);
  }

  @Override
  protected @Nonnull NNList<E> makeList() {
    return new NNList<E>();
  }

}
