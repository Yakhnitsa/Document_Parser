package com.yurets_y.document_parser.model;


import com.yurets_y.document_parser.bin.RailroadDocument;

import com.yurets_y.document_parser.util.ExceptionHandler;
import com.yurets_y.document_parser.util.MessageManager;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;


/**
 * Парсер XML формы документа
 */
public class DocumentParser implements Parser {
    /*
     * Парсинг жд документа из URL ссылки
     */
    @Override
    public RailroadDocument parseFromURL(String url) throws IOException, ParseException {
        Connection connection = Jsoup.connect(url);
        Document htmlDoc = connection.get();

        return parseDocument(htmlDoc);
    }

    /*
     * Парсинг жд документа из файла
     */
    @Override
    public RailroadDocument parseFromFile(File file) throws IOException, ParseException {
        Document document = Jsoup.parse(file, "UTF-8");
        if(!file.getName().toLowerCase().endsWith(".xml")){
            throw new IOException("Неизвесный формат файла!!!");
        }
        RailroadDocument railDoc = parseDocument(document);
        if(railDoc != null){
            updateDocument(document,railDoc);
        }

        return railDoc;
    }

    /*
     * Парсинг данных по документу из представления xml документа
     */
    private RailroadDocument parseDocument(Document jSoupDocument) throws ParseException {
        RailroadDocument railDoc = new RailroadDocument();

        railDoc.setDocNumber(jSoupDocument.getElementsByAttribute("nom_doc").attr("nom_doc"));
        railDoc.setDocDate(jSoupDocument.getElementsByAttribute("date_otpr").attr("date_otpr").toString());
        //Добавление учасников трансп процесса:
        RailroadDocument.Participant sender = parseAndAddParticipants(jSoupDocument.getElementsByAttributeValue("type", "1"), railDoc);
        railDoc.setCargoSender(sender);
        RailroadDocument.Participant receiver = parseAndAddParticipants(jSoupDocument.getElementsByAttributeValue("type", "2"), railDoc);
        railDoc.setCargoReceiver(receiver);
        //добавление инфо про плательщика

        RailroadDocument.Participant tarifPayer = parseTarifPayer(jSoupDocument.getElementsByAttributeValue("type", "0"));
        railDoc.setTarifPayer(tarifPayer);
        //добавление инфо про маршрут:
        parseAndAddStations(jSoupDocument, railDoc);
        //добавление информации о перевозчике
        parseAndAddCarriers(jSoupDocument,railDoc);
        //добавление инфо по вагонам:
        parseAndAddVagonsToDoc(jSoupDocument, railDoc);
        //Добавление инфо по грузу:
        parseAndAddCarriageInfoToDoc(jSoupDocument, railDoc);
        //Определение платы по документу
        String payment = jSoupDocument.getElementsByAttributeValue("type_pay", "0").attr("osum");
        railDoc.setPayment(payment);
        //Определение информации 7 и 15 графы
        String column7 = jSoupDocument.getElementsByAttribute("zayava").attr("zayava").toString();
        String column15 = jSoupDocument.getElementsByAttribute("zayava").attr("marks").toString();
        railDoc.setColumn7info(column7);
        railDoc.setColumn15info(column15);

        return railDoc;
    }

    /*
     * Добавление инфо о станциях из документа
     */
    private void parseAndAddStations(Document jSoupDoc, RailroadDocument railDoc) {
        RailroadDocument.Station sendStation = new RailroadDocument.Station();
        sendStation.setName(jSoupDoc.getElementsByAttribute("name_from").attr("name_from").toString());
        sendStation.setCode(jSoupDoc.getElementsByAttribute("stn_from").attr("stn_from").toString());

        RailroadDocument.Station receiveStation = new RailroadDocument.Station();
        receiveStation.setName(jSoupDoc.getElementsByAttribute("name_to").attr("name_to").toString());
        receiveStation.setCode(jSoupDoc.getElementsByAttribute("stn_to").attr("stn_to").toString());

        railDoc.setSendStation(sendStation);
        railDoc.setReceiveStation(receiveStation);

    }

