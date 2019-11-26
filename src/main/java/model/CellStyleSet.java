package model;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.DateFormatConverter;

import java.util.Locale;

/**
 * Created by Yuriy on 21.08.2016.
 * Служебный клас для определения формата ячеек
 */
class CellStyleSet {

    static CellStyle getDateStyle(Workbook workbook){
        String excelFormatPattern = DateFormatConverter.convert(Locale.ROOT, "dd.MM.yyyy");
        CellStyle cellStyle = workbook.createCellStyle();
        DataFormat poiFormat = workbook.createDataFormat();
        cellStyle.setDataFormat(poiFormat.getFormat(excelFormatPattern));
        return cellStyle;
    }

}
