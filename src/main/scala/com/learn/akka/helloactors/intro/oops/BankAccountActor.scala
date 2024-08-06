package com.learn.akka.helloactors.intro.oops

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

object BankAccountActor {


  //Type of messages this actor can handle
  sealed trait Transaction
  final case class Deposit(amount: Int) extends Transaction
  final case class Withdraw(amount: Int) extends Transaction
  final case class ShowBalance() extends Transaction
  
  def apply(): Behavior[Transaction] =
    Behaviors.setup(context => new BankAccountBehavior(context))

  //Actor Behavior Definition
  class BankAccountBehavior(context: ActorContext[Transaction]) extends AbstractBehavior[Transaction](context) {

    //mutable state of the actor
    var balanceAmount: Int = 0


    override def onMessage(msg: Transaction): Behavior[Transaction] = msg match {
      case Deposit(amount) =>
        context.log.info(s"Depositing $amount into the bank account")
        balanceAmount += amount
        this

      case Withdraw(amount) =>
        if(balanceAmount >= amount) {
          context.log.info(s"Withdrawing $amount from the bank account")
          balanceAmount -= amount
        } else {
          context.log.error("Insufficient balance in the bank account")
        }
        this
      case ShowBalance() =>
        context.log.info(s"Balance = $balanceAmount")
        this
    }
  }

}
