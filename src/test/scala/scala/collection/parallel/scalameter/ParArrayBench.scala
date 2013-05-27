package scala.collection.parallel
package scalameter



import org.scalameter.api._
import scala.reflect.ClassTag



class ParArrayBench extends PerformanceTest.Regression with Serializable {
  import Par._
  import workstealing.WorkstealingTreeScheduler
  import workstealing.WorkstealingTreeScheduler.Config

  /* config */

  def persistor = new SerializationPersistor

  /* generators */

  val arraySizes = Gen.enumeration("size")(10000000, 30000000, 50000000)
  val arrays = for (size <- arraySizes) yield (0 until size).toArray
  @transient lazy val s1 = new WorkstealingTreeScheduler.ForkJoin(new Config.Default(1))
  @transient lazy val s2 = new WorkstealingTreeScheduler.ForkJoin(new Config.Default(2))
  @transient lazy val s4 = new WorkstealingTreeScheduler.ForkJoin(new Config.Default(4))
  @transient lazy val s8 = new WorkstealingTreeScheduler.ForkJoin(new Config.Default(8))

  val pcopts = Seq[(String, Any)](
    exec.minWarmupRuns -> 2,
    exec.maxWarmupRuns -> 4,
    exec.benchRuns -> 4,
    exec.independentSamples -> 1,
    reports.regression.noiseMagnitude -> 0.75)

  /* tests */

