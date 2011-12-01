package edu.uchicago.lowasser.fingertree;

interface DeepContainer<E, T extends Container<E>> extends Container<E> {
  T get(int i);
  int size();
}
