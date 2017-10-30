package info.loenwind.autosave.handlers.java;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import info.loenwind.autosave.handlers.IHandler;

public class HandleNNList<E extends Object> extends HandleAbstractList<E> {

  protected HandleNNList(IHandler<E> elemHandler) {
    super(elemHandler);
  }

  @Override
  protected @Nonnull NNList<E> makeList() {
    return new NNList<E>();
  }

}
