package model;


import bin.RDocEnum;
import bin.RailroadDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Yuriy on 29.06.2016.
 */
public interface Saver {
    /**
     *
     * @param docs
     * @param file
     * @param columnOrder
     * @return int - Количество успешно сохраненных документов
     * @throws IOException
     */
    int saveGroupToFile(List<RailroadDocument> docs, File file, RDocEnum... columnOrder);

    /**
     *
     * @param documents
     * @param folder
     * @param columnOrder
     * @return int - Количество успешно сохраненных документов
     */
    int saveGroupToFolder(List<RailroadDocument> documents, File folder, RDocEnum... columnOrder);

}
