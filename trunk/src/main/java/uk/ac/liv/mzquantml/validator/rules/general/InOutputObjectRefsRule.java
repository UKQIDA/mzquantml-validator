/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author ddq
 */
public class InOutputObjectRefsRule {

    List<Object> objRefs;
    String targetClassId;
    ArrayList<Message> msgs = new ArrayList<Message>();

    /*
     * constructor
     */
    public InOutputObjectRefsRule(List<Object> objectRefs, String tarClsId) {
        this.objRefs = objectRefs;
        this.targetClassId = tarClsId;
    }

    /*
     * public methods
     */
    public void check() {
        if (!this.objRefs.isEmpty()) {
            for (Object ref : objRefs) {
                if (!ref.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.RawFilesGroupType.class)
                        && !ref.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.ProteinGroupListType.class)
                        && !ref.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.ProteinListType.class)
                        && !ref.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.PeptideConsensusListType.class)
                        && !ref.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.FeatureListType.class)
                        && !ref.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.GlobalQuantLayerType.class)
                        && !ref.getClass().equals(info.psidev.psi.pi.mzquantml._1_0.QuantLayerType.class)) {
                    msgs.add(new Message("InputObject_refs and OutputOjbect_refs in DataProcessing MUST "
                            + "reference RawFilesGroup, FeatureList, PeptideConsensusList, ProteinList, ProteinGroupList or QuantLayers.", Level.INFO));
                    msgs.add(new Message("The objectRef in " + this.targetClassId + " is referencing " + ref.getClass().getSimpleName() + "\n", Level.ERROR));
                }
            }
        }
    }

    public List<Message> getMsgs() {
        return msgs;
    }
}
