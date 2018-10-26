/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package expensesCalculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Simon
 */
public class JavaFX extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource( "FXMLTextField_GUI.fxml" ));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static class test {
        private double x = 0;

        public test(double x){
            this.x = x;
        }

        public void setX(double x){
            this.x = x;
        }

        public double getX(){
            return x;
        }


        public static void main(String[] args) {
            List<String> stringList = new ArrayList<>(  );
            stringList.add( "a" );
            stringList.add( "b" );
            stringList.add( "c" );

            stringList.forEach( ausList -> System.out.println("Ausgabe: " + ausList) );


            test test = new test(44);

            System.out.println(test.getX());

            test.setX( 66 );

            System.out.println(test.getX());
        }
    }
}
