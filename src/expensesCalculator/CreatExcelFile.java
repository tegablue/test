package expensesCalculator;
/**
 * Created by Simon on 24.03.2018.
 */

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

import static java.nio.file.Files.delete;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class CreatExcelFile extends BankPdf {

    //Blank workbook
    private static XSSFWorkbook workbook = new XSSFWorkbook();
    //Create a blank sheet
    private static XSSFSheet sheet = workbook.createSheet( "Tabelle der Ausgaben 2017" );
    //List
    private static List<Integer> list = new ArrayList<>();


    private static Map<Integer, Object[]> putToDataPool() {
        //This data needs to be written (Object[])
        Map<Integer, Object[]> dataPool = new TreeMap<>();
        int positionInDataPool = 0;
        for (int i = 0; i < listOfPayment.size(); i++) {
            dataPool.put( i, new Object[]{listOfdate.get( positionInDataPool ), listOfdate.get( positionInDataPool + 1 ), listOfPayment.get( i )} );
            positionInDataPool += 2;
        }
        return dataPool;
    }

    private static int summeDerAusgaben(XSSFWorkbook workbook, XSSFSheet sheet, List<Integer> list, XSSFCell cell, int lastRowNumPreviwe, int cellnumPreviwe, int rowCounter, XSSFCell work, int existingRow){
        //Beim einmaligen Monatswechsel wird an "lastRowNum" die Zahl der geschriebenen Reihen übergeben
        int lastRowNum = workbook.getSheetAt( 0 ).getPhysicalNumberOfRows();

        //Es kann noch in keine bereits generierte Zeile geschrieben werden
        if(lastRowNum > lastRowNumPreviwe ){

            //Letzter Cellenwert im Monat (und gibt den Zellenbuchstaben zurück)
            String lastCell = cell.getReference();
            String firstCell = lastCell.split("\\d+")[0] + "1";

            //Kreiert Row für die zu berechnende Summe
            sheet.createRow( rowCounter ).createCell( cellnumPreviwe ).setCellFormula( "SUM(" + lastCell + ":" + firstCell + ")" );
            list.add( rowCounter );

            //Da noch eine Row für die Summe kreiert wurde
            lastRowNum = workbook.getSheetAt( 0 ).getPhysicalNumberOfRows();

            rowCounter++;
        }

        if(lastRowNum == lastRowNumPreviwe || lastRowNum < lastRowNumPreviwe){

            //Letztes Cellenwert im Monat (und gibt den Zellenbuchstaben zurück)
            String lastCell = work.getReference();
            String firstCell = lastCell.split("\\d+")[0] + "1";

            workbook.getSheetAt( 0 ).getRow( list.get( existingRow ) ).createCell( cellnumPreviwe ).setCellFormula( "SUM(" + lastCell + ":" + firstCell + ")" );
        }
        return lastRowNum;
    }


    public static void openFile() throws Exception {

        String month = "";
        String nextMonth = "";
        Row row;
        XSSFCell cell = null;
        XSSFCell work = null;

        int rowCounter = 0;
        int existingRow = 0;
        int lastRowNum = 0;
        int cellJump = 0;
        int cellnumPreviwe = 0;
        int lastRowNumPreviwe = 0;

        //KeySet
        Set<Integer> keyset = putToDataPool().keySet();

        for (Integer key : keyset) {
            Object[] objArr = putToDataPool().get( key );
            int cellnum = 0;

            //Ermittelt aus dem ausgelesenem Datum den month
            if (objArr[0] instanceof String) {
                if (objArr[0].toString().contains( "." )) {
                    Object[] date = objArr[0].toString().split( Pattern.quote( "." ) );
                    month = date[1].toString();
                    System.out.println("Im Preview: " + month);
                }
            }

            //Prüft ob ein Monatswechsel stattgefunden hat und übergibt die Anzahl der bereits geschriebenen Reihen (row's)
            if ((!month.equals( nextMonth ) && !nextMonth.equals( "" ))) {
                lastRowNum = summeDerAusgaben( workbook, sheet, list, cell, lastRowNumPreviwe, cellnumPreviwe, rowCounter, work, existingRow );
                lastRowNumPreviwe = lastRowNum;
                //In der gleichen Reihe jeweils um 4 Stellen nach rechts springen
                cellJump += 4;
                existingRow = 0;
            }

            //Wenn noch keine Zeile geschrieben wurde
            if (lastRowNum == 0) {
                row = sheet.createRow( rowCounter);
                list.add( rowCounter );

                //Startet bei 0 und springt dann durch (cellJump+=4;) immer um 4 Stellen nach rechts
                cellnum+=cellJump;

                for (Object obj : objArr) {
                    cell = (XSSFCell) row.createCell( cellnum++ );
                    if (obj instanceof String)
                        cell.setCellValue( (String) obj );
                    else if (obj instanceof Double)
                        cell.setCellValue( (Double) obj );
                }
                rowCounter++;
            }

            //Wenn Zeile/Zeilen schon vorhanden sind
            if (lastRowNum != 0) {
                //Startet bei 0 und springt dann durch (cellJump+=4;) immer um 4 Stellen nach rechts
                cellnum+=cellJump;

                for (Object obj : objArr) {
                    work = workbook.getSheetAt( 0 ).getRow( list.get( existingRow ) ).createCell( cellnum++ );
                    if (obj instanceof String)
                        work.setCellValue( (String) obj );
                    else if (obj instanceof Double)
                        work.setCellValue( (Double) obj );
                }
                existingRow++;
                lastRowNum--;
            }
            cellnumPreviwe = cellnum-1;
            nextMonth = month;
        }

        //Zur Berechnung der letzten Summe wenn alle Monate durchlaufen sind
        summeDerAusgaben(workbook,sheet,list,cell, lastRowNumPreviwe,cellnumPreviwe,rowCounter,work,existingRow);

        try {
            clearExcelFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void clearExcelFile() throws IOException {

        File file;
        if (( file = new File( "howtodoinjava_demo.xlsx" )).exists()) {
            delete( file.toPath() );
/*
            FileInputStream inputStream = new FileInputStream( file );
            HSSFWorkbook wb = new HSSFWorkbook(inputStream);
            wb.removeSheetAt( 0 );
            inputStream.close();*/
        }
        //Write the workbook in file system
        FileOutputStream excelOutputStream = new FileOutputStream( file );
        workbook.write( excelOutputStream );
        excelOutputStream.close();
        System.out.println( "howtodoinjava_demo.xlsx written successfully on disk." );
    }
}
