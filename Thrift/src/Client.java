import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;


public class Client {
    public static void main(String[] args) {
        try {
            Thread.sleep(2000);
            TTransport transport = new TSocket("localhost", 3146);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            myservice.MyService.Client client = new myservice.MyService.Client(protocol);

            perform(client);

            transport.close();

        } catch (TException x) {
            x.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void perform(myservice.MyService.Client client) throws TException {
        String htmlText = client.get_htmlPage("http://urfu.ru");

        org.jsoup.nodes.Document doc = Jsoup.parse(htmlText);

        Elements links = doc.select("a[href]");

        System.out.println(links.size());
    }
}
