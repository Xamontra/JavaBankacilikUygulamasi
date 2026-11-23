module com.example.javabankacilikuygulamasi {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.example.javabankacilikuygulamasi to javafx.fxml;
    exports com.example.javabankacilikuygulamasi;
}