package tests.util_test;

import bin.RDocEnum;
import util.RDocEnumLoader;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Admin on 21.05.2017.
 * Тест для загрузчика порядка колонок
 */
public class OrderLoaderTest {

    public static void main(String[] args) throws IOException, JAXBException {
        RDocEnumLoader loader = RDocEnumLoader.getInstance();
        RDocEnum[] testedOdrer = new RDocEnum[]{RDocEnum.DOC_NUMBER,RDocEnum.PAYER_CODE,RDocEnum.RECEIVE_STATION_CODE};
        System.out.println(Arrays.asList(testedOdrer));
        File file = new File("test_resources/order.xml");
//        try {
//            loader.saveOrderToXML(testedOdrer,file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }
        testedOdrer = loader.loadOrderFromXML(file);
        System.out.println(Arrays.asList(testedOdrer));
    }
}
