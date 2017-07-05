package com.yurets_y.document_parser.util;

import com.sun.xml.internal.bind.v2.TODO;
import com.yurets_y.document_parser.bin.RailroadDocument;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Admin on 20.06.2017.
 */
public class DocumentTester {


    public static String testDocuments(List<RailroadDocument> documentList, File testFile){
        StringBuilder result = new StringBuilder();
        List<TestNode> controlList = getControlList(testFile);
        List<TestNode> testList = documentList.stream()
                .map(doc -> new TestNode(doc.getDocNumber(),doc.getFullVeight(),doc.getPayment()))
                .collect(Collectors.toList());

        controlList.forEach(docNode ->{
            if(controlList.contains(docNode)){
                //TODO сделать проверку по массе и списаниям
            }else{
                result.append("Документ - ")
                        .append(docNode.getDocNumber())
                        .append(" не найден")
                        .append(System.lineSeparator());
            }

        });


        return result.toString();
    }

    private static List<TestNode> getControlList(File file){

        throw new RuntimeException("Нереализованная функция");
    }

    private static class TestNode{
        String docNumber;
        int docMass;
        int docPayment;

        public TestNode(String docNumber, int docMass, int docPayment) {
            this.docNumber = docNumber;
            this.docMass = docMass;
            this.docPayment = docPayment;
        }

        public String getDocNumber() {
            return docNumber;
        }

        public void setDocNumber(String docNumber) {
            this.docNumber = docNumber;
        }

        public int getDocMass() {
            return docMass;
        }

        public void setDocMass(int docMass) {
            this.docMass = docMass;
        }

        public int getDocPayment() {
            return docPayment;
        }

        public void setDocPayment(int docPayment) {
            this.docPayment = docPayment;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestNode testNode = (TestNode) o;

            return docNumber.equals(testNode.docNumber);

        }

        @Override
        public int hashCode() {
            return docNumber.hashCode();
        }
    }
}
