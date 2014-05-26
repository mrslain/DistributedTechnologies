import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.server.XmlRpcStreamServer;
import org.apache.xmlrpc.webserver.WebServer;

public class RpcServer implements Runnable{
    private int port;

    public RpcServer(int port) {
        this.port = port;
    }

    public void run() {
        try {
            WebServer webServer = new WebServer(port);

            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            phm.addHandler(Solver.class.getName(), Solver.class);

            XmlRpcStreamServer xmlRpcServer = webServer.getXmlRpcServer();
            xmlRpcServer.setHandlerMapping(phm);

            XmlRpcServerConfigImpl serverConfig =
                    (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
            serverConfig.setEnabledForExtensions(true);
            serverConfig.setContentLengthOptional(false);

            webServer.start();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("---------->");
            e.printStackTrace();
        }
    }
}
