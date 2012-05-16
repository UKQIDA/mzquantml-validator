/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import info.psidev.psi.pi.mzquantml._1_0.FeatureListType;
import info.psidev.psi.pi.mzquantml._1_0.PeptideConsensusListType;
import info.psidev.psi.pi.mzquantml._1_0.ProteinGroupListType;
import info.psidev.psi.pi.mzquantml._1_0.ProteinListType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.mzquantml.validator.utils.AnalysisSummaryElement;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType.AnalTp;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi @time 13:57:11 13-May-2012 @institution University of Liverpool
 */
public class QuantLayerRule {

    HashMap<AnalysisSummaryElement, Boolean> anlSumMap;
    List<PeptideConsensusListType> pepCnsLsts;
    ProteinGroupListType protGrpLst;
    ProteinListType protLst;
    List<FeatureListType> ftLsts;
    ArrayList<Message> msgs = new ArrayList<Message>();

    /*
     * constructor
     */
    public QuantLayerRule() {
        this.msgs = null;
        this.anlSumMap = null;
    }

    public QuantLayerRule(HashMap<AnalysisSummaryElement, Boolean> summaryMap, ProteinGroupListType proteinGroupList, ProteinListType proteinList,
            List<PeptideConsensusListType> peptideConsensusLists, List<FeatureListType> featureLists) {
        this.anlSumMap = summaryMap;
        this.protGrpLst = proteinGroupList;
        this.protLst = proteinList;
        this.pepCnsLsts = peptideConsensusLists;
        this.ftLsts = featureLists;
    }

    /*
     * public methods
     */
    public void check() {
        for (AnalysisSummaryElement ase : this.anlSumMap.keySet()) {
            if (ase.getAnalysisType() == AnalTp.LabelFree) {
                checkLCMS(ase, anlSumMap.get(ase));
            } else if (ase.getAnalysisType() == AnalTp.MS1LabelBased) {
                checkMS1(ase, anlSumMap.get(ase));
            } else if (ase.getAnalysisType() == AnalTp.MS2TagBased) {
                checkMS2(ase, anlSumMap.get(ase));
            } else if (ase.getAnalysisType() == AnalTp.SpectralCounting) {
                checkSC(ase, anlSumMap.get(ase));
            }
        }
    }

    public List<Message> getMsgs() {
        return msgs;
    }

    /*
     * private methods
     */
    private void checkLCMS(AnalysisSummaryElement ase, Boolean b) {
        if (ase == AnalysisSummaryElement.LCMSProtG) {
            if (isProtGQLExist(this.protGrpLst) != b.booleanValue()) {
                if (b.booleanValue()) {
                    msgs.add(new Message("Rule: If the cvParam values in AnalysisSummary states that a particular type of QuantLayer is present, "
                            + "then at least one quant layer MUST be present", Level.ERROR));
                    msgs.add(new Message("Problem: the value of \"label-free proteingroup level quantitation\" is \"true\", "
                            + "but there is no QuantLayer in ProteinGroupList", Level.ERROR));
                } else {
                    msgs.add(new Message("Rule: If the cvParam values in AnalysisSummary states that a particular type of QuantLayer is present, "
                            + "then at least one quant layer MUST be present", Level.ERROR));
                    msgs.add(new Message("Problem: the value of \"label-free proteingroup level quantitation\" is \"false\", "
                            + "but there is at least one QuantLayer in ProteinGroupList", Level.ERROR));
                }
            }
        }

        if (ase == AnalysisSummaryElement.LCMSProt) {
            if (isProtQLExist(this.protLst) != b.booleanValue()) {
                if (b.booleanValue()) {
                    msgs.add(new Message("Rule: If the cvParam values in AnalysisSummary states that a particular type of QuantLayer is present, "
                            + "then at least one quant layer MUST be present", Level.ERROR));
                    msgs.add(new Message("Problem: the value of \"label-free protein level quantitation\" is \"true\", "
                            + "but there is no QuantLayer in ProteinGroupList", Level.ERROR));
                } else {
                    msgs.add(new Message("Rule: If the cvParam values in AnalysisSummary states that a particular type of QuantLayer is present, "
                            + "then at least one quant layer MUST be present", Level.ERROR));
                    msgs.add(new Message("Problem: the value of \"label-free protein level quantitation\" is \"false\", "
                            + "but there is at least one QuantLayer in ProteinList", Level.ERROR));
                }
            }
        }

        if (ase == AnalysisSummaryElement.LCMSPep) {
        }

        if (ase == AnalysisSummaryElement.LCMSFt) {
        }
    }

    private void checkMS1(AnalysisSummaryElement ase, Boolean b) {
    }

    private void checkMS2(AnalysisSummaryElement ase, Boolean b) {
    }

    private void checkSC(AnalysisSummaryElement ase, Boolean b) {
    }

    private boolean isPepQLExist(PeptideConsensusListType peptideConsensusList) {
        boolean b = false;
        if (peptideConsensusList.getAssayQuantLayer() != null
                || peptideConsensusList.getStudyVariableQuantLayer() != null
                || peptideConsensusList.getRatioQuantLayer() != null
                || peptideConsensusList.getGlobalQuantLayer() != null) {
            b = true;
        }
        return b;
    }

    private boolean isProtGQLExist(ProteinGroupListType proteinGroupList) {
        boolean b = false;
        if (proteinGroupList.getAssayQuantLayer() != null
                || proteinGroupList.getStudyVariableQuantLayer() != null
                || proteinGroupList.getRatioQuantLayer() != null
                || proteinGroupList.getGlobalQuantLayer() != null) {
            b = true;
        }
        return b;
    }

    private boolean isProtQLExist(ProteinListType proteinList) {
        boolean b = false;
        if (proteinList.getAssayQuantLayer() != null
                || proteinList.getStudyVariableQuantLayer() != null
                || proteinList.getRatioQuantLayer() != null
                || proteinList.getGlobalQuantLayer() != null) {
            b = true;
        }
        return b;
    }

    private boolean isFtQLExist(FeatureListType featureList) {
        boolean b = false;
        if (featureList.getMS2AssayQuantLayer() != null
                || featureList.getMS2StudyVariableQuantLayer() != null
                || featureList.getMS2RatioQuantLayer() != null
                || featureList.getFeatureQuantLayer() != null) {
            b = true;
        }
        return b;
    }
}
