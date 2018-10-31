package expensesCalculator;
/**
 * Created by Simon on 17.06.2017.
 */

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankPdf {
    private StringBuffer buff = new StringBuffer();
    private static String s;

    public static List<Double> listOfPayment = new ArrayList<>();
    public static List<String> listOfdate = new ArrayList<>();

    private static double convertNumber(Matcher m) {
        //Die Methode wandelt die PDF-Gleitkommazahl in eine Java-Zahl um/convertiert zu double
        String replace = m.group().replace( ".", "" ).replace( ",", "." );
        double amount = Double.parseDouble( replace );
        return amount;
    }

    private List<Double> guthaben(String s) {
        Matcher m = Pattern.compile( "[-+]?((\\d{1,3}[\\.]\\d{3})|\\d+)[,]\\d{2}" ).matcher( s );

        while (m.find()) {
            if (!m.group().startsWith( "-" ) && m.group().contains( "," )) {
                System.out.println( "JAVA Dezimalzahl: " + convertNumber( m ) );
                listOfPayment.add( convertNumber( m ) );

                DecimalFormat formatter = new DecimalFormat( "#,###.00" );

                System.out.println( "PDF/Excelzahl: " + formatter.format( convertNumber( m ) ));
                System.out.println( "\n" );
            }
        }
        return listOfPayment;
    }

    private List<Double> abzuege(String s) {
        Matcher m = Pattern.compile( "[-+]?((\\d{1,3}[\\.]\\d{3})|\\d+)[,]\\d{2}" ).matcher( s );

        while (m.find()) {
            if (m.group().startsWith( "-" ) && m.group().contains( "," )) {
                System.out.println( "JAVA Dezimalzahl: " + convertNumber( m ) );
                listOfPayment.add( convertNumber( m ) );

                DecimalFormat formatter = new DecimalFormat( "#,###.00" );

                System.out.println( "PDF/Excelzahl: " + formatter.format( convertNumber( m ) ) );
                System.out.println( "\n" );
            }
        }
        return listOfPayment;
    }

    public static List<String> datum(String s) {
        //In dem String "s" befindet sich jeweils der gesammte Text aus der jeweiligen PDF Seite ink. der "\n"
        Matcher matcher = Pattern.compile( "[-+]?((\\d{1,3}[\\.]\\d{3})|\\d+)[,]\\d{2}" ).matcher( s );
        int previewPositiv = 0, beforPositiv = 0;
        int previewNegativ = 0, beforNegativ = 0;
        boolean nichtKontostand = true;

        while (matcher.find()) {
            //Wenn ein positiver Betrag gefunden wird, wird dieser in den ersten Zwischenspeicher abgelegt
            if (!(matcher.group().startsWith( "-" )) && matcher.group().contains( "," )) {
                previewPositiv = matcher.start();
            }

            //Wenn ein negativer Betrag gefungen wird, wird dieser in den zweiten Zwischenspeicher abgelegt
            if (matcher.group().startsWith( "-" ) && matcher.group().contains( "," )) {
                previewNegativ = matcher.start();

                int x3 = 0;
                if (beforNegativ == 0 || (previewPositiv > beforNegativ)) {
                    x3 = matcher.start() - beforPositiv;
                } else {
                    x3 = matcher.start() - beforNegativ;
                }

                Matcher m2 = Pattern.compile( "(\\d{2}[\\.]\\d{2}[\\.]\\d{4})" ).matcher( s ).region( matcher.start() - x3, matcher.start() );
                while (m2.find()) {
                    listOfdate.add( m2.group() );
                    System.out.println( "Datum " + m2.group() + " " + "Betrag: " + matcher.group() );
                }
            }

            beforPositiv = previewPositiv;
            beforNegativ = previewNegativ;
        }
        return listOfdate;
    }


    public void PDFreadAll(int numberOfPages, PdfReader reader) throws Exception {
        for (int i = 1; i <= numberOfPages; i++) {
            s = PdfTextExtractor.getTextFromPage( reader, i );
            System.out.println( "Ausgabe des Split: " );

            buff.append( s + "\n" );
            String pdftext = buff.toString();
            System.out.println( "Ausgabe Seite " + i + "\n" + pdftext + "Ende der Seite " + i );
        }
    }

    public void PDFreadSum(int numberOfPages, PdfReader reader) throws Exception {
        for (int i = 1; i <= numberOfPages; i++) {
            s = PdfTextExtractor.getTextFromPage( reader, i );
            //guthaben(s);
            datum( s );
            abzuege( s );

            //Kein Datum für die positiven Beträge vorhanden
            //guthaben( s );
        }
    }

    public static void main(String[] args) throws Exception {
        PdfReader reader = new PdfReader("test.pdf");
        int numberOfPages = reader.getNumberOfPages();

        BankPdf test = new BankPdf();
        //test.PDFreadAll(numberOfPages, reader);
        test.PDFreadSum(numberOfPages, reader);
        //openFile();
        CreatExcelFile creatFile = new CreatExcelFile();
        creatFile.openFile();
    }
}


