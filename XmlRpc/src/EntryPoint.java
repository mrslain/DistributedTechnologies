public class EntryPoint {
    public static void main(String[] args) {
        RpcServer myServer = new RpcServer(3146);
        myServer.run();
    }
}
