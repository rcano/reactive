package reactive

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class ForwardableTests extends FunSuite with ShouldMatchers with CollectEvents {
  implicit val observing = new Observing {}

  test("thunkToSink") {
    var counter = 0
    val source = new EventSource[Int] {} >> { i: Int => counter += i }
    counter should equal(0)
    source.fire(1)
    source.fire(2)
    source.fire(3)
    counter should equal(6)
  }

  test("blockToSink") {
    var counter = 0
    val source = new EventSource[Unit] {} >> { counter += 1 }
    counter should equal(0)
    source.fire()
    source.fire()
    source.fire()
    counter should equal(3)
  }
  test("varToSink") {
    val dest = Var(0)
    val source = new EventSource[Int] {} >> dest
    dest.now should equal(0)
    source.fire(1)
    dest.now should equal(1)
    source.fire(2)
    dest.now should equal(2)
    source.fire(3)
    dest.now should equal(3)
  }
  test("eventSourceToSink") {
    val dest = new EventSource[Int] {}
    val source = new EventSource[Int] {} >> dest
    CollectEvents.collecting(dest) {
      source.fire(1)
      source.fire(2)
      source.fire(3)
    } should equal(
      List(1, 2, 3)
    )
  }
}

