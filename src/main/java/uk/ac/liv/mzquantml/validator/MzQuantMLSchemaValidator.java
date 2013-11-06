/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.liv.mzquantml.validator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;
import uk.ac.liv.jmzqml.model.mzqml.MzQuantML;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time 04-Nov-2013 15:18:41
 */
public class MzQuantMLSchemaValidator {

    private static Schema schema;
    private static File mzqFile;
    private static JAXBContext context;
    private static List<Message> msgs = new ArrayList<Message>();
    private Unmarshaller unmarsh = null;

    /**
     * Constructor of MzQuantMLSchemaValidator with no parameter.
     * <p>
     * This is an empty constructor.
     */
    public MzQuantMLSchemaValidator() {
        schema = null;
        mzqFile = null;
        context = null;
    }

    /**
     * Constructor of MzQuantMLSchemaValidator with single parameter mzqFile.
     * <p>
     * The schema by default is mzQuantML_1_0_0.xsd.
     *
     * @param mzq the mzQuantML file.
     */
    public MzQuantMLSchemaValidator(File mzq) {
        mzqFile = mzq;
        setSchema(getSchemaFile());
        msgs.clear();
        try {
            context = JAXBContext.newInstance(new Class[]{MzQuantML.class});
        }
        catch (JAXBException ex) {
            msgs.add(new Message("Exception in JAXBContext.newInstance: " + ex.getMessage() + "\n\n"));
        }
    }

    /**
     * Constructor of MzQuantMLSchemaValidator with double parameters.
     *
     * @param mzq  the mzQuantML file.
     * @param sche the schema file.
     */
    public MzQuantMLSchemaValidator(File mzq, File sche) {
        mzqFile = mzq;
        msgs.clear();
        setSchema(sche);
        msgs.add(new Message("\nSchema validation message(s): \n\n"));
        try {
            context = JAXBContext.newInstance(new Class[]{MzQuantML.class});
        }
        catch (JAXBException ex) {
            msgs.add(new Message("Exception in JAXBContext.newInstance: " + ex.getMessage() + "\n\n"));
        }
    }

    /**
     * The method to retrieve the messages after the schema validation process.
     *
     * @return a list of validation messages.
     */
    public List<Message> getValidateResult() {
        validate();
        return msgs;
    }

    private static void setSchema(File schemaFile) {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schema = sf.newSchema(schemaFile);
        }
        catch (SAXException ex) {
            msgs.add(new Message("Exception in setSchema: " + ex.getMessage() + "\n\n"));
        }
    }

    private void validate() {
        try {
            this.unmarsh = context.createUnmarshaller();
            this.unmarsh.setSchema(schema);

            ValidationEventHandler veh = new ValidationEventHandler() {

                @Override
                public boolean handleEvent(ValidationEvent event) {
                    // ignore warnings; WARNING is lowest error in this case
                    if (event.getSeverity() != ValidationEvent.WARNING) {
                        ValidationEventLocator vel = event.getLocator();
                        String singleMsg = "Severity: " + event.getSeverity() + "\n";
                        singleMsg = singleMsg + "Line: " + vel.getLineNumber() + "; ";
                        singleMsg = singleMsg + "Col: " + vel.getColumnNumber() + "\n";
                        singleMsg = singleMsg + "Message: " + event.getMessage() + "\n\n";

                        msgs.add(new Message(singleMsg));
                    }
                    return true;
                }

            };
            unmarsh.setEventHandler(veh);
            unmarsh.unmarshal(mzqFile);
        }
        catch (JAXBException ex) {
            msgs.add(new Message("Exception in context.createUnmarshaller(): " + ex.getMessage() + "\n\n"));
        }
    }

    private File getSchemaFile() {
        File file = new File(getClass().getClassLoader().getResource("mzQuantML_1_0_0.xsd").getFile());
        return file;
    }

}
