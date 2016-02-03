package com.wong;

import akka.actor.ActorRef;

public class Notification {
	public final ActorRef sender;
	public final ActorRef receiver;
	public final int id;
	public final Object obj;

	public Notification(ActorRef sender, ActorRef receiver, int id, Object obj) {
	  this.sender = sender;
	  this.receiver = receiver;
	  this.id = id;
	  this.obj = obj;
	}
}
