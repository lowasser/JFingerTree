package edu.uchicago.lowasser.fingertree;

public interface Container<E> {
  E index(int i);
  int length();
}
