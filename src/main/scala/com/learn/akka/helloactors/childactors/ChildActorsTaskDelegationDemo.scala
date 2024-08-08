package com.learn.akka.helloactors.childactors

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.learn.akka.helloactors.childactors.GuardianActor.Task
import com.learn.akka.helloactors.childactors.TaskExecutorActor.Op

/**
 * Demo to show how a parent actor can delegate tasks to child actors
 *
 * Use case
 *  - whenever a new task comes, create a child actor and delegate task to it
 *  - e.g we will create one actor for each mathematical operations - square, sqroot, sin, etc
 *  - all square operations will be handled by same child actor, all sqroot by another child actors
 *  - each new operation will create a corresponding new child actor
 */
object ChildActorsTaskDelegationDemo extends App {

  val actorSystem = ActorSystem(GuardianActor(), "GuardianActor")

  val sqFn: Int => Double = x => x * x
  val sqRootFn: Int => Double = x => Math.sqrt(x)
  val sinFn: Int => Double = x => Math.sin(x)
  val tanFn: Int => Double = x => Math.tan(x)

  val tasks = Seq(
    Task("Square", 1, sqFn),
    Task("Tan", 50, tanFn),
    Task("SqRoot", 4489, sqRootFn),
    Task("Square", 5, sqFn),
    Task("SqRoot", 144, sqRootFn),
    Task("Sin", 90, sinFn),
    Task("Square", 10, sqFn),
    Task("Square", 100, sqFn),
    Task("Sin", 100, sinFn),
    Task("Tan", 100, tanFn),
    Task("Tan", 90, tanFn),
    Task("Sin", 50, sinFn),
    Task("Square", 25, sqFn),
    Task("Sin", 75, sinFn),
    Task("SqRoot", 169, sqRootFn),
    Task("Tan", 75, tanFn),
  )

  tasks.foreach(t => actorSystem ! t)

  Thread.sleep(10000) // just sleep for 10 seconds, before terminating the actor system

  actorSystem.terminate()
}


object GuardianActor {

  final case class Task(op: String, input: Int, task: Int => Double)

  def apply(): Behavior[Task] = Behaviors.receive { (context, message) =>
    val childActor = context.child(message.op)
    val msgForChildActor = Op(message.input, message.task)
    childActor match {
      case Some(child: ActorRef[Op]) => child ! msgForChildActor
      case _ =>
        context.log.info(s"creating new actor for op = ${message.op}")
        val newActor = context.spawn(TaskExecutorActor(), message.op)
        newActor ! msgForChildActor

    }
    Behaviors.same
  }
}

object TaskExecutorActor {

  final case class Op(input: Int, task: Int => Double)

  def apply(): Behavior[Op] = Behaviors.receive{ (context, message) =>
    val result = message.task(message.input)
    context.log.info(f"${context.self.path} : Input = ${message.input}, Result = ${result}%1.2f")
    Behaviors.same
  }
}