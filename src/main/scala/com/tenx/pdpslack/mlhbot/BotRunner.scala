package com.tenx.pdpslack.mlhbot

import akka.actor.{ActorContext, ActorRef, ActorSystem, Props}
import io.scalac.slack.api.{BotInfo, Start}
import io.scalac.slack.bots.system.{CommandsRecognizerBot, HelpBot}
import io.scalac.slack.common.Shutdownable
import io.scalac.slack.common.actors.SlackBotActor
import io.scalac.slack.websockets.WebSocket
import io.scalac.slack.{BotModules, MessageEventBus, Config => SlackConfig}

//https://pdpslack.slack.com/apps/A0F7YS25R
//http://blog.scalac.io/2015/07/16/slack.html
//https://devcenter.heroku.com/articles/deploying-scala
object BotRunner extends Shutdownable {
  val system = ActorSystem("SlackBotSystem")
  val eventBus = new MessageEventBus
  val slackBot = system.actorOf(Props(classOf[SlackBotActor], new ExampleBotsBundle(), eventBus, this, None), "slack-bot")

  var botInfo: Option[BotInfo] = None

  def main(args: Array[String]) {
    println("SlackBot started")
    println("With api key: " + SlackConfig.apiKey)

    try {
      slackBot ! Start

      system.awaitTermination()
      println("Shutdown successful...")
    } catch {
      case e: Exception =>
        println("An unhandled exception occurred...", e)
        system.shutdown()
        system.awaitTermination()
    }
  }

  sys.addShutdownHook(shutdown())

  override def shutdown(): Unit = {
    slackBot ! WebSocket.Release
    system.shutdown()
    system.awaitTermination()
  }

  class ExampleBotsBundle() extends BotModules {
    override def registerModules(context: ActorContext, websocketClient: ActorRef) = {
      context.actorOf(Props(classOf[CommandsRecognizerBot], eventBus), "commandProcessor")
      context.actorOf(Props(classOf[HelpBot], eventBus), "helpBot")
      context.actorOf(Props(classOf[MLHBot], eventBus), "MLHBot")
    }
  }
}
