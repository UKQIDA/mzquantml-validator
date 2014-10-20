/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.jmzqml.model.mzqml.*;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType.AnalTp;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @time 16:27:16 12-May-2012
 * @institution University of Liverpool
 */
public class ListsRule {

    AnalysisType at;
    InputFiles infls;
    ProteinGroupList protGrpLst;
    ProteinList protLst;
    Iterator<PeptideConsensusList> pepCnsLsts;
    Iterator<FeatureList> ftLsts;
    List<Message> msgs = new ArrayList<Message>();

    /*
     * constructor
     */
    public ListsRule(AnalysisType analysisType, InputFiles inputFiles,
                     ProteinGroupList proteinGroupList, ProteinList proteinList,
                     Iterator<PeptideConsensusList> peptideConsensusLists,
                     Iterator<FeatureList> featureLists) {
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
        if ((this.protLst != null) && (this.pepCnsLsts.hasNext())) {
            // All proteins SHOULD have peptide_refs
            for (Protein protein : this.protLst.getProtein()) {
                if (protein.getPeptideConsensusRefs() == null || protein.getPeptideConsensusRefs().isEmpty()) {
                    msgs.add(new Message("If there is a ProteinList and "
                            + "a PeptideConsensusList, all protein SHOULD have <PeptideConsensus_refs>.\n", Level.INFO));
                    msgs.add(new Message("Protein \""
                            + protein.getId() + "\" does not have <PeptideConsensus_refs>.\n", Level.WARN));
                }
            }
        }

        if ((this.protGrpLst != null) || (this.protLst != null) || (this.pepCnsLsts.hasNext())) {
            if (this.infls.getSearchDatabase() == null || this.infls.getSearchDatabase().isEmpty()) {
                msgs.add(new Message("If there is a ProteinGroupList, ProteinList or "
                        + "PeptideConsensusList, there SHOULD be SearchDatabase.\n", Level.INFO));
                msgs.add(new Message("There SHOULD be a SearchDatabase.\n", Level.WARN));
            }
        }

        if (this.at.getAnalysisType() == AnalTp.SpectralCounting) {
            checkSC();
        }
        else if (this.at.getAnalysisType() == AnalTp.LabelFree) {
            checkLCMS();
        }
        else if (this.at.getAnalysisType() == AnalTp.MS1LabelBased) {
            checkMS1();
        }
        else if (this.at.getAnalysisType() == AnalTp.MS2TagBased) {
            checkMS2();
        }
        else if (this.at.getAnalysisType() == AnalTp.SRM) {
            checkSRM();
        }
    }

    public List<Message> getMsgs() {
        return msgs;
    }

    /*
     * private methods
     */
    private void checkSC() {
        if ((this.protGrpLst == null) && (this.protLst == null) && (!this.pepCnsLsts.hasNext())) {
            msgs.add(new Message("One of PeptideConsensusList, ProteinList, ProteinGroupList MUST be present.\n", Level.INFO));
            msgs.add(new Message("None of them exists in this file.\n", Level.ERROR));
        }

        while (ftLsts.hasNext()) {
            FeatureList featureList = ftLsts.next();
            if (!featureList.getFeatureQuantLayer().isEmpty()) {
                msgs.add(new Message("There SHOULD NOT be a FeatureQuantLayer.\n", Level.INFO));
                msgs.add(new Message("FeatureQuantLayer found in FeatrueList "
                        + featureList.getId() + ".\n", Level.WARN));
            }
        }
    }

    private void checkLCMS() {
        if ((this.protGrpLst == null) && (this.protLst == null) && (!this.pepCnsLsts.hasNext())) {
            msgs.add(new Message("One of PeptideConsensusList, ProteinList, ProteinGroupList SHOULD be present.\n", Level.INFO));
            msgs.add(new Message("None of them exists in this file.\n", Level.WARN));
        }
    }

    private void checkMS1() {
        if ((this.protGrpLst == null) && (this.protLst == null) && (!this.pepCnsLsts.hasNext())) {
            msgs.add(new Message("One of PeptideConsensusList, ProteinList, ProteinGroupList MUST be present.\n", Level.INFO));
            msgs.add(new Message("None of them exists in this file.\n", Level.ERROR));
        }
    }

    private void checkMS2() {
        if (this.pepCnsLsts.hasNext()) {
            checkFtLst();
        }
    }

    private void checkFtLst() {
        if (!this.ftLsts.hasNext()) {
            msgs.add(new Message(("If PeptideConsensusList is present there MUST be a FeatureList present and "
                    + "there MUST be a MS2AssayQuantLayer present.\n"), Level.INFO));
            msgs.add(new Message("There is no FeatureList.\n", Level.ERROR));
        }
        else {
            //for (FeatureList ftLst : this.ftLsts) {
            while (ftLsts.hasNext()) {
                FeatureList ftLst = ftLsts.next();
                if (ftLst.getMS2AssayQuantLayer() == null) {
                    msgs.add(new Message(("If PeptideConsensusList is present there MUST be a FeatureList present and "
                            + "there MUST be a MS2AssayQuantLayer present.\n"), Level.INFO));
                    msgs.add(new Message("There is a FeatureList but no MS2AssayQuantLayer.\n", Level.ERROR));
                }
            }
        }
    }

    private void checkSRM() {
        if (!this.ftLsts.hasNext()) {
            msgs.add(new Message("There MUST be at least one FeatureList.\n", Level.INFO));
            msgs.add(new Message("No FeatureList can be found in this file.\n", Level.ERROR));
        }
    }

}
