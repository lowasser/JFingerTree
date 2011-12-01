package edu.uchicago.lowasser.fingertree;

interface Container<E> extends Iterable<E> {
  E index(int i);

  int length();
}
