package expensesCalculator;


import com.itextpdf.text.pdf.PdfReader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.nio.file.Files.*;

public class FXMLTextFieldController implements Initializable {

    private static ArrayList<String> datein = new ArrayList<>();

    @FXML
    private ListView<String> area;

    @FXML
    private ListView<String> area1;

    @FXML
    private TextField textFieldNome;

    @FXML
    private AnchorPane textFieldIdade;

    @FXML
    private Button buttonInputDatei;

    @FXML
    private Button buttonRight;

    @FXML
    private Button buttonLeft;

    @FXML
    private Button startReading;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Testsystem");
        getArea();
        // TODO
    }


    public void getArea() {
        area.getSelectionModel().selectedItemProperty()
                .addListener( new ChangeListener<String>() {
                    public void changed(
                            ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {

                        System.out.println( "TEST: " + newValue );
                    }
                } );
    }


    public static List<Path> transformDirectoryToFile(Path path) throws IOException {
        final PathMatcher pathMatcher = path.getFileSystem().getPathMatcher("glob:**/*.pdf");
        List<Path> paths = new ArrayList<>(  );

        walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (pathMatcher.matches(file)) {
                    paths.add( file );
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return paths;
    }


    public void moveDateiRight(ActionEvent actionEvent) {

        ObservableList<String> items;
        if (buttonRight == actionEvent.getSource()) {
            System.out.println( "Gewählt: " + area.getSelectionModel().getSelectedItem() );

            items = FXCollections.observableArrayList( area.getSelectionModel().getSelectedItem() );

            if (items != null) {
                area1.getItems().add( area1.getItems().size(), items.toString().replace( "[", "" ).replace( "]", "" ) );
                area.getItems().remove( area.getSelectionModel().getSelectedIndex() );
            }
        }
    }


    public void moveDateiLeft(ActionEvent actionEvent) {
        ObservableList<String> items;
        if (buttonLeft == actionEvent.getSource()) {
            System.out.println( "Gewählt: " + area1.getSelectionModel().getSelectedItem() );

            items = FXCollections.observableArrayList(area1.getSelectionModel().getSelectedItem());

            if (items != null) {
                area.getItems().add( area.getItems().size(), items.toString().replace( "[", "" ).replace( "]", "" ) );
                area1.getItems().remove( area1.getSelectionModel().getSelectedIndex() );
            }
        }
    }


    @FXML
    void startReading(ActionEvent event) throws Exception {
        if (startReading == event.getSource()) {
            area1.refresh();
            for (String file : area1.getItems()) {
                PdfReader reader = new PdfReader(file);
                System.out.println("Gelesen wird aus: " + reader.getInfo());

                int numberOfPages = reader.getNumberOfPages();

                BankPdf test = new BankPdf();
                //test.PDFreadAll(numberOfPages, reader);
                test.PDFreadSum(numberOfPages, reader);
                //openFile();

                CreatExcelFile creatFile = new CreatExcelFile();
                creatFile.openFile();
            }
        }
    }


    public void readDateiPfad(ActionEvent actionEvent) throws IOException {
        ObservableList<String> items;
        if(buttonInputDatei == actionEvent.getSource()){
            if(textFieldNome.getText().isEmpty()){
                System.out.println("Keine Eingabe im Textfeld!");
            } else {
                Path path = Paths.get( textFieldNome.getText() );
                if (!exists( path )) {
                    System.out.println( "FEHLER! " + path + " existiert nicht!" );
                } else {
                    if (isDirectory( path )) {
                        for (Path path1 : transformDirectoryToFile( path )) {
                            System.out.println( path1.getFileName().toString() );
                            datein.add( path1.getFileName().toString() );
                        }
                        //Alle Datein in der Liste anzeigen
                        items = FXCollections.observableArrayList(datein);
                        area.setItems(items);

                    } else {
                        //transformFile( path );
                    }
                }
            }
        }
    }
}







