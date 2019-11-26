package bin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Yuriy on 29.06.2016.
 * Файл для сохранения данных
 * по железнодорожному документу
 */
public class RailroadDocument {
    private String docNumber;

    private Date docDate;
    private Date delDate;
    private Date notfDate;
    private Date credDate;

    private Station sendStation;
    private Station receiveStation;

    private Participant cargoSender;
    private Participant cargoReceiver;
    private Participant tarifPayer;

    private List<Carrier> carriers = new ArrayList<>();

    private String cargoName;
    private String cargoCode;

    private int payment;

    private int tarifDistance;

    private String column7info;
    private String column15info;

    private List<Vagon> vagonList = new ArrayList<>();

    private Map<String, String> stamps = new HashMap<String, String>();

    /*
     * getters and setters:
     */
    public void addVagon(Vagon vagon) {
        vagonList.add(vagon);
    }

    public List<Vagon> getVagonList() {
        return vagonList;
    }

    public Participant getCargoReceiver() {
        return cargoReceiver;
    }

    public void setCargoReceiver(Participant cargoReceiver) {
        this.cargoReceiver = cargoReceiver;
    }

    public Participant getCargoSender() {
        return cargoSender;
    }

    public void setCargoSender(Participant cargoSender) {
        this.cargoSender = cargoSender;
    }

    public Participant getTarifPayer() {
        return tarifPayer;
    }

