package com.learn.akka.helloactors.intro

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import com.learn.akka.helloactors.intro.PingActor.Ping
import com.learn.akka.helloactors.intro.PingPong.Start
import com.learn.akka.helloactors.intro.PongActor.Pong

import java.time.LocalDateTime

object PingPongDemo extends App {

  val actorSystem = ActorSystem(PingPong(), "PingPongGame")

  actorSystem! Start()
  actorSystem! Start()

  Thread.sleep(10000) // wait for 10 seconds and then terminate actor system

  actorSystem.terminate()
}

object PingPong {

  final case class Start()

  def apply(): Behavior[Start] = Behaviors.setup(context => new GameStartBehavior(context))

  private class GameStartBehavior(context: ActorContext[Start]) extends AbstractBehavior[Start](context) {

    private var gameStarted = false

    override def onMessage(msg: Start): Behavior[Start] = {
      if(gameStarted) {
        context.log.info("Game already started... will do nothing")
      }
      else {
        gameStarted = true
        context.log.info("Starting Ping Pong Game")
        val pingActor = context.spawn(PingActor(), "Pinger")
        val pongActor = context.spawn(PongActor(), "Ponger")

        pingActor ! Ping(pongActor)
      }

      this
    }
  }
}

object PingActor {
  final case class Ping(sender: ActorRef[PongActor.Pong])

  def apply(): Behavior[Ping] = Behaviors.receive { (context, message) =>
    context.log.info(s"Ping received at ${LocalDateTime.now()}")
    Thread.sleep(1000) //just simulate some computation time for demo.. but ideally don't put thread sleep in actor
    message.sender ! Pong(context.self)
    Behaviors.same
  }
}

object PongActor {
  final case class Pong(sender: ActorRef[PingActor.Ping])

  def apply(): Behavior[Pong] = Behaviors.setup { context =>
    Behaviors.receiveMessage(message => {
      context.log.info(s"Pong received at ${LocalDateTime.now()}")
      Thread.sleep(1500) //just to simulate some computation time in the demo
      message.sender ! Ping(context.self)
      Behaviors.same
    })
  }

}