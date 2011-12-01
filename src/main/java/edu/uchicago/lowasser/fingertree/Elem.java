package edu.uchicago.lowasser.fingertree;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import com.google.common.collect.Iterators;

final class Elem<E> implements Container<E> {
  public static <E> Elem<E> of(E value) {
    return new Elem<E>(checkNotNull(value));
  }

  private final E value;

  private Elem(E value) {
    this.value = value;
  }

  @Override
  public E index(int i) {
    if (i != 0) {
      throw new IndexOutOfBoundsException();
    }
    return value;
  }

  @Override
  public int length() {
    return 1;
  }

  @Override
  public Iterator<E> iterator() {
    return Iterators.singletonIterator(value);
  }
}
