package com.learn.akka.helloactors.intro.func

import akka.actor.typed.ActorSystem
import com.learn.akka.helloactors.intro.func.BankAccountActor.{Deposit, ShowBalance, Transaction, Withdraw}

object ActorDemo extends App {

  val actorSystem: ActorSystem[Transaction] = ActorSystem(BankAccountActor(10000), "bank_account_system")
  
  actorSystem ! ShowBalance()
  actorSystem ! Deposit(1000)
  actorSystem ! Withdraw(20000)
  actorSystem ! Withdraw(7500)
  actorSystem ! ShowBalance()

  actorSystem.terminate()

}
