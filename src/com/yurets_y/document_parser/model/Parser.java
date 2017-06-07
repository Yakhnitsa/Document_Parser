package com.yurets_y.document_parser.model;

import com.yurets_y.document_parser.bin.RailroadDocument;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Yuriy on 29.06.2016.
 */
public interface Parser {
    /**
     * Загружает жд документ из файла по url ссылке на xml файл
     * @param url
     * @return ЖД Документ
     * @throws IOException
     * @throws ParseException
     */
    RailroadDocument parseFromURL(String url) throws IOException, ParseException;

    /**
     * Загружает жд документ из файла
     * @param file
     * @return ЖД документ
     * @throws IOException
     * @throws ParseException
     */
    RailroadDocument parseFromFile(File file) throws IOException, ParseException;

}
