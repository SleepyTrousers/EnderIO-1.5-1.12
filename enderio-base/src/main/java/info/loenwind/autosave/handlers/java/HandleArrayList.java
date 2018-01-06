package info.loenwind.autosave.handlers.java;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import info.loenwind.autosave.handlers.IHandler;

public class HandleArrayList<E> extends HandleAbstractCollection<E, ArrayList<E>> {

  protected HandleArrayList(IHandler<E> elemHandler) {
    super(elemHandler);
  }

  @Override
  protected @Nonnull ArrayList<E> makeCollection() {
    return new ArrayList<E>();
  }

}
