package com.learn.akka.helloactors.intro.func

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

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
