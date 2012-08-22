/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import org.apache.log4j.Level;
import uk.ac.liv.jmzqml.model.mzqml.*;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time May 2, 2012 3:23:05 PM
 */
public class ObjectRefTypeMatchRule {

    ArrayList<Message> msgs = new ArrayList<Message>();
    String targetClassId;
    Object refObj;
    Class cls;

    public ObjectRefTypeMatchRule() {
    }

    public ObjectRefTypeMatchRule(String tarClsId, Object obj, Class cls) {
        this.targetClassId = tarClsId;
        this.cls = cls;
        this.refObj = obj;
    }

    public void check() {
        if (this.refObj != null) {
            if (!(this.refObj.getClass().equals(this.cls)) && !(this.cls.isAssignableFrom(this.refObj.getClass()))) {
                String id = new String();
                if (refObj.getClass().equals(uk.ac.liv.jmzqml.model.mzqml.Assay.class)) {
                    Assay obj = (Assay) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(uk.ac.liv.jmzqml.model.mzqml.DataProcessing.class)) {
                    DataProcessing obj = (DataProcessing) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(uk.ac.liv.jmzqml.model.mzqml.Cv.class)) {
                    Cv obj = (Cv) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(uk.ac.liv.jmzqml.model.mzqml.Feature.class)) {
                    Feature obj = (Feature) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(uk.ac.liv.jmzqml.model.mzqml.IdentificationFile.class)) {
                    IdentificationFile obj = (IdentificationFile) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(uk.ac.liv.jmzqml.model.mzqml.MethodFile.class)) {
                    MethodFile obj = (MethodFile) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(uk.ac.liv.jmzqml.model.mzqml.SearchDatabase.class)) {
                    SearchDatabase obj = (SearchDatabase) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(uk.ac.liv.jmzqml.model.mzqml.Protein.class)) {
                    Protein obj = (Protein) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(uk.ac.liv.jmzqml.model.mzqml.PeptideConsensus.class)) {
                    PeptideConsensus obj = (PeptideConsensus) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(uk.ac.liv.jmzqml.model.mzqml.RawFilesGroup.class)) {
                    RawFilesGroup obj = (RawFilesGroup) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(uk.ac.liv.jmzqml.model.mzqml.StudyVariable.class)) {
                    StudyVariable obj = (StudyVariable) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(uk.ac.liv.jmzqml.model.mzqml.Software.class)) {
                    Software obj = (Software) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(uk.ac.liv.jmzqml.model.mzqml.Organization.class)) {
                    Organization obj = (Organization) refObj;
                    id = obj.getId();
                }


                msgs.add(new Message("All object reference of type IDREFS or IDREF MUST match the correct object type", Level.INFO));
                msgs.add(new Message("The object reference \""
                        + id + "\" in \"" + this.targetClassId + "\" does not match the correct object type "
                        + cls.getName() + "\n", Level.ERROR));
            }
        }
    }

    public ArrayList<Message> getMessage() {
        return msgs;
    }
}
