package com.yurets_y.document_parser.util;

import com.yurets_y.document_parser.bin.RDocEnum;
import com.yurets_y.document_parser.bin.RDocEnumOrderWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 27.04.2017.
 * Класс для чтения получения порядка колонок из строки или текстового файла
 */
public class RDocEnumLoader {
    private static RDocEnumLoader instance;

    public static RDocEnumLoader getInstance(){
        if(instance == null){
            instance = new RDocEnumLoader();
        }
        return instance;
    }

    public void saveOrderToXML(RDocEnum[] order, File file) throws IOException,JAXBException{
        JAXBContext context = JAXBContext
                .newInstance(RDocEnumOrderWrapper.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        // Обёртываем наши данные об адресатах.
        RDocEnumOrderWrapper wrapper = new RDocEnumOrderWrapper();
        wrapper.setOrder(order);

        // Маршаллируем и сохраняем XML в файл.
        m.marshal(wrapper, file);
    }

    public RDocEnum[] loadOrderFromXML(File file) throws IOException,JAXBException{
        JAXBContext context = JAXBContext.newInstance(RDocEnumOrderWrapper.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        RDocEnumOrderWrapper wrapper = (RDocEnumOrderWrapper) unmarshaller.unmarshal(file);
        return wrapper.getOrder();
    }


}
