package com.learn.akka.helloactors.intro.oops

import akka.actor.typed.ActorSystem
import BankAccountActor.{Deposit, ShowBalance, Transaction, Withdraw}

object ActorDemo extends App {

  val actorSystem: ActorSystem[Transaction] = ActorSystem(BankAccountActor(), "bank_account_system")

  actorSystem ! Deposit(1000)
  actorSystem ! Withdraw(2000)
  actorSystem ! Withdraw(750)
  actorSystem ! ShowBalance()

  actorSystem.terminate()

}
