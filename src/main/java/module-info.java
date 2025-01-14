module org.example.demo1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires com.google.gson;
    requires java.net.http;
    requires mysql.connector.j;
    requires jbcrypt;
    requires javax.websocket.api;
    requires tyrus.server;
    requires org.json;
    requires javafx.media;


    opens org.example.demo1 to javafx.fxml;
    exports org.example.demo1;
}