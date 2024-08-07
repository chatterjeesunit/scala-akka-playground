package com.learn.akka.helloactors.intro.oops

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior}
import com.learn.akka.helloactors.intro.oops.BankAccountActor.{Deposit, ShowBalance, Transaction, Withdraw}

object BankAccountDemoWithOopsActor extends App {

  val actorSystem: ActorSystem[Transaction] = ActorSystem(BankAccountActor(), "bank_account_system")

  actorSystem ! Deposit(1000)
  actorSystem ! Withdraw(2000)
  actorSystem ! Withdraw(750)
  actorSystem ! ShowBalance()

  actorSystem.terminate()

}


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