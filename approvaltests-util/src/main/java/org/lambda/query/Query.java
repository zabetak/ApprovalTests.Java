package org.lambda.query;

import com.spun.util.ArrayUtils;
import org.lambda.functions.Function1;
import org.lambda.query.OrderBy.Order;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

public class Query<In>
{
  public static <In, Out> Queryable<Out> select(Collection<In> list, Function1<In, Out> function)
  {
    Queryable<Out> out = new Queryable<Out>();
    for (In i : list)
    {
      out.add(function.call(i));
    }
    return out;
  }
  public static <In, Out> Queryable<Out> select(In[] list, Function1<In, Out> function)
  {
    return select(ArrayUtils.asList(list), function);
  }
  public static <In> Queryable<In> where(Iterable<In> list, Function1<In, Boolean> funct)
  {
    Queryable<In> out = new Queryable<In>();
    for (In i : list)
    {
      if (funct.call(i))
      {
        out.add(i);
      }
    }
    return out;
  }
  public static <In> In first(In[] list, Function1<In, Boolean> filter)
  {
    return first(ArrayUtils.asList(list), filter);
  }
  public static <In> In first(Iterable<In> list, Function1<In, Boolean> filter)
  {
    for (In i : list)
    {
      if (filter.call(i))
      { return i; }
    }
    return null;
  }
  public static <In> Queryable<In> where(In[] list, Function1<In, Boolean> filter)
  {
    Queryable<In> out = new Queryable<In>();
    for (In i : list)
    {
      if (filter.call(i))
      {
        out.add(i);
      }
    }
    return out;
  }
  public static <In, Out extends Comparable<Out>> In max(Collection<In> list, Function1<In, Out> f1)
  {
    return getTop(list, f1, 1);
  }
  public static <In, Out extends Comparable<Out>> In min(List<In> list, Function1<In, Out> f1)
  {
    return getTop(list, f1, -1);
  }
  public static <In> Double average(List<In> list, Function1<In, Number> f1)
  {
    double total = 0.00;
    for (In in : list)
    {
      total += f1.call(in).doubleValue();
    }
    return total / list.size();
  }
  private static <In, Out extends Comparable<Out>> In getTop(Collection<In> list, Function1<In, Out> f1,
      int modifier)
  {
    if (ArrayUtils.isEmpty(list))
    { return null; }
    In found = list.iterator().next();
    Out max = f1.call(found);
    for (In in : list)
    {
      Out current = f1.call(in);
      if (max.compareTo(current) * modifier < 0)
      {
        max = current;
        found = in;
      }
    }
    return found;
  }
  public static <T, Out extends Comparable<Out>> T[] orderBy(T[] list, Function1<T, Out> f1)
  {
    return orderBy(list, Order.Ascending, f1);
  }
  public static <T, Out extends Comparable<Out>> T[] orderBy(T[] list, Order order, Function1<T, Out> f1)
  {
    Arrays.sort(list, new OrderBy<T, Out>(order, f1));
    return list;
  }
  public static <T> Queryable<T> orderBy(List<T> list, Function1<T, Comparable<?>> f1)
  {
    return orderBy(list, Order.Ascending, f1);
  }
  public static <T> Queryable<T> orderBy(List<T> list, Order order, Function1<T, Comparable<?>> f1)
  {
    Collections.sort(list, new OrderBy<T, Comparable<?>>(order, f1));
    return Queryable.as(list);
  }
  /**
   * Why does sum() return double? see {@link #sum(Number[])}
   */
  public static <In, Out extends Number> Double sum(In[] list, Function1<In, Out> f1)
  {
    return sum(ArrayUtils.asList(list), f1);
  }
  /**
   * Why does sum() return double? see {@link #sum(Number[])}
   */
  public static <In, Out extends Number> Double sum(Collection<In> list, Function1<In, Out> f1)
  {
    double sum = 0;
    for (In in : list)
    {
      sum += f1.call(in).doubleValue();
    }
    return sum;
  }
  /**
   * Why does sum() return double? see {@link #sum(Number[])}
   */
  public static <Out extends Number> Double sum(Collection<Out> list)
  {
    return sum(list, a -> a);
  }
  /**
   * <H1>Why does sum() return double?</H1><br>
   * sum() needs to handle possible overflow.<br>
   * For example:<br>
   * <code>Queryable.as(100, 100, 100).sum(n -&gt; n.byteValue())</code> should equal 300.<br>
   * Because Double handles the largest range of any number, that is what is returned by default. You can convert
   * it to whichever type you want, but be aware that overflowed values may be truncated.
   */
  public static <Out extends Number> Double sum(Out[] list)
  {
    return sum(list, a -> a);
  }
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static <T extends Number> T max(List<T> numbers)
  {
    return (T) max(numbers, (a) -> (Comparable) a);
  }
  public static <T extends Number> T max(T[] numbers)
  {
    return max(ArrayUtils.asList(numbers));
  }
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static <T extends Number> T min(List<T> numbers)
  {
    return (T) min((List) numbers, (Comparable a) -> a);
  }
  public static <In> boolean all(In[] array, Function1<In, Boolean> funct)
  {
    return array.length == where(array, funct).size();
  }
  public static <In> boolean all(List<In> array, Function1<In, Boolean> funct)
  {
    return array.size() == where(array, funct).size();
  }
  public static <In> boolean any(List<In> array, Function1<In, Boolean> funct)
  {
    return first(array, funct) != null;
  }
  public static <In> boolean any(In[] array, Function1<In, Boolean> funct)
  {
    return first(array, funct) != null;
  }
  public static <In> Queryable<In> distinct(List<In> list)
  {
    Queryable<In> distinct = new Queryable<>();
    for (In in : list)
    {
      if (!distinct.contains(in))
      {
        distinct.add(in);
      }
    }
    return distinct;
  }
  public static <In> In last(In[] list)
  {
    return last(Arrays.asList(list));
  }
  static <In> In last(List<In> asList)
  {
    if (asList.isEmpty())
    { return null; }
    return asList.get(asList.size() - 1);
  }
  public static <Out, In> Queryable<Out> selectMany(Queryable<In> list, Function1<In, Collection<Out>> selector)
  {
    Queryable<Out> out = new Queryable<Out>();
    for (In i : list)
    {
      out.addAll(selector.call(i));
    }
    return out;
  }
  public static <Out, In> Queryable<Out> selectManyArray(Queryable<In> list, Function1<In, Out[]> selector)
  {
    Queryable<Out> out = new Queryable<Out>();
    for (In i : list)
    {
      out.addAll(Arrays.asList(selector.call(i)));
    }
    return out;
  }
  public static <Key, In> Queryable<Map.Entry<Key, Queryable<In>>> groupBy(Queryable<In> list,
      Function1<In, Key> keySelector)
  {
    return groupBy(list, keySelector, v -> v, r -> r);
  }
  public static <Key, In, Out1, Out2> Queryable<Entry<Key, Out2>> groupBy(Queryable<In> list,
      Function1<In, Key> keySelector, Function1<In, Out1> valueSelector,
      Function1<Queryable<Out1>, Out2> resultSelector)
  {
    Queryable<Map.Entry<Key, Queryable<Out1>>> tuples = new Queryable<>();
    Queryable<Map.Entry<Key, Out1>> objectsWithKey = list
        .select(i -> new SimpleEntry<>(keySelector.call(i), valueSelector.call(i)));
    for (Entry<Key, Out1> tuple : objectsWithKey)
    {
      Map.Entry<Key, Queryable<Out1>> first = tuples.first(o -> o.getKey().equals(tuple.getKey()));
      if (first == null)
      {
        first = new SimpleEntry<>(tuple.getKey(), Queryable.as(tuple.getValue()));
        tuples.add(first);
      }
      else
      {
        first.getValue().add(tuple.getValue());
      }
    }
    return tuples.select(t -> new SimpleEntry<>(t.getKey(), resultSelector.call(t.getValue())));
  }

  public static <In> Queryable<In> skip(Iterable<In> list, int number) {
    Queryable<In> result = new Queryable<>();
    int counter = 0;
    for (In in : list) {
      counter++;
      if (number < counter) {
        result.add(in);
      }
    }
    return result;
  }
  public static <In> Queryable<In> skip(In[] list, int number) {
    if (list == null || list.length <= number) {
      return Queryable.createEmpty(list);
    }
    In[] ins = Arrays.copyOfRange(list, number, list.length);
    return Queryable.as(ins);
  }

  public static <In> Queryable<In> take(Iterable<In> list, int number) {
    Queryable<In> result = new Queryable<>();
    int counter = 0;
    for (In in : list) {
      counter++;
      if (counter <= number) {
        result.add(in);
      } else {
        break;
      }
    }
    return result;
  }

  public static <In> Queryable<In> take(In[] list, int number) {
    In[] ins = Arrays.copyOfRange(list, 0, number);
    return Queryable.as(ins);
  }
}
