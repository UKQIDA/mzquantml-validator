/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import info.psidev.psi.pi.mzquantml._1_0.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType.AnalTp;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi @time 16:27:16 12-May-2012 @institution University of Liverpool
 */
public class ListsRule {

    AnalysisType at;
    InputFilesType infls;
    ProteinGroupListType protGrpLst;
    ProteinListType protLst;
    List<PeptideConsensusListType> pepCnsLsts;
    List<FeatureListType> ftLsts;
    List<Message> msgs = new ArrayList<Message>();

    /*
     * constructor
     */
    public ListsRule(AnalysisType analysisType, InputFilesType inputFiles,
            ProteinGroupListType proteinGroupList, ProteinListType proteinList,
            List<PeptideConsensusListType> peptideConsensusLists, List<FeatureListType> featureLists) {
        this.at = analysisType;
        this.infls = inputFiles;
        this.protGrpLst = proteinGroupList;
        this.protLst = proteinList;
        this.pepCnsLsts = peptideConsensusLists;
        this.ftLsts = featureLists;
    }

    /*
     * public methods
     */
    public void check() {
        if ((this.protLst != null) && (this.pepCnsLsts != null)) {
            // All proteins SHOULD have peptide_refs
            for (ProteinType protein : this.protLst.getProtein()) {
                if (protein.getPeptideConsensusRefs() == null) {
                    msgs.add(new Message("If there is a ProteinList and "
                            + "a PeptideConsensusList, all protein SHOULD have peptide_refs", Level.INFO));
                    msgs.add(new Message("Protein "
                            + protein.getId() + " does not have peptide_refs\n", Level.WARN));
                }
            }
        }

        if ((this.protGrpLst != null) || (this.protLst != null) || (this.pepCnsLsts != null)) {
            if (this.infls.getSearchDatabase() == null) {
                msgs.add(new Message("If there is a ProteinGroupList, ProteinList or "
                        + "PeptideConsensusList, there SHOULD be SearchDatabase", Level.INFO));
                msgs.add(new Message("There SHOULD be SearchDatabase\n", Level.WARN));
            }
        }

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
        if ((this.protGrpLst == null) && (this.protLst == null) && (this.pepCnsLsts == null)) {
            msgs.add(new Message("One of PeptideConsensusList, ProteinList, ProteinGroupList MUST be present", Level.INFO));
            msgs.add(new Message("None of them exists in this file\n", Level.ERROR));
        }

        if (this.ftLsts != null) {
            for (FeatureListType featureList : ftLsts) {
                if (!featureList.getFeatureQuantLayer().isEmpty()) {
                    msgs.add(new Message("There SHOULD NOT be a FeatureQuantLayer", Level.INFO));
                    msgs.add(new Message("FeatureQuantLayer found in FeatrueList "
                            + featureList.getId() + "\n", Level.WARN));
                }
            }
        }
    }

    private void checkLCMS() {
        if ((this.protGrpLst == null) && (this.protLst == null) && (this.pepCnsLsts == null)) {
            msgs.add(new Message("One of PeptideConsensusList, ProteinList, ProteinGroupList SHOULD be present", Level.INFO));
            msgs.add(new Message("None of them exists in this file\n", Level.WARN));
        }
    }

    private void checkMS1() {
        if ((this.protGrpLst == null) && (this.protLst == null) && (this.pepCnsLsts == null)) {
            msgs.add(new Message("One of PeptideConsensusList, ProteinList, ProteinGroupList MUST be present", Level.INFO));
            msgs.add(new Message("None of them exists in this file\n", Level.ERROR));
        }
    }

    private void checkMS2() {
        if (this.pepCnsLsts != null) {
            checkFtLst();
        }
    }

    private void checkFtLst() {
        if (this.ftLsts == null) {
            msgs.add(new Message(("If PeptideConsensusList is present there MUST be a FeatureList present and "
                    + "there MUST be a MS2AssayQuantLayer present"), Level.INFO));
            msgs.add(new Message("There is no FeatureList\n", Level.ERROR));
        } else {
            for (FeatureListType ftLst : this.ftLsts) {
                if (ftLst.getMS2AssayQuantLayer() == null) {
                    msgs.add(new Message(("If PeptideConsensusList is present there MUST be a FeatureList present and "
                            + "there MUST be a MS2AssayQuantLayer present"), Level.INFO));
                    msgs.add(new Message("There is a FeatureList but no MS2AssayQuantLayer\n", Level.ERROR));
                }
            }
        }
    }
}
