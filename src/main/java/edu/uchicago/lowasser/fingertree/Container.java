package edu.uchicago.lowasser.fingertree;

interface Container<E> {
  E index(int i);
  int length();
}
