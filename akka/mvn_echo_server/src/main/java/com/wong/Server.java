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

public class Server extends UntypedActor {
  
  final ActorRef manager = Tcp.get(getContext().system()).manager();
  
  public static Props props(ActorRef manager) {
    return Props.create(Server.class, manager);
  }
 
  @Override
  public void preStart() throws Exception {
    final ActorRef tcp = Tcp.get(getContext().system()).manager();
    tcp.tell(TcpMessage.bind(getSelf(), new InetSocketAddress("0.0.0.0", 8888), 100), getSelf());
  }
 
  @Override
  public void onReceive(Object msg) throws Exception {
    if (msg instanceof Bound) {
      manager.tell(msg, getSelf());
 
    } else if (msg instanceof CommandFailed) {
      getContext().stop(getSelf());
    
    } else if (msg instanceof Connected) {
      final Connected conn = (Connected) msg;
      manager.tell(conn, getSelf());
      final ActorRef handler = getContext().actorOf(Props.create(SimplisticHandler.class));
      getContext().system().eventStream().subscribe(handler, Notification.class);
      getSender().tell(TcpMessage.register(handler), getSelf());
    }
  }
  
}


