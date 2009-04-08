// THIS IS AN AUTO-GENERATED FILE. DO NOT MODIFY.    
// generated by GenCounter on Tue Apr 07 15:39:49 PDT 2009
package scalanlp.counters.longs;
/*
 Copyright 2009 David Hall, Daniel Ramage
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at 
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License. 
*/
import scala.collection.mutable.Map;
import scala.collection.mutable.HashMap;

/**
 * Count objects of type Long with type Int.
 * This trait is a wrapper around Scala's Map trait
 * and can work with any scala Map. 
 *
 * @author dlwh
 */
@serializable 
trait Long2IntCounter extends IntCounter[Long] {


  abstract override def update(k : Long, v : Int) = {

    super.update(k,v);
  }

  // this isn't necessary, except that the jcl MapWrapper overrides put to call Java's put directly.
  override def put(k : Long, v : Int) :Option[Int] = { val old = get(k); update(k,v); old}

  abstract override def -=(key : Long) = {

    super.-=(key);
  }

  /**
   * Increments the count by the given parameter.
   */
   override  def incrementCount(t : Long, v : Int) = {
     update(t,(this(t) + v).asInstanceOf[Int]);
   }


  override def ++=(kv: Iterable[(Long,Int)]) = kv.foreach(+=);

  /**
   * Increments the count associated with Long by Int.
   * Note that this is different from the default Map behavior.
  */
  override def +=(kv: (Long,Int)) = incrementCount(kv._1,kv._2);

  override def default(k : Long) : Int = defaultValue;

  override  protected def defaultValue: Int = 0;

  override def apply(k : Long) : Int = super.apply(k);

  // TODO: clone doesn't seem to work. I think this is a JCL bug.
  override def clone(): Long2IntCounter  = super.clone().asInstanceOf[Long2IntCounter]

  /**
   * Return the Long with the largest count
   */
  override  def argmax() : Long = (elements reduceLeft ((p1:(Long,Int),p2:(Long,Int)) => if (p1._2 > p2._2) p1 else p2))._1

  /**
   * Return the Long with the smallest count
   */
  override  def argmin() : Long = (elements reduceLeft ((p1:(Long,Int),p2:(Long,Int)) => if (p1._2 < p2._2) p1 else p2))._1

  /**
   * Return the largest count
   */
  override  def max : Int = values reduceLeft ((p1:Int,p2:Int) => if (p1 > p2) p1 else p2)
  /**
   * Return the smallest count
   */
  override  def min : Int = values reduceLeft ((p1:Int,p2:Int) => if (p1 < p2) p1 else p2)

  // TODO: decide is this is the interface we want?
  /**
   * compares two objects by their counts
   */ 
  override  def comparator(a : Long, b :Long) = apply(a) compare apply(b);

  /**
   * Return a new Long2DoubleCounter with each Int divided by the total;
   */
  override  def normalized() : Long2DoubleCounter = {
    val normalized = Long2DoubleCounter();
    val total : Double = this.total
    if(total != 0.0)
      for (pair <- elements) {
        normalized(pair._1) = pair._2 / total;
      }
    normalized
  }

  /**
   * Return the sum of the squares of the values
   */
  override  def l2norm() : Double = {
    var norm = 0.0
    for (val v <- values) {
      norm += (v * v)
    }
    return Math.sqrt(norm)
  }

  /**
   * Return a List the top k elements, along with their counts
   */
  override  def topK(k : Int) = Counters.topK[(Long,Int)](k,(x,y) => if(x._2 < y._2) -1 else if (x._2 == y._2) 0 else 1)(this);

  /**
   * Return \sum_(t) C1(t) * C2(t). 
   */
  def dot(that : Long2IntCounter) : Double = {
    var total = 0.0
    for (val (k,v) <- that.elements) {
      total += apply(k).asInstanceOf[Double] * v
    }
    return total
  }

  def +=(that : Long2IntCounter) {
    for(val (k,v) <- that.elements) {
      update(k,(this(k) + v).asInstanceOf[Int]);
    }
  }

  def -=(that : Long2IntCounter) {
    for(val (k,v) <- that.elements) {
      update(k,(this(k) - v).asInstanceOf[Int]);
    }
  }

  override  def *=(scale : Int) {
    transform { (k,v) => (v * scale).asInstanceOf[Int]}
  }

  override  def /=(scale : Int) {
    transform { (k,v) => (v / scale).asInstanceOf[Int]}
  }

  override  def unary_-() = {
      val rv : IntCounter[Long] = Long2IntCounter();
      rv -= this;
      rv;
  }
}


object Long2IntCounter {
  import it.unimi.dsi.fastutil.objects._
  import it.unimi.dsi.fastutil.ints._
  import it.unimi.dsi.fastutil.shorts._
  import it.unimi.dsi.fastutil.longs._
  import it.unimi.dsi.fastutil.floats._
  import it.unimi.dsi.fastutil.doubles._


  import scala.collection.jcl.MapWrapper;
  @serializable
  @SerialVersionUID(2L)
  class FastMapCounter(override val defaultValue: Int) extends MapWrapper[Long,Int] with Long2IntCounter {
    private val under = new Long2IntOpenHashMap;
    def underlying() = under.asInstanceOf[java.util.Map[Long,Int]];
    override def apply(x : Long) = under.get(x);
    override def update(x : Long, v : Int) {
      val oldV = this(x);
      updateTotal(v-oldV);
      under.put(x,v);
    }
  }

  def apply() = new FastMapCounter(0);

  def withDefaultValue(d: Int) = new FastMapCounter(d);
  
}