    private void parseAndAddCarriers(Document jSoupDoc,RailroadDocument railDoc){
        Elements carrierElements = jSoupDoc.getElementsByTag("CARRIER");
        carrierElements.forEach(element ->{
            String codeInn = element.attr("esr_in");
            String nameInn = element.attr("esr_name_in");
            String codeOut = element.attr("esr_out");
            String nameOut = element.attr("esr_name_out");
            RailroadDocument.Station innStation = new RailroadDocument.Station(nameInn,codeInn);
            RailroadDocument.Station outStation = new RailroadDocument.Station(nameOut,codeOut);
            RailroadDocument.Carrier carrier =  new RailroadDocument.Carrier(innStation,outStation);
            railDoc.addCarrier(carrier);
                }
        );
    }

    /*
     * Добавление инфо о получателе/отправителе из єлемента документа // Completed
     */
    private RailroadDocument.Participant parseAndAddParticipants(Elements element, RailroadDocument railDoc) {

        RailroadDocument.Participant participant = new RailroadDocument.Participant();
        participant.setName(element.attr("name").toString());
        participant.setAddress(element.attr("adress").toString());
        participant.setRailroadCode(element.attr("kod").toString());
        participant.setEdrpuCode(element.attr("okpo").toString());

        return participant;
    }

    /*
     *
     */
    private RailroadDocument.Participant parseTarifPayer(Elements elements) {
        RailroadDocument.Participant tarifPayer = new RailroadDocument.Participant();
        tarifPayer.setName(elements.attr("name_plat").toString());
        tarifPayer.setRailroadCode(elements.attr("kod_plat").toString());
        return tarifPayer;
    }

    /*
     * Добавление инфо про вагоны //Completed
     */
    private void parseAndAddVagonsToDoc(Document jSoupDoc, RailroadDocument railDoc) {
        Elements elements = jSoupDoc.getElementsByAttribute("nomer");
        for (Element element : elements) {

            String number = element.getElementsByAttribute("nomer").attr("nomer").toString();
            int netVeight = 0;
            int tareVeight = 0;
            try {
                netVeight = Integer.parseInt(element.getElementsByAttribute("vesg").attr("vesg").toString());
                tareVeight = Integer.parseInt(element.getElementsByAttribute("ves_tary_arc").attr("ves_tary_arc").toString());
            } catch (NumberFormatException e) {
            }

            RailroadDocument.Vagon vagon = new RailroadDocument.Vagon(number, netVeight, tareVeight);

            double capasity = 0;
            try {
                capasity = Double.parseDouble(element.getElementsByAttribute("gruzp").attr("gruzp").toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            vagon.setCarryingCapasity(capasity);

            railDoc.addVagon(vagon);
        }
    }

    /*
     * Добавление инфо про груз в документ
     */
    private void parseAndAddCarriageInfoToDoc(Document jSoupDoc, RailroadDocument railDoc) {
        String carriageName = jSoupDoc.getElementsByAttribute("name_etsng").attr("name_etsng").toString();
        String carriageCode = jSoupDoc.getElementsByAttribute("kod_etsng").attr("kod_etsng").toString();
        railDoc.setCargoName(carriageName);
        railDoc.setCargoCode(carriageCode);
    }
    /*
     * Проверка документа на предмет изменения веса
     */
    private void updateDocument(Document jSoupDoc, RailroadDocument railDoc){
        int vagonCount = railDoc.getVagonCount();
        String atributeKey = "target";
        String valueFormat = "OTPR/VAGON[%1$d]/COLLECT_V[1]";
        for(int i = 0; i < vagonCount;){
            RailroadDocument.Vagon vagon = railDoc.getVagonList().get(i++);
            String ves = null;
            try {
                String value = String.format(valueFormat,i);
                Elements elements = jSoupDoc.getElementsByAttributeValue(atributeKey,value);
                if(elements.first() != null){
                    Elements vesElement = elements.first().getElementsByAttribute("vesg");
                    ves = vesElement.attr("vesg");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if(ves != null){
                try{
                    vagon.setNetVeight(Integer.parseInt(ves));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

    public void testUpdateMetod(File file,RailroadDocument rDoc) throws IOException {
        Document document = Jsoup.parse(file, "UTF-8");
        updateDocument(document,rDoc);
    }

}
