package com.yurets_y.document_parser.model;


import com.yurets_y.document_parser.util.ExceptionHandler;
import com.yurets_y.document_parser.bin.RDocEnum;
import com.yurets_y.document_parser.bin.RailroadDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;

/**
 * Класс для сохранения документов в книгу Excel
 */
public class DocumentSaver implements Saver {
    private static final String docType = ".xls";

    /**
     * Сохраняет группу документов в один файл ексель
     *
     * @param documents   - список документов для сохранения
     * @param file        - путь к файлу для сохранения
     * @param columnOrder - поряток столбцов в файле сохранения
     * @return - int - колисество успешно сохраненных документов.
     */
    @Override
    public int saveGroupToFile(List<RailroadDocument> documents, File file, RDocEnum... columnOrder) {
        if (!file.getName().toLowerCase().endsWith(docType)) {
            String fileName = file.getAbsolutePath() + docType;
            file = new File(fileName);
        }
        int docCount = documents.size();
        try (OutputStream fileOutStream = new FileOutputStream(file)) {
            Workbook workbook = getWorkbookFromRailDocsList(documents, columnOrder);
            workbook.write(fileOutStream);
        } catch (IOException e) {
            ExceptionHandler.handleException(e);
        }
        return docCount;
    }

    /**
     * Сохраняет группу документов по одному файлу в директорию
     *
     * @param documents   - список документов
     * @param folder      - путь для сохранения
     * @param columnOrder - подярок столбцов для документа
     * @return int - количество успешно сохраненных документов.
     */
    @Override
    public int saveGroupToFolder(List<RailroadDocument> documents, File folder, RDocEnum... columnOrder) {
        if (!folder.isDirectory())
            throw new IllegalArgumentException("Указанный путь не является папкой!!!");
        StringBuilder exceptList = new StringBuilder();
        int docCount = 0;
        for (RailroadDocument document : documents) {
            try {
                saveToFolder(document, folder, columnOrder);
                docCount++;
            } catch (IOException e) {
                exceptList.append(e.getCause().toString()).append(" - ").append(e.getMessage());
            }
        }
        ExceptionHandler.showMessage(exceptList.toString());

        return docCount;
    }

    /**
     * Сохраняет отдельный документ в папку, присваивая ему имя
     *
     * @param document
     * @param folder
     * @param columnOrder
     * @throws IOException - в случаи если не удается сохранить файл.
     * @see DocumentSaver#getFileName(RailroadDocument)
     */
    private void saveToFolder(RailroadDocument document, File folder, RDocEnum... columnOrder) throws IOException {
        if (!folder.isDirectory())
            throw new IllegalArgumentException("Указанный путь не является папкой!!!");
        String fileName = getFileName(document);
        File file = Paths.get(folder.getAbsolutePath(), fileName).toFile();

        try (OutputStream fileOutStream = new FileOutputStream(file)) {
            Workbook workbook = getWorkbookFromRailDoc(document, columnOrder);
            workbook.write(fileOutStream);
        }

    }

    /*
     * Переформатирование жд документа в книгу ексель
     */
    private Workbook getWorkbookFromRailDoc(RailroadDocument railroadDocument, RDocEnum... columnOrder) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");
        //Создание строки заголовков:
        Row mainRow = sheet.createRow(0);
        for (int i = 0; i < columnOrder.length; i++) {
            mainRow.createCell(i).setCellValue(columnOrder[i].toString());
        }
        //Сохранение данных в книгу
        int i = 0;
        int weightPosition = -1;
        while (i < railroadDocument.getVagonList().size()) {
            Row currentRow = sheet.createRow(++i);
            //Создание и сохранение данных для каждой ячейки
            for (int j = 0; j < columnOrder.length; j++) {
                Cell cell = currentRow.createCell(j);
                RDocEnum type = columnOrder[j];
                if (type == RDocEnum.VAGON_NET_WEIGHT)
                    weightPosition = j;
                setSpecifiedValueToCell(cell, railroadDocument, columnOrder[j], i - 1, workbook);
            }
        }
        //Запись суммарного значения массы в последней строке документа
        Row finalRow = sheet.createRow(i + 1);

