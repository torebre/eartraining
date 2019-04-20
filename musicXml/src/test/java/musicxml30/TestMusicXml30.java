package musicxml30;


import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.Marshaller;


public class TestMusicXml30 {


    @Test
    public void test() throws JAXBException {
        ObjectFactory objectFactory = new ObjectFactory();

        ScorePartwise scorePart = objectFactory.createScorePartwise();


        ScorePartwise.Part part = objectFactory.createScorePartwisePart();

        List<ScorePartwise.Part.Measure> measures = new ArrayList<ScorePartwise.Part.Measure>();

        ScorePartwise.Part.Measure measure = objectFactory.createScorePartwisePartMeasure();
        measure.setNumber(Integer.valueOf(1).toString());

        Attributes attributes = objectFactory.createAttributes();
        attributes.setDivisions(BigDecimal.valueOf(1));
        List<Key> keys = attributes.getKey();
        Key key = objectFactory.createKey();
        key.setFifths(BigInteger.valueOf(0));
        keys.add(key);


        JAXBContext jaxbContext = JAXBContext.newInstance("musicxml30");
        Marshaller marshalller = jaxbContext.createMarshaller();
        marshalller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshalller.marshal(scorePart, System.out);


    }


}
