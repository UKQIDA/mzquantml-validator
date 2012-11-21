/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.liv.jmzqml.model.mzqml.Assay;
import uk.ac.liv.jmzqml.model.mzqml.AssayList;
import uk.ac.liv.jmzqml.model.mzqml.ModParam;
import uk.ac.liv.jmzqml.model.mzqml.RawFilesGroup;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType.AnalTp;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @time 17:08:22 12-May-2012
 * @institution University of Liverpool
 */
public class AssayLabelRule {

    private static final Logger logger = Logger.getLogger(AssayLabelRule.class);
    AnalysisType at;
    AssayList assyLst;
    List<Message> msgs = new ArrayList<Message>();

    /*
     * constructor
     */
    public AssayLabelRule() {
        this.at = null;
    }

    public AssayLabelRule(AnalysisType analysisType, AssayList assayList) {
        this.at = analysisType;
        this.assyLst = assayList;
    }

    /*
     * public methods
     */
    public void check() {
        if (this.at.getAnalysisType() == AnalTp.SpectralCounting) {
            checkSC();
        } else if (this.at.getAnalysisType() == AnalTp.LabelFree) {
            checkLCMS();
        } else if (this.at.getAnalysisType() == AnalTp.MS1LabelBased) {
            checkMS1();
        } else if (this.at.getAnalysisType() == AnalTp.MS2TagBased) {
            checkMS2();
        }
    }

    public List<Message> getMsgs() {
        return msgs;
    }

    /*
     * private methods
     */
    private void checkSC() {
        for (Assay assay : this.assyLst.getAssay()) {
            if (assay.getLabel() != null) {
                List<ModParam> modParamList = assay.getLabel().getModification();

                if (modParamList != null) {

                    if (modParamList.size() == 1 && !modParamList.get(0).getCvParam().getName().toLowerCase().equals("unlabeled sample")) {
                        msgs.add(new Message("There MUST NOT be any Label in "
                                + "AssayList or only \"unlabeled sample\" label (spectral counting)", Level.INFO));
                        msgs.add(new Message("The label is not \"unlabeled sample\"\n", Level.ERROR));
                    }
                    if (modParamList.size() > 1) {
                        msgs.add(new Message("There MUST NOT be any Label in "
                                + "AssayList or only \"unlabeled sample\" label (spectral counting)", Level.INFO));
                        msgs.add(new Message("There MUST NOT be any modification label "
                                + "(except unlabeled sample) in AssayList\n", Level.ERROR));
                    }
                }
            }

            if ((assay.getIdentificationFileRefs() == null) && (assay.getRawFilesGroupRef() == null)) {
                msgs.add(new Message("There MUST be at least one of rawFilesGroup_Ref or "
                        + "identificationFile_Refs in Assay", Level.INFO));
                msgs.add(new Message("None of these exsits in Assay "
                        + assay.getId() + "\n", Level.ERROR));
            }
        }
    }

    private void checkLCMS() {
        for (Assay assay : this.assyLst.getAssay()) {
            if (assay.getLabel() != null) {
                List<ModParam> modParamList = assay.getLabel().getModification();

                if (modParamList != null) {

                    if (modParamList.size() == 1 && !modParamList.get(0).getCvParam().getName().toLowerCase().equals("unlabeled sample")) {
                        msgs.add(new Message("There MUST NOT be any Label in "
                                + "AssayList or only \"unlabeled sample\" label (LC-MS label-free)", Level.INFO));
                        msgs.add(new Message("The label is not \"unlabeled sample\"\n", Level.ERROR));
                    }
                    if (modParamList.size() > 1) {
                        msgs.add(new Message("There MUST NOT be any Label in "
                                + "AssayList or only \"unlabeled sample\" label (LC-MS label-free)", Level.INFO));
                        msgs.add(new Message("There MUST NOT be any modification label "
                                + "(except unlabeled sample) in AssayList\n", Level.ERROR));
                    }
                }
            }
        }
    }

    private void checkMS1() {
        HashMap<Object, ArrayList<Assay>> rawfilegrouprefAssayMap =
                getRawfilegrouprefAssayMap(this.assyLst.getAssay());
        checkRawfilegrouprefAssayMap(rawfilegrouprefAssayMap);
    }

    private HashMap<Object, ArrayList<Assay>> getRawfilegrouprefAssayMap(
            List<Assay> assays) {
        HashMap<Object, ArrayList<Assay>> rawfilegrouprefAssayMap =
                new HashMap<Object, ArrayList<Assay>>();
        for (Assay assay : assays) {
            Object rawfilegroupRef = assay.getRawFilesGroupRef();
            ArrayList<Assay> manyAssay = rawfilegrouprefAssayMap.get(rawfilegroupRef);
            if (manyAssay == null) {
                manyAssay = new ArrayList<Assay>();
                rawfilegrouprefAssayMap.put(rawfilegroupRef, manyAssay);
            }
            manyAssay.add(assay);
        }
        return rawfilegrouprefAssayMap;
    }

    private void checkRawfilegrouprefAssayMap(
            HashMap<Object, ArrayList<Assay>> rawfilegrouprefAssayMap) {

        // check MS1 label base rule MUST No. 3:
        // The file MUST contain two or more assays references to the same rawFileGroup
        for (Object ref : rawfilegrouprefAssayMap.keySet()) {
            RawFilesGroup rawFileGroup = (RawFilesGroup) ref;
            ArrayList<Assay> assays = rawfilegrouprefAssayMap.get(ref);
            if (assays.size() < 2) {
                msgs.add(new Message("The file MUST contain two or more "
                        + "assays references to the same rawFileGroup", Level.INFO));
                msgs.add(new Message("Only one assay reference to the rawFileGroup ("
                        + rawFileGroup.getId() + ")\n", Level.ERROR));
            }
        }

        // check MS1 label base rule MUST No. 4:
        // At least one of the grouped assays that reference to a common rawFileGroup MUST have the "Label" element
        for (Object ref : rawfilegrouprefAssayMap.keySet()) {
            RawFilesGroup rawFileGroup = (RawFilesGroup) ref;
            ArrayList<Assay> assays = rawfilegrouprefAssayMap.get(ref);
            if (assays.size() > 1) {
                if (!haveAssayLabel(assays)) {
                    msgs.add(new Message("At least one of the grouped assays that "
                            + "reference to a common rawFileGroup MUST hae the \"Label\" element", Level.INFO));
                    msgs.add(new Message("Assays with a common rawFileGroupRef ("
                            + rawFileGroup.getId() + ") do not have \"Lable\" element\n", Level.ERROR));
                }
            }
        }
    }

    private boolean haveAssayLabel(ArrayList<Assay> assays) {
        boolean b = true;
        for (Assay assay : assays) {
            if (assay.getLabel() == null) {
                b = false;
                break;
            }
        }
        return b;
    }

    private void checkMS2() {
    }
}