        if (weightPosition != -1) {
            Cell finalCell = finalRow.createCell(weightPosition);
            finalCell.setCellValue(railroadDocument.getFullVeightToString());
        }

        return workbook;
    }

    /*
     * Переформатирование списка жд документов в книгу ексель
     */
    private Workbook getWorkbookFromRailDocsList(List<RailroadDocument> documentList, RDocEnum... columnOrder) {
        if (documentList == null) {
            throw new NullPointerException("Исходящий список документов не инициализирован!!!");
        }
        if (columnOrder == null) {
            throw new NullPointerException("Порядок колонок для экспорта не инициализирован!!!");
        }
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");
        //Создание строки заголовков:
        Row mainRow = sheet.createRow(0);
        for (int i = 0; i < columnOrder.length; i++) {
            mainRow.createCell(i).setCellValue(columnOrder[i].toString());
        }
        //Сохранение данных каждого жд документа в книгу
        int rowNumb = 1;
        for (RailroadDocument document : documentList) {
            for (int vagNumb = 0; vagNumb < document.getVagonList().size(); vagNumb++) {
                Row currentRow = sheet.createRow(rowNumb);
                //Создание и сохранение данных для каждой ячейки в строке
                for (int j = 0; j < columnOrder.length; j++) {
                    Cell cell = currentRow.createCell(j);
                    setSpecifiedValueToCell(cell, document, columnOrder[j], vagNumb, workbook);
                }
                rowNumb++;
            }
        }
        return workbook;
    }

    /**
     * Устанавливает необходимое значение в передаваемую ячейку в зависимости от типа
     *
     * @param cell    - Записываемая ячейка
     * @param railDoc - Документ с данными
     * @param column  - тип столбца из перечисляемого типа
     * @param vagNumb - номер вагона по порядку
     */
    private void setSpecifiedValueToCell(Cell cell, RailroadDocument railDoc, RDocEnum column, int vagNumb, Workbook workbook) {
        switch (column) {

            case ORDER_NUMBER:
                cell.setCellValue(vagNumb + 1);
                break;
            case DOC_NUMBER:
                cell.setCellValue(railDoc.getDocNumber());
                break;
            case DOC_DATE:
                cell.setCellStyle(CellStyleSet.getDateStyle(workbook));
                if (railDoc.getDocDate() == null) break;
                cell.setCellValue(railDoc.getDocDate());
                break;
            case DOC_DELIV:
                cell.setCellStyle(CellStyleSet.getDateStyle(workbook));
                if (railDoc.getDocDate() == null) break;
                cell.setCellValue(railDoc.getDelDate());
                break;
            case DOC_CRED:
                cell.setCellStyle(CellStyleSet.getDateStyle(workbook));
                if (railDoc.getDocDate() == null) break;
                cell.setCellValue(railDoc.getCredDate());
                break;
            case SEND_STATION_NAME:
                cell.setCellValue(railDoc.getSendStation().getName());
                break;
            case SEND_STATION_CODE:
                cell.setCellValue(railDoc.getSendStation().getCode());
                break;
             case SEND_STATION_NAME_CODE:
                cell.setCellValue(railDoc.getSendStation().getNameAndCode());
                break;
            case RECEIVE_STATION_NAME:
                cell.setCellValue(railDoc.getReceiveStation().getName());
                break;
            case RECEIVE_STATION_CODE:
                cell.setCellValue(railDoc.getReceiveStation().getCode());
                break;
            case RECEIVE_STATION_NAME_CODE:
                cell.setCellValue(railDoc.getReceiveStation().getNameAndCode());
                break;
            case OUT_STATION_NAME:
                cell.setCellValue(railDoc.getOutStation().getName());
                break;
            case OUT_STATION_CODE:
                cell.setCellValue(railDoc.getOutStation().getCode());
                break;
            case OUT_STATION_NAME_CODE:
                cell.setCellValue(railDoc.getOutStation().getNameAndCode());
                break;
            case INN_STATION_NAME:
                cell.setCellValue(railDoc.getInnStation().getName());
                break;
            case INN_STATION_CODE:
                cell.setCellValue(railDoc.getInnStation().getCode());
                break;
            case INN_STATION_NAME_CODE:
                cell.setCellValue(railDoc.getInnStation().getNameAndCode());
                break;
            case SENDER_NAME:
                cell.setCellValue(railDoc.getCargoSender().getName());
                break;
            case SENDER_ADDRESS:
                cell.setCellValue(railDoc.getCargoSender().getAddress());
                break;
            case SENDER_RAILROAD_CODE:
                cell.setCellValue(railDoc.getCargoSender().getRailroadCode());
                break;
            case SENDER_EDRPU_CODE:
                cell.setCellValue(railDoc.getCargoSender().getEdrpuCode());
                break;
            case SENDER_NAME_CODE:
                cell.setCellValue(railDoc.getCargoSender().getCodeAndName());
                break;
            case RECEIVER_NAME:
                cell.setCellValue(railDoc.getCargoReceiver().getName());
                break;
            case RECEIVER_ADDRESS:
                cell.setCellValue(railDoc.getCargoReceiver().getAddress());
                break;
            case RECEIVER_RAILROAD_CODE:
                cell.setCellValue(railDoc.getCargoReceiver().getRailroadCode());
                break;
            case RECEIVER_EDRPU_CODE:
                cell.setCellValue(railDoc.getCargoReceiver().getEdrpuCode());
                break;
            case TARIF_PAYER_NAME:
                cell.setCellValue(railDoc.getTarifPayer().getName());
                break;
            case PAYER_CODE:
                cell.setCellValue(railDoc.getTarifPayer().getRailroadCode());
                break;
            case VAGON_NUMBER:
                cell.setCellValue(railDoc.getVagonList().get(vagNumb).getNumber());
                break;
            case VAGON_GROSS_WEIGHT:
                cell.setCellValue(railDoc.getVagonList().get(vagNumb).getGrossVeight());
                break;
            case VAGON_NET_WEIGHT:
                cell.setCellValue(railDoc.getVagonList().get(vagNumb).getNetVeight());
                break;
            case VAGON_TARE_WEIGTH:
                cell.setCellValue(railDoc.getVagonList().get(vagNumb).getTareVeight());
                break;
            case VAGON_CARRYING_CAPASITY:
                cell.setCellValue(railDoc.getVagonList().get(vagNumb).getCarryingCapasity());
                break;
            case RECEIVER_NAME_CODE:
                cell.setCellValue(railDoc.getCargoReceiver().getCodeAndName());
                break;
            case CARGO_NAME:
                cell.setCellValue(railDoc.getCargoName());
                break;
            case CARGO_CODE:
                cell.setCellValue(railDoc.getCargoCode());
                break;
            case VOID_SPACE:
                cell.setCellValue("");
                break;
            case PAYMENT_SUMM:
                if(vagNumb==railDoc.getVagonCount()-1)
                    cell.setCellValue((double)railDoc.getPayment()/100);
                break;
            case COLUMN_7_INFO:
                cell.setCellValue(railDoc.getColumn7info());
                break;
            case COLUMN_15_INFO:
                cell.setCellValue(railDoc.getColumn15info());
                break;

            default:
                cell.setCellValue("тип данных не определен!!!");
        }
    }

    /**
     * Служит для получения названия файла для сохранения документа
     *
     * @param railDoc - ЖД документ, с которого будем лепить название
     * @return название файла для сохранения
     */
    private String getFileName(RailroadDocument railDoc) {
        return railDoc.getDocNumber() + railDoc.getSendStation().getNameAndCode() + docType;
    }

}
