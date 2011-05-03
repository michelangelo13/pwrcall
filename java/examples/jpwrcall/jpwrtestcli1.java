import jpwrcall.Node;
import jpwrcall.Util;
import jpwrcall.Promise;
import jpwrcall.RPCConnection;

import org.msgpack.MessagePackObject;

/**
 * 
 * @author Mark Schloesser
 */
public class jpwrtestcli1 {
	private static class OnResult implements Util.Callback {
		public void cb(Object r) {
			System.out.println("on_result " + r.toString());
			MessagePackObject rm = (MessagePackObject)r;
			if (rm.isIntegerType())
				System.out.println("received result: " + rm.asInt());
			System.exit(0);
		}
	}

	private static class OnConnected implements Util.Callback {
		private String ref;

		public OnConnected(String ref) {
			this.ref = ref;
		}

		public void cb(Object r) {
			System.out.println("on_connected " + r.toString());

			RPCConnection rc = (RPCConnection) r;
			Promise p = rc.call(this.ref, "add", 25, 75);
			p.when(new OnResult());
			p.except(new OnError());
		}
	}

	private static class OnError implements Util.Callback {
		public void cb(Object r) {
			System.out.println("on_error " + r.toString());
		}
	}

	public static void main(String[] args) {
		Node n = new Node("cert_t1.jks");
		Promise p = n.connect("127.0.0.1", 10000);
		p.when(new OnConnected(args[0]));
		p.except(new OnError());
	}

}