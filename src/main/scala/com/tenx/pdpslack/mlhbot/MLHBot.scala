package com.tenx.pdpslack.mlhbot

import io.scalac.slack.MessageEventBus
import io.scalac.slack.bots.AbstractBot
import io.scalac.slack.common._

class MLHBot(override val bus: MessageEventBus) extends AbstractBot {

  override def help(channel: String): OutboundMessage =
    OutboundMessage(channel,
      s"$name will help you to solve MLH problems \\n" +
      "Usage: $approve pof")

  override def act: Receive = {
    //show broker commisions
    case Command("show", "broker" :: args, message) =>
      val arg = args(0)
      if (arg.startsWith("commission")) {
        publish(RichOutboundMessage(message.channel, List(
          Attachment(
            Title("Broker commission is 5%", Some("Refer to http://ten-x.com/broker for more details")),
            Color.good
          )
        )
        )
        )
      } else {
        publish(RichOutboundMessage(message.channel, List(
          Attachment(
            Title("Go to http://ten-x.com/broker for other details"),
            Color.good
          )
        )
        )
        )
      }

    case Command("approve", document, message) =>

      publish(RichOutboundMessage(message.channel, List(
        Attachment(
          Title("Buyer Proof Of Funds was approved", Some("http://ten-x.com")),
          Color.good,
          PreText("Buyer Proof Of Funds was approved " + message.channel ),
          Text("Now I'm talking with color and blocks " + document),
          Field("Field 1", "fill entire row", short = false),
          Field("Field 2", "fill half of the row" + message.user, short = true),
          Field("Field 3", "fill half od the row", short = true),
          Field("Field 4", "fill entire row")
        ),
        Attachment(Title("Good message"), Text("something like that")),
        Attachment(Color.warning, Field("Warning field", "taka sytuacja"))
      )
      )
      )

    case Command(_, _, message) =>
      publish(OutboundMessage(message.channel, s"No Operation \ arguments specified!"))
  }
}
