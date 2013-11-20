/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.jmzqml.xml.io.MzQuantMLUnmarshaller;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author ddq
 */
public class InOutputObjectRefsRule {

    List<String> objRefs;
    String targetClassId;
    MzQuantMLUnmarshaller um;
    ArrayList<Message> msgs = new ArrayList<Message>();

    /*
     * constructor
     */
    public InOutputObjectRefsRule(List<String> objectRefs, String tarClsId,
                                  MzQuantMLUnmarshaller unmarsh) {
        this.objRefs = objectRefs;
        this.targetClassId = tarClsId;
        this.um = unmarsh;
    }

    /*
     * public methods
     */
    public void check() {
        if (!this.objRefs.isEmpty()) {
            for (String ref : objRefs) {
                try {
                    this.um.unmarshal(uk.ac.liv.jmzqml.model.mzqml.RawFilesGroup.class, ref);
                    break;
                }
                catch (Exception ex) {
                    System.out.println("Is not RawFilesGroup");
                }
                try {
                    this.um.unmarshal(uk.ac.liv.jmzqml.model.mzqml.ProteinGroupList.class, ref);
                    break;
                }
                catch (Exception ex1) {
                    System.out.println("Is not ProteinGroupList");
                }
                try {
                    this.um.unmarshal(uk.ac.liv.jmzqml.model.mzqml.ProteinList.class, ref);
                    break;
                }
                catch (Exception ex2) {
                    System.out.println("Is not ProteinList");
                }
                try {
                    this.um.unmarshal(uk.ac.liv.jmzqml.model.mzqml.PeptideConsensusList.class, ref);
                    break;
                }
                catch (Exception ex3) {
                    System.out.println("Is not PeptideConsensusList");
                }

//TODO; when jmzquantml can resolve the quantlayer, globalquanlay, and ratioquanlayer, then uncomment this block
//                try {
//                    this.um.unmarshal(uk.ac.liv.jmzqml.model.mzqml.GlobalQuantLayer.class, ref);
//                    break;
//                }
//                catch (Exception ex5) {
//                    System.out.println("Is not GlobalQuantLayer");
//                }
//                try {
//                    this.um.unmarshal(uk.ac.liv.jmzqml.model.mzqml.QuantLayer.class, ref);
//                    break;
//                }
//                catch (Exception ex6) {
//                    System.out.println("Is not QuantLayer");
//                }
//                try {
//                    this.um.unmarshal(uk.ac.liv.jmzqml.model.mzqml.RatioQuantLayer.class, ref);
//                    break;
//                }
//                catch (Exception ex7) {
//                    System.out.println("Is not RatioQuantLayer");
//                }
                
                try {
                    this.um.unmarshal(uk.ac.liv.jmzqml.model.mzqml.FeatureList.class, ref);
                    break;
                }
                catch (Exception ex4) {
                    System.out.println("Is not FeatureList");
                    msgs.add(new Message("InputObject_refs and OutputOjbect_refs in DataProcessing MUST "
                            + "reference RawFilesGroup, FeatureList, PeptideConsensusList, ProteinList, ProteinGroupList or QuantLayers.", Level.INFO));
                    msgs.add(new Message("\"" + ref + "\" in \"" + this.targetClassId + "\" is not expected.\n", Level.ERROR));
                }

            }

        }

    }

    public List<Message> getMsgs() {
        return msgs;
    }

}
