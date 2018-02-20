package info.loenwind.autosave.handlers.endercore;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.java.HandleAbstractCollection;

public abstract class HandleNNList<E> extends HandleAbstractCollection<E, NNList<E>> {

  protected HandleNNList(IHandler<E> elemHandler) {
    super(elemHandler);
  }

  @Override
  protected @Nonnull NNList<E> makeCollection() {
    return new NNList<E>();
  }

  @Override
  protected abstract @Nonnull E makeEmptyValueObject();

}
