package scala.collection.parallel
package scalatest



import org.scalatest._
import scala.collection._



class TreeStealerTest extends FunSuite {
  import Par._
  import parallel.workstealing.Ops._

  def createHashSet(sz: Int) = {
    var hs = new immutable.HashSet[Int]
    for (i <- 0 until sz) hs = hs + i
    hs
  }

  def printHashSet[T](hs: immutable.HashSet[T], indent: Int = 0) {
    import immutable.HashSet._
    hs match {
      case t: HashTrieSet[_] =>
        println("%sTrie\n".format(" " * indent))
        for (h <- t.elems) printHashSet(h, indent + 2)
      case t: HashSet1[_] =>
        println("%s%s".format(" " * indent, t.iterator.next()))
    }
  }

  test("HashTrieStealer(1).advance(1)") {
    val hs = createHashSet(1)
    val stealer = new workstealing.Trees.HashTrieSetStealer(hs)
    stealer.rootInit()
    assert(stealer.advance(1) == 1)
    assert(stealer.hasNext)
    assert(stealer.next() == 0)
    assert(!stealer.hasNext)
    assert(stealer.advance(1) == -1)
  }

  test("HashTrieStealer(2).advance(1)") {
    val hs = createHashSet(2)
    val stealer = new workstealing.Trees.HashTrieSetStealer(hs)
    stealer.rootInit()
    assert(stealer.advance(1) == 1)
    assert(stealer.hasNext)
    assert(hs contains stealer.next())
    assert(!stealer.hasNext)
    assert(stealer.advance(1) == 1)
    assert(stealer.hasNext)
    assert(hs contains stealer.next())
    assert(!stealer.hasNext)
    assert(stealer.advance(1) == -1)
  }

  test("HashTrieStealer(5).advance(1)") {
    import immutable.HashSet._
    val hs = {
      val h2a = new HashTrieSet(3, Array[immutable.HashSet[Int]](new HashSet1(0, 0), new HashSet1(1, 0)), 2)
      val h2b = new HashSet1(2, 0)
      val h2c = new HashTrieSet(3, Array[immutable.HashSet[Int]](new HashSet1(3, 0), new HashSet1(4, 0)), 2)
      val h1 = new HashTrieSet(7, Array[immutable.HashSet[Int]](h2a, h2b, h2c), 5)
      h1
    }
    val stealer = new workstealing.Trees.HashTrieSetStealer(hs)
    stealer.rootInit()
    assert(stealer.advance(1) == 1)
    assert(stealer.hasNext)
    assert(stealer.next() == 0)
    assert(!stealer.hasNext)
    assert(stealer.advance(1) == 1)
    assert(stealer.hasNext)
    assert(stealer.next() == 1)
    assert(!stealer.hasNext)
    assert(stealer.advance(1) == 1)
    assert(stealer.hasNext)
    assert(stealer.next() == 2)
    assert(!stealer.hasNext)
    assert(stealer.advance(1) == 1)
    assert(stealer.hasNext)
    assert(stealer.next() == 3)
    assert(!stealer.hasNext)
    assert(stealer.advance(1) == 1)
    assert(stealer.hasNext)
    assert(stealer.next() == 4)
    assert(!stealer.hasNext)
    assert(stealer.advance(1) == -1)
  }

  // test("HashTrieStealer.advance(64)") {
  //   val hs = createHashSet(64)
  //   val stealer = new workstealing.Trees.HashTrieSetStealer(hs)
  //   stealer.rootInit()
  //   printHashSet(hs)
  //   println(stealer)
  //   println(stealer.advance(1))
  //   println(stealer)
  // }

}




