package edu.uchicago.lowasser.fingertree;

import static com.google.common.base.Preconditions.checkNotNull;

final class Elem<E> implements Container<E> {
  public static <E> Elem<E> of(E value) {
    return new Elem<E>(checkNotNull(value));
  }

  private final E value;

  private Elem(E value) {
    this.value = value;
  }

  public E index(int i) {
    if (i != 0) {
      throw new IndexOutOfBoundsException();
    }
    return value;
  }

  public int length() {
    return 1;
  }
}
