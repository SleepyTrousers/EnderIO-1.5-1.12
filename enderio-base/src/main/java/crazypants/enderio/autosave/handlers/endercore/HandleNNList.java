package crazypants.enderio.autosave.handlers.endercore;

import java.lang.reflect.Type;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.java.HandleAbstractCollection;

@SuppressWarnings("rawtypes")
public class HandleNNList extends HandleAbstractCollection<NNList> {

  public HandleNNList() throws NoHandlerFoundException {
    super();
  }
  
  protected HandleNNList(Registry registry, Type... types) throws NoHandlerFoundException {
    super(registry, types);
  }
  
  @Override
  public Class<?> getRootType() {
    return NNList.class;
  }

  @Override
  protected @Nonnull NNList makeCollection() {
    return new NNList();
  }

  @Override
  protected IHandler<? extends NNList> create(Registry registry, Type... types) throws NoHandlerFoundException {
    return new HandleNNList(registry, types);
  }
}
