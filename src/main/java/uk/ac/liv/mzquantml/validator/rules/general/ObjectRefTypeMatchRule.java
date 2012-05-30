/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import info.psidev.psi.pi.mzquantml._1_0.*;
import java.util.ArrayList;
import org.apache.log4j.Level;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi @institute University of Liverpool @time May 2, 2012 3:23:05 PM
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
                if (refObj.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.AssayType.class)) {
                    AssayType obj = (AssayType) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.DataProcessingType.class)) {
                    DataProcessingType obj = (DataProcessingType) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.CvType.class)) {
                    CvType obj = (CvType) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.FeatureType.class)) {
                    FeatureType obj = (FeatureType) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.IdentificationFileType.class)) {
                    IdentificationFileType obj = (IdentificationFileType) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.MethodFileType.class)) {
                    MethodFileType obj = (MethodFileType) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.SearchDatabaseType.class)) {
                    SearchDatabaseType obj = (SearchDatabaseType) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.ProteinType.class)) {
                    ProteinType obj = (ProteinType) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.PeptideConsensusType.class)) {
                    PeptideConsensusType obj = (PeptideConsensusType) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.RawFilesGroupType.class)) {
                    RawFilesGroupType obj = (RawFilesGroupType) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.StudyVariableType.class)) {
                    StudyVariableType obj = (StudyVariableType) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.SoftwareType.class)) {
                    SoftwareType obj = (SoftwareType) refObj;
                    id = obj.getId();
                }
                if (refObj.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.OrganizationType.class)) {
                    OrganizationType obj = (OrganizationType) refObj;
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
