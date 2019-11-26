package model;

import bin.RailroadDocument;

import java.io.File;
import java.util.List;

/**
 * Created by Admin on 26.08.2017.
 */
public interface FilesRenamer {
    List<String> renameFiles(List<File> files, List<RailroadDocument> documents);
}
