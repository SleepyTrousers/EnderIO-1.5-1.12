package crazypants.gui;

public interface ListSelectionListener<T> {

  void selectionChanged(GuiScrollableList<T> list, int selectedIndex);
  
}
