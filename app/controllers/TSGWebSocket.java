package controllers;

import play.Logger;
import play.mvc.Http.WebSocketClose;
import play.mvc.Http.WebSocketEvent;
import play.mvc.WebSocketController;

public class TSGWebSocket extends WebSocketController{
	
	public static void echo() {
//	    while(inbound.isOpen()) {
//	         WebSocketEvent e = await(inbound.nextEvent());
//	         
//	         for(String quit: TextFrame.and(Equals("quit")).match(e)) {
//	             outbound.send("Bye!");
//	             disconnect();
//	         }
//	 
//	         for(String message: TextFrame.match(e)) {
//	             outbound.send("Echo: %s", message);
//	         }
//	         
//	         for(WebSocketClose closed: SocketClosed.match(e)) {
//	             Logger.info("Socket closed!");
//	         }
//	    }
	}
	
	
}