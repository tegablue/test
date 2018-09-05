package expensesCalculator;

import java.util.ArrayList;
import java.util.List;

public class test {

    public static void main(String[] args) {
        List<String> stringList = new ArrayList<>(  );
        stringList.add( "a" );
        stringList.add( "b" );
        stringList.add( "c" );

        stringList.forEach( ausList -> System.out.println("Ausgabe: " + ausList) );
    }
}
