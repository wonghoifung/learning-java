package com.wong;

import java.net.InetSocketAddress;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.Tcp.Bound;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.io.TcpMessage;
import akka.japi.Procedure;
import akka.util.ByteString;

public class SimplisticHandler extends UntypedActor {
  @Override
  public void onReceive(Object msg) throws Exception {
    if (msg instanceof Received) {

      final ByteString data = ((Received) msg).data();
      System.out.println(data);
      getContext().system().eventStream().publish(new Notification(getSender(), getSelf(), 1, data));

    } else if (msg instanceof ConnectionClosed) {

      getContext().stop(getSelf());

    } else if (msg instanceof Notification) {

      Notification noti = (Notification)msg;
      // TODO while the below statement don't broadcast ?
      if (noti.id == 1) 
        noti.sender.tell(TcpMessage.write((ByteString)(noti.obj)), noti.receiver);

    }
  }
}