    public void setTarifPayer(Participant tarifPayer) {
        this.tarifPayer = tarifPayer;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(String stringDate) {
        docDate = getDateFromString(stringDate);
    }

    public Date getDelDate() {
        return delDate;
    }

    public void setDelDate(String delDate) {
        this.delDate = getDateFromString(delDate);
    }

    public Date getNotfDate() {
        return notfDate;
    }

    public void setNotfDate(String notfDate) {
        this.notfDate = getDateFromString(notfDate);
    }

    public Date getCredDate() {
        return credDate;
    }

    public void setCredDate(String credDate) {
        this.credDate = getDateFromString(credDate);
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getCargoCode() {
        return cargoCode;
    }

    public void setCargoCode(String cargoCode) {
        this.cargoCode = cargoCode;
    }

    public String getCargoName() {
        return cargoName;
    }

    public void setCargoName(String cargoName) {
        this.cargoName = cargoName;
    }

    public Station getReceiveStation() {
        return receiveStation;
    }

    public void setReceiveStation(Station receiveStation) {
        this.receiveStation = receiveStation;
    }

    public Station getSendStation() {
        return sendStation;
    }

    public void setSendStation(Station sendStation) {
        this.sendStation = sendStation;
    }

    public Station getOutStation() {
        return carriers.size() > 0 ? carriers.get(0).getTo() : new Station();
    }

    public Station getInnStation() {
        return carriers.size() > 1 ? carriers.get(1).getFrom(): new Station();
    }

    /*
     * Получение полной массы груза
     */
    public int getFullVeight() {
        int fullVeight = 0;
        for (Vagon vagon : vagonList) {
            fullVeight += vagon.netVeight;
        }
        return fullVeight;
    }

    public int getVagonCount() {
        return vagonList.size();
    }

    /*
     * Получение полной массы груза в виде формулы для excel
     */
    public String getFullVeightToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=");
        for (Vagon vagon : vagonList) {
            sb.append(vagon.getNetVeight());
            if (vagon != vagonList.get(vagonList.size() - 1))
                sb.append("+");
        }
        return sb.toString();
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public void setPayment(String paymentString) {
        try {
            this.payment = Integer.parseInt(paymentString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public String getColumn7info() {
        return column7info;
    }

    public void setColumn7info(String column7info) {
        this.column7info = column7info;
    }

    public String getColumn15info() {
        return column15info;
    }

    public void setColumn15info(String column15info) {
        this.column15info = column15info;
    }

    public List<Carrier> getCarriers() {
        return carriers;
    }

    public void addCarrier(Carrier carrier){
        carriers.add(carrier);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("№ документа: %s, дата: %2$td/%2$tm/%2$tY%n", docNumber, docDate));
        sb.append(String.format("ст. Отправления: %s, код: %s%n", sendStation.name, sendStation.code));
        sb.append(String.format("ст. Назначения: %s, код: %s%n", receiveStation.name, receiveStation.code));
        sb.append(String.format("Отправитель: %s%n", cargoSender));
        sb.append(String.format("Получатель: %s%n", cargoReceiver));
        sb.append(String.format("Масса груза: %d кг%n", getFullVeight()));
        sb.append(String.format("Груз: %s, код груза: %s%n", cargoName, cargoCode));

        sb.append("Вагоны:\n");
        for (Vagon vagon : vagonList) {
            sb.append(String.format("\t%s%n", vagon));
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RailroadDocument document = (RailroadDocument) o;
        return docNumber != null ? docNumber.equals(document.docNumber) : document.docNumber == null;

    }

    @Override
    public int hashCode() {
        return docNumber != null ? docNumber.hashCode() : 0;
    }

    private Date getDateFromString(String stringDate){
        if ((stringDate == null)||(stringDate.equals("")))
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(stringDate);
        } catch (ParseException e) {
            dateFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm");
            try {
                date = dateFormat.parse(stringDate);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return date;
    }


    public String getShortRepresentation() {
        return String.format("№ док: %s, %s - %s дата: %4$td_%4$tm_%4$tY %5$d ваг%n", docNumber, sendStation.getName(), receiveStation.getName(), docDate, getVagonCount());
    }

    public int getTarifDistance() {
        return tarifDistance;
    }

    public void setTarifDistance(int tarifDistance) {
        this.tarifDistance = tarifDistance;
    }

    public Map<String, String> getStamps() {
        return stamps;
    }

    public boolean containsStamp(String key) {
        return getStamps().containsKey(key);
    }

    public String getStampText(String key) {
        return getStamps().get(key);
    }

    public String putStamp(String key, String value) {
        return getStamps().put(key, value);
    }

    /*
     * Класс для сохранения данных про вагон:
     */
    public static class Vagon {
        private String number;
        private int grossVeight;
        private int netVeight;
        private int tareVeight;
        private double carryingCapasity;
        private double payment;



        private List<String> zpuList;

        public Vagon(String number, int netVeight, int tareVeight) {
            zpuList = new ArrayList<>();
            this.number = number;
            this.netVeight = netVeight;
            this.tareVeight = tareVeight;
            this.grossVeight = netVeight + tareVeight;
        }

        public int getGrossVeight() {
            return grossVeight;
        }

        public void setGrossVeight(int grossVeight) {
            this.grossVeight = grossVeight;
        }

        public int getNetVeight() {
            return netVeight;
        }

        public void setNetVeight(int netVeight) {
            this.netVeight = netVeight;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public int getTareVeight() {
            return tareVeight;
        }

        public void setTareVeight(int tareVeight) {
            this.tareVeight = tareVeight;
        }

        public double getCarryingCapasity() {
            return carryingCapasity;
        }

        public void setCarryingCapasity(double carryingCapasity) {
            this.carryingCapasity = carryingCapasity;
        }

        public void addZpu(String zpu){
            zpuList.add(zpu);
        }
        public int getZpuCount(){
            return zpuList.size();
        }
        public String getZpuList(){
            return String.join(", ",zpuList);
        }

        @Override
        public String toString() {
            return String.format("%s гп: %.1f; нетто: %d, тара %d, брутто: %d.", number, carryingCapasity, netVeight, tareVeight, grossVeight);
        }

        public double getPayment() {
            return payment;
        }

        public void setPayment(double payment) {
            this.payment = payment;
        }

    }

    /*
     * Класс для сохранения данных про станцию
     */
    public static class Station {
        private String name = "";
        private String code = "";
        private String road = "";

        public Station(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public Station() {
        }
        /*
         * getters And Setters
         */

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRoad() {
            return road;
        }

        public void setRoad(String road) {
            this.road = road;
        }

        @Override
        public String toString() {
            return String.format("ст. %s, код: %s", name, code);
        }

        public String getNameAndCode() {
            return String.format("(%s) %s", code, name);
        }
    }

    /*
     * Класс для сохранения данных по учасникам
     * танспортного процесса (отправитель/получатель)
     */
    public static class Participant {
        private String name;
        private String railroadCode;
        private String edrpuCode;
        private String address;

        /*
         * getters and setters
         */

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEdrpuCode() {
            return edrpuCode;
        }

        public void setEdrpuCode(String edrpuCode) {
            this.edrpuCode = edrpuCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRailroadCode() {
            return railroadCode;
        }

        public void setRailroadCode(String railroadCode) {
            this.railroadCode = railroadCode;
        }

        public String getCodeAndName() {
            return String.format("(%s) %s", railroadCode, name);
        }

        @Override
        public String toString() {
            return String.format("%s, код: %s, адресс: %s", name, railroadCode, address);
        }
    }

    public static class Carrier{
        private Station from;
        private Station to;

        public Carrier(Station from, Station to) {
            this.from = from;
            this.to = to;
        }

        public Station getFrom() {
            return from;
        }

        public Station getTo() {
            return to;
        }
    }


}
