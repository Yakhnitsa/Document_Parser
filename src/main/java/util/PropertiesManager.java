package util;

import bin.RDocEnum;

import java.io.*;
import java.util.Properties;

/**
 * Created by Admin on 09.03.2017.
 */
public class PropertiesManager {

    private static PropertiesManager instanse;
    private static String PROP_FILE_PATH = "res\\properties.properties";

    private Properties properties;

    private PropertiesManager() {

        properties = new Properties();

        try (FileInputStream fis = new FileInputStream(PROP_FILE_PATH);
             InputStreamReader innStrRead = new InputStreamReader(fis, "UTF-8")) {
            properties.load(innStrRead);
        } catch (IOException e) {
            String message = "Ошибка загрузки файла конфигурации";
            String content = "Проверьте доступ к файлу:" + System.lineSeparator() + PROP_FILE_PATH;
            MessageManager.getInstance().showErrorMessage(null,message,content);
        }
    }

    public static PropertiesManager getInstance() {
        if (instanse == null) {
            instanse = new PropertiesManager();
        }
        return instanse;
    }

    public File getDefaultAddFolder() {
        String filePath = properties.getProperty("def.add.folder");
        if ((filePath == null) || (filePath.equals(""))) {
            return null;
        }
        File file = new File(filePath);
        if (file.exists()) return file;
        return null;
    }

    public File getDefaultSaveFolder() {
        String filePath = properties.getProperty("def.save.folder");
        if ((filePath == null) || (filePath.equals(""))) {
            return null;
        }
        File file = new File(filePath);
        if (file.exists()) return file;
        return null;
    }

    public RDocEnum[] getDefaultColumnOrder() {
        String filePath = properties.getProperty("def.order.file");
        File file = new File(filePath);
        try{
            RDocEnum[] order = RDocEnumLoader.getInstance().loadOrderFromXML(file);
            return order;
        }catch (Exception e){
            e.printStackTrace();
            String message = "Ошибка загрузки файла конфигурации";
            String context = "Восстановлен порядок колонок по умолчанию";
            MessageManager.getInstance().showErrorMessage(null,message,context);
            return RDocEnum.values();
        }


    }

    public String getProgramName() {
        return properties.getProperty("prog.name");
    }

    public void setDefaultAddFolder(File file) {
        if (!file.exists()) return;
        properties.setProperty("def.add.folder", file.getAbsolutePath());
        savePropertyFile();
    }

    public void setDefaultSaveFolder(File file) {
        if (!file.exists()) return;
        properties.setProperty("def.save.folder", file.getAbsolutePath());
        savePropertyFile();
    }
    public void setDefaultColumnOrderFile(File file){
        properties.setProperty("def.order.file", file.getAbsolutePath());
        savePropertyFile();
    }

    private void savePropertyFile() {
        try (FileOutputStream fileOut = new FileOutputStream(PROP_FILE_PATH);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOut, "UTF-8"));
        ) {
            properties.store(writer, "");
        } catch (IOException e) {
            String message = "Ошибка сохранения файла конфигурации";
            String content = "Проверьте доступ к файлу:" + System.lineSeparator() + PROP_FILE_PATH;
            MessageManager.getInstance().showErrorMessage(null,message,content);
        }
    }

    public File getDefaultColumnOrderFile(){
        return new File(properties.getProperty("def.order.file"));
    }

}
