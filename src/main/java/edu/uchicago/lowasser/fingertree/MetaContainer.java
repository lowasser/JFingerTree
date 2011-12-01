package edu.uchicago.lowasser.fingertree;

public interface MetaContainer<E, T extends Container<E>> extends Container<E> {
  T get(int i);
  int size();
}
