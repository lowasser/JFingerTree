package edu.uchicago.lowasser.fingertree;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import com.google.common.base.Optional;

final class Digit<E, T extends Container<E>> implements DeepContainer<E, T> {
  public static <E, T extends Container<E>> Digit<E, T> of(T a) {
    return new Digit<E, T>(a);
  }

  public static <E, T extends Container<E>> Digit<E, T> of(T a, T b) {
    return new Digit<E, T>(a, b);
  }

  public static <E, T extends Container<E>> Digit<E, T> of(T a, T b, T c) {
    return new Digit<E, T>(a, b, c);
  }

  public static <E, T extends Container<E>> Digit<E, T> of(T a, T b, T c, T d) {
    return new Digit<E, T>(a, b, c, d);
  }

  private final T[] contents;
  private final int length;

  @SuppressWarnings("unchecked")
  private Digit(T a) {
    this((T[]) new Container[] { a }, a.length());
  }

  @SuppressWarnings("unchecked")
  private Digit(T a, T b) {
    this((T[]) new Container[] { a, b }, a.length() + b.length());
  }

  @SuppressWarnings("unchecked")
  private Digit(T a, T b, T c) {
    this((T[]) new Container[] { a, b, c }, a.length() + b.length() + c.length());
  }

  @SuppressWarnings("unchecked")
  private Digit(T a, T b, T c, T d) {
    this((T[]) new Container[] { a, b, c, d }, a.length() + b.length() + c.length() + d.length());
  }

  Digit(T[] contents, int length) {
    checkArgument(contents.length >= 1 && contents.length <= 4);
    for (T t : contents) {
      checkNotNull(t);
    }
    this.contents = contents;
    this.length = length;
  }

  public E index(int i) {
    for (T sub : contents) {
      int len = sub.length();
      if (i < len) {
        return sub.index(i);
      } else {
        i -= len;
      }
    }
    throw new IndexOutOfBoundsException();
  }

  public int length() {
    return length;
  }

  public T get(int i) {
    return contents[i];
  }

  public int size() {
    return contents.length;
  }

  public T first() {
    return contents[0];
  }

  public T last() {
    return contents[contents.length - 1];
  }

  public View<T, Optional<Digit<E, T>>> viewL() {
    return View.of(contents[0], tail());
  }

  public View<T, Optional<Digit<E, T>>> viewR() {
    switch (contents.length) {
      case 1:
        return View.of(contents[0], Optional.<Digit<E, T>> absent());
      default:
        int last = contents.length - 1;
        int newLen = length - contents[last].length();
        T[] newContents = Arrays.copyOfRange(contents, 0, last);
        return View.of(contents[last], Optional.of(new Digit<E, T>(newContents, newLen)));
    }
  }

  public Optional<Digit<E, T>> tail() {
    switch (contents.length) {
      case 1:
        return Optional.absent();
      default:
        int newLen = length - contents[0].length();
        T[] newContents = Arrays.copyOfRange(contents, 1, contents.length);
        return Optional.of(new Digit<E, T>(newContents, newLen));
    }
  }

  public Optional<Digit<E, T>> init() {
    switch (contents.length) {
      case 1:
        return Optional.absent();
      default:
        int last = contents.length - 1;
        int newLen = length - contents[last].length();
        T[] newContents = Arrays.copyOfRange(contents, 0, last);
        return Optional.of(new Digit<E, T>(newContents, newLen));
    }
  }

  public FingerTree<E, T> asFingerTree() {
    FingerTree<E, Node<E, T>> deepEmpty = FingerTree.empty();
    switch (contents.length) {
      case 1:
        return FingerTree.single(contents[0]);
      case 2:
        return FingerTree.deep(Digit.of(contents[0]), deepEmpty, Digit.of(contents[1]));
      case 3:
        return FingerTree.deep(
            Digit.of(contents[0], contents[1]),
            deepEmpty,
            Digit.of(contents[2]));
      case 4:
        return FingerTree.deep(
            Digit.of(contents[0], contents[1]),
            deepEmpty,
            Digit.of(contents[2], contents[3]));
      default:
        throw new AssertionError();
    }
  }

  public View<Digit<E, T>, Optional<Node<E, T>>> cons(T t) {
    checkNotNull(t);
    switch (contents.length) {
      case 4:
        Node<E, T> node = Node.of(contents[1], contents[2], contents[3]);
        return View.of(Digit.of(t, contents[0]), Optional.of(node));
      default:
        @SuppressWarnings("unchecked")
        T[] newContents = (T[]) new Container[contents.length + 1];
        System.arraycopy(contents, 0, newContents, 1, contents.length);
        int newLength = length + t.length();
        newContents[0] = t;
        return View.of(new Digit<E, T>(newContents, newLength), Optional.<Node<E, T>> absent());
    }
  }

  public View<Digit<E, T>, Optional<Node<E, T>>> snoc(T t) {
    checkNotNull(t);
    switch (contents.length) {
      case 4:
        Node<E, T> node = Node.of(contents[0], contents[1], contents[2]);
        return View.of(Digit.of(contents[3], t), Optional.of(node));
      default:
        T[] newContents = Arrays.copyOf(contents, contents.length + 1);
        newContents[contents.length] = t;
        int newLength = length + t.length();
        return View.of(new Digit<E, T>(newContents, newLength), Optional.<Node<E, T>> absent());
    }
  }
}