  performance of "Par[Array]" config (
    exec.minWarmupRuns -> 25,
    exec.maxWarmupRuns -> 50,
    exec.benchRuns -> 48,
    exec.independentSamples -> 6,
    exec.outliers.suspectPercent -> 40,
    exec.jvmflags -> "-server -Xms3072m -Xmx3072m -XX:MaxPermSize=256m -XX:ReservedCodeCacheSize=64m -XX:+UseCondCardMark -XX:CompileThreshold=100 -Dscala.collection.parallel.range.manual_optimizations=false",
    reports.regression.noiseMagnitude -> 0.15) in {

      measure method "fold" in {
        using(arrays) curve ("Sequential") in { a =>
          var i = 0
          val until = a.length
          var sum = 0
          while (i < until) {
            sum += a(i)
            i += 1
          }
          if (sum == 0) ???
        }

        using(arrays) curve ("Par-1") in { arr =>
          import workstealing.Ops._
          implicit val s = s1
          val pa = arr.toPar
          pa.fold(0)(_ + _)
        }

        using(arrays) curve ("Par-2") in { arr =>
          import workstealing.Ops._
          implicit val s = s2
          val pa = arr.toPar
          pa.fold(0)(_ + _)
        }

        using(arrays) curve ("Par-4") in { arr =>
          import workstealing.Ops._
          implicit val s = s4
          val pa = arr.toPar
          pa.fold(0)(_ + _)
        }

        using(arrays) curve ("Par-8") in { arr =>
          import workstealing.Ops._
          implicit val s = s8
          val pa = arr.toPar
          pa.fold(0)(_ + _)
        }
      }

      measure method "reduce" in {
        using(arrays) curve ("Array") in { a =>
          var i = 0
          val until = a.length
          var sum = 0
          while (i < until) {
            sum += a(i)
            i += 1
          }
          if (sum == 0) ???
        }

        using(arrays) curve ("Par-1") in { a =>
          import workstealing.Ops._
          implicit val s = s1
          a.toPar.reduce(_ + _)
        }

        using(arrays) curve ("Par-2") in { a =>
          import workstealing.Ops._
          implicit val s = s2
          a.toPar.reduce(_ + _)
        }

        using(arrays) curve ("Par-4") in { a =>
          import workstealing.Ops._
          implicit val s = s4
          a.toPar.reduce(_ + _)
        }

        using(arrays) curve ("Par-8") in { a =>
          import workstealing.Ops._
          implicit val s = s8
          a.toPar.reduce(_ + _)
        }
      }

      measure method "aggregate" in {
        using(arrays) curve ("Sequential") in { arr =>
          var i = 0
          val until = arr.length
          var sum = 0
          while (i < until) {
            sum += arr(i)
            i += 1
          }
          if (sum == 0) ???
        }

        using(arrays) curve ("Par-1") in { arr =>
          import workstealing.Ops._
          implicit val s = s1
          val pa = arr.toPar
          pa.aggregate(0)(_ + _)(_ + _)
        }

        using(arrays) curve ("Par-2") in { arr =>
          import workstealing.Ops._
          implicit val s = s2
          val pa = arr.toPar
          pa.aggregate(0)(_ + _)(_ + _)
        }

        using(arrays) curve ("Par-4") in { arr =>
          import workstealing.Ops._
          implicit val s = s4
          val pa = arr.toPar
          pa.aggregate(0)(_ + _)(_ + _)
        }

        using(arrays) curve ("Par-8") in { arr =>
          import workstealing.Ops._
          implicit val s = s8
          val pa = arr.toPar
          pa.aggregate(0)(_ + _)(_ + _)
        }
      }

      measure method "sum" in {
        using(arrays) curve ("Sequential") in { arr =>
          var i = 0
          val until = arr.length
          var sum = 0
          while (i < until) {
            sum += arr(i)
            i += 1
          }
          if (sum == 0) ???
        }

        using(arrays) curve ("Par-1") in { arr =>
          import workstealing.Ops._
          implicit val s = s1
          val pa = arr.toPar
          pa.sum
        }

        using(arrays) curve ("Par-2") in { arr =>
          import workstealing.Ops._
          implicit val s = s2
          val pa = arr.toPar
          pa.sum
        }

        using(arrays) curve ("Par-4") in { arr =>
          import workstealing.Ops._
          implicit val s = s4
          val pa = arr.toPar
          pa.sum
        }

        using(arrays) curve ("Par-8") in { arr =>
          import workstealing.Ops._
          implicit val s = s8
          val pa = arr.toPar
          pa.sum
        }
      }

      measure method "product" in {
        using(arrays) curve ("Sequential") in { arr =>
          var i = 0
          val until = arr.length
          var sum = 1
          while (i < until) {
            sum *= arr(i)
            i += 1
          }
          if (sum == 1) ???
        }

        using(arrays) curve ("Par-1") in { arr =>
          import workstealing.Ops._
          implicit val s = s1
          val pa = arr.toPar
          pa.product
        }

        using(arrays) curve ("Par-2") in { arr =>
          import workstealing.Ops._
          implicit val s = s2
          val pa = arr.toPar
          pa.product
        }

        using(arrays) curve ("Par-4") in { arr =>
          import workstealing.Ops._
          implicit val s = s4
          val pa = arr.toPar
          pa.product
        }

        using(arrays) curve ("Par-8") in { arr =>
          import workstealing.Ops._
          implicit val s = s8
          val pa = arr.toPar
          pa.product
        }
      }

      measure method "count" in {
        using(arrays) curve ("Sequential") in { arr =>
          var i = 0
          val until = arr.length
          var count = 0
          while (i < until) {
            if (arr(i) % 3 == 1) { count += 1}
            i += 1
          }
          count
        }

        using(arrays) curve ("Par-1") in { arr =>
          import workstealing.Ops._
          implicit val s = s1
          val pa = arr.toPar
          pa.count(_ % 3 == 1)
        }

        using(arrays) curve ("Par-2") in { arr =>
          import workstealing.Ops._
          implicit val s = s2
          val pa = arr.toPar
          pa.count(_ % 3 == 1)
        }

        using(arrays) curve ("Par-4") in { arr =>
          import workstealing.Ops._
          implicit val s = s4
          val pa = arr.toPar
          pa.count(_ % 3 == 1)
        }

        using(arrays) curve ("Par-8") in { arr =>
          import workstealing.Ops._
          implicit val s = s8
          val pa = arr.toPar
          pa.count(_ % 3 == 1)
        }
      }
    }
}

