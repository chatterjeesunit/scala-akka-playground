package com.learn.akka.helloactors.intro.func

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import com.learn.akka.helloactors.intro.func.BankAccountActor.{Deposit, ShowBalance, Transaction, Withdraw}

/**
 * Single Actor Creation / Message Sending / Actor State Demo
 * Create a Bank Account Actor and send messages to it to withdraw money from the account
 * Demonstrate how actor maintains its state
 *
 * In this code, we have used Functional style to create an actor an maintain its state
 */
object BankAccountActorDemoWithFunctionalActors extends App {

  val actorSystem: ActorSystem[Transaction] = ActorSystem(BankAccountActor(10000), "bank_account_system")
  
  actorSystem ! ShowBalance()
  actorSystem ! Deposit(1000)
  actorSystem ! Withdraw(20000)
  actorSystem ! Withdraw(7500)
  actorSystem ! ShowBalance()

  actorSystem.terminate()

}

object BankAccountActor {

  sealed trait Transaction
  final case class Deposit(amount: Int) extends Transaction
  final case class Withdraw(amount: Int) extends Transaction
  final case class ShowBalance() extends Transaction


  def apply(bankBalance: Int): Behavior[Transaction] = Behaviors.setup { context =>

    Behaviors.receiveMessage {
      case Deposit(amount) =>
        context.log.info(s"Depositing $amount into the bank account")
        apply(bankBalance + amount)

      case Withdraw(amount) =>
        if (bankBalance >= amount) {
          context.log.info(s"Withdrawing $amount from the bank account")
          apply(bankBalance - amount)
        } else {
          context.log.error("Insufficient balance in the bank account")
          Behaviors.same
        }
      case ShowBalance() =>
        context.log.info(s"Balance = $bankBalance")
        Behaviors.same
    }
  }

}