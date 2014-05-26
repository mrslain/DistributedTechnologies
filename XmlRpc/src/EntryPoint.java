public class EntryPoint {
    public static void main(String[] args) {
        try {
            new ServerRunner().run(3146);
            new RpcClient(3146).makeRequests(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
