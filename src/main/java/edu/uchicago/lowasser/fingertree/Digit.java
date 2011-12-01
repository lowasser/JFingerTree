package edu.uchicago.lowasser.fingertree;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

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

  final T[] contents;

  public final List<T> asList() {
    return Collections.unmodifiableList(Arrays.asList(contents));
  }

  @SuppressWarnings("unchecked")
  private Digit(T a) {
    this((T[]) new Container[] { a });
  }

  @SuppressWarnings("unchecked")
  private Digit(T a, T b) {
    this((T[]) new Container[] { a, b });
  }

  @SuppressWarnings("unchecked")
  private Digit(T a, T b, T c) {
    this((T[]) new Container[] { a, b, c });
  }

  @SuppressWarnings("unchecked")
  private Digit(T a, T b, T c, T d) {
    this((T[]) new Container[] { a, b, c, d });
  }

  Digit(T[] contents) {
    checkArgument(contents.length >= 1 && contents.length <= 4);
    for (T t : contents) {
      checkNotNull(t);
    }
    this.contents = contents;
  }

  @SuppressWarnings("unchecked")
  Digit(List<T> list) {
    this(list.toArray((T[]) new Container[0]));
  }

  @Override
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

  @Override
  public int length() {
    int len = 0;
    for (T t : contents) {
      len += t.length();
    }
    return len;
  }

  @Override
  public T get(int i) {
    return contents[i];
  }

  @Override
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
        T[] newContents = Arrays.copyOfRange(contents, 0, last);
        return View.of(contents[last], Optional.of(new Digit<E, T>(newContents)));
    }
  }

  public Optional<Digit<E, T>> tail() {
    switch (contents.length) {
      case 1:
        return Optional.absent();
      default:
        T[] newContents = Arrays.copyOfRange(contents, 1, contents.length);
        return Optional.of(new Digit<E, T>(newContents));
    }
  }

  public Optional<Digit<E, T>> init() {
    switch (contents.length) {
      case 1:
        return Optional.absent();
      default:
        int last = contents.length - 1;
        T[] newContents = Arrays.copyOfRange(contents, 0, last);
        return Optional.of(new Digit<E, T>(newContents));
    }
  }

  public FingerTree<E, T> asFingerTree() {
    return FingerTree.small(contents);
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
        newContents[0] = t;
        return View.of(new Digit<E, T>(newContents), Optional.<Node<E, T>> absent());
    }
  }

  public View<Digit<E, T>, Node<E, T>[]> cons(Digit<E, T> digit) {
    int p = 0;
    @SuppressWarnings("unchecked")
    T[] tmp = (T[]) new Container[8];
    System.arraycopy(contents, 0, tmp, p, size());
    p += size();
    System.arraycopy(digit.contents, 0, tmp, p, digit.size());
    p += digit.size();

    int nNodes = (p - 2) / 3; // >= 0
    @SuppressWarnings("unchecked")
    Node<E, T>[] nodes = new Node[nNodes];
    for (int i = p - (3 * nNodes), j = 0; j < nNodes; i += 3, j++) {
      nodes[j] = Node.of(tmp[i], tmp[i + 1], tmp[i + 2]);
    }
    return View.of(new Digit<E, T>(Arrays.copyOf(tmp, p - (3 * nNodes))), nodes);
  }

  public View<Digit<E, T>, Node<E, T>[]> snoc(Digit<E, T> digit) {
    int p = 0;
    @SuppressWarnings("unchecked")
    T[] tmp = (T[]) new Container[8];
    System.arraycopy(digit.contents, 0, tmp, p, digit.size());
    p += digit.size();
    System.arraycopy(contents, 0, tmp, p, size());
    p += size();

    int nNodes = (p - 2) / 3;
    @SuppressWarnings("unchecked")
    Node<E, T>[] nodes = new Node[nNodes];
    for (int i = 0, j = 0; j < nNodes; i += 3, j++) {
      nodes[j] = Node.of(tmp[i], tmp[i + 1], tmp[i + 2]);
    }
    return View.of(new Digit<E, T>(Arrays.copyOfRange(tmp, 3 * nNodes, p)), nodes);
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
        return View.of(new Digit<E, T>(newContents), Optional.<Node<E, T>> absent());
    }
  }

  @Override
  public Iterator<E> iterator() {
    return Iterables.concat(contents).iterator();
  }
}
