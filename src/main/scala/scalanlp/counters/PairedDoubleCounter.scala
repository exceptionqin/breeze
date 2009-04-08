package scalanlp.counters;

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

import scala.collection.mutable.HashMap;

@serializable
class PairedDoubleCounter[K1,K2](defaultValue:Double) extends HashMap[K1,DoubleCounter[K2]] with Function2[K1,K2,Double] {
  def this() = this(0.0);

  // may be overridden with other counters
  override def default(k1 : K1) :DoubleCounter[K2] = DoubleCounter.withDefaultValue[K2](defaultValue);

  override def apply(k1 : K1) = getOrElseUpdate(k1,default(k1));
  def get(k1 : K1, k2 : K2) : Option[Double] = get(k1).flatMap(_.get(k2))
  def apply(k1 : K1, k2: K2) : Double= apply(k1)(k2);
  def update(k1 : K1, k2: K2, v : Double) = apply(k1)(k2) = v;

  def incrementCount(k1: K1, k2: K2, v: Double) = this(k1).incrementCount(k2,v);

  def total = map(_._2.total).foldLeft(0.0)(_+_)

  override def toString = {
    val b = new StringBuilder;
    b append "["
    foreach {  x=>
      b append x
      b append ",\n"
    }
    b append "]"
    b.toString

  }

  /**
   * Ternary version of transform that modifies each element of a counter
   */ 
  def transform(f : (K1,K2,Double)=>Double) = foreach { case (k1,c) =>
    c.transform{ case (k2,v) => f(k1,k2,v)}
  };

  /** 
   * Returns an iterator over each (K1,K2,Value) pair
   */ 
  def triples : Iterator[(K1,K2,Double)] = {
    for( (k1,c) <- elements;
      (k2,v) <- c.elements)
    yield (k1,k2,v);
  }

  def +=(that : PairedDoubleCounter[K1,K2]) {
    this += that.triples;
  }

  def +=(that : Iterable[(K1,K2,Double)]) {
    this += that.elements;
  }

  def +=(that : Iterator[(K1,K2,Double)]) {
    for( (k1,k2,v) <- that) {
      this(k1,k2) += v;
    }
  }
}
