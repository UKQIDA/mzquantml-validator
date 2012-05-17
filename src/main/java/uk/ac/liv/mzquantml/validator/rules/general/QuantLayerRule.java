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
            if (ase.getAnalysisType() == AnalTp.LabelFree
                    || ase.getAnalysisType() == AnalTp.MS1LabelBased
                    || ase.getAnalysisType() == AnalTp.SpectralCounting) {
                checkNonMS2(ase, anlSumMap.get(ase));
            } else if (ase.getAnalysisType() == AnalTp.MS2TagBased) {
                checkMS2(ase, anlSumMap.get(ase));
            }
        }
    }

    public List<Message> getMsgs() {
        return msgs;
    }

    /*
     * private methods
     */
    private void checkNonMS2(AnalysisSummaryElement ase, Boolean b) {
        if (ase == AnalysisSummaryElement.LCMSProtG
                || ase == AnalysisSummaryElement.MS1ProtG
                || ase == AnalysisSummaryElement.SCProtG) {
            // proteingroup level quantitation = "true"
            if (b.booleanValue()) {
                if (this.protGrpLst == null) {
                    msgs.add(new Message(ase.getName() + " = \"true\", "
                            + "there is no ProteinGroupList in the file", Level.ERROR));
                } else {
                    if (!isProtGQLExist(this.protGrpLst)) {
                        msgs.add(new Message(ase.getName() + " = \"true\", "
                                + "but there is no QuantLayer in ProteinGroupList " + "\"" + this.protGrpLst.getId() + "\"", Level.ERROR));
                    }
                }
            } else { // proteingroup level quantitation = "false"
                if (this.protGrpLst != null && isProtGQLExist(this.protGrpLst)) {
                    msgs.add(new Message(ase.getName() + " = \"false\", "
                            + "but there is at least one QuantLayer in ProteinGroupList " + "\"" + this.protGrpLst.getId() + "\"", Level.ERROR));
                }
            }
        }

        if (ase == AnalysisSummaryElement.LCMSProt
                || ase == AnalysisSummaryElement.MS1Prot
                || ase == AnalysisSummaryElement.SCProt) {
            // protein level quantitation = "true"
            if (b.booleanValue()) {
                if (this.protLst == null) {
                    msgs.add(new Message(ase.getName() + " = \"true\", "
                            + "there is no ProteinList in the file", Level.ERROR));
                } else {
                    if (!isProtQLExist(this.protLst)) {
                        msgs.add(new Message(ase.getName() + " = \"true\", "
                                + "but there is no QuantLayer in ProteinList " + "\"" + this.protLst.getId() + "\"", Level.ERROR));
                    }
                }
            } else { // protein level quantitation = "false"
                if (this.protLst != null && isProtQLExist(this.protLst)) {
                    msgs.add(new Message(ase.getName() + " = \"false\", "
                            + "but there is at least one QuantLayer in ProteinList " + "\"" + this.protLst.getId() + "\"", Level.ERROR));
                }
            }
        }

        if (ase == AnalysisSummaryElement.LCMSPep
                || ase == AnalysisSummaryElement.MS1Pep
                || ase == AnalysisSummaryElement.SCPep) {
            if (this.pepCnsLsts == null && b.booleanValue()) {
                msgs.add(new Message(ase.getName() + " = \"true\", "
                        + "but there is no PeptideConsensusList", Level.ERROR));
            }
            if (this.pepCnsLsts != null) {
                for (PeptideConsensusListType pepCnsLst : this.pepCnsLsts) {
                    if (isPepQLExist(pepCnsLst) != b.booleanValue()) {
                        if (b.booleanValue()) {
                            msgs.add(new Message("If the cvParam values in AnalysisSummary states that a particular type of QuantLayer is present, "
                                    + "then at least one quant layer MUST be present", Level.INFO));
                            msgs.add(new Message(ase.getName() + " = \"true\", "
                                    + "but there is no QuantLayer in PeptideConsensuList " + "\"" + pepCnsLst.getId() + "\"", Level.ERROR));
                        } else {
                            msgs.add(new Message("If the cvParam values in AnalysisSummary states that a particular type of QuantLayer is present, "
                                    + "then at least one quant layer MUST be present", Level.INFO));
                            msgs.add(new Message(ase.getName() + " = \"false\", "
                                    + "but there is at least one QuantLayer in PeptideConsensusList " + "\"" + pepCnsLst.getId() + "\"", Level.ERROR));
                        }
                    }
                }
            }
        }

        if (ase == AnalysisSummaryElement.LCMSFt || ase == AnalysisSummaryElement.MS1Ft) {
            if (this.ftLsts == null && b.booleanValue()) {
                msgs.add(new Message(ase.getName() + " = \"true\", "
                        + "but there is no FeatureList", Level.ERROR));
            }
            if (this.ftLsts != null) {
                for (FeatureListType ftLst : this.ftLsts) {
                    if (b.booleanValue() && ftLst.getFeatureQuantLayer() == null) {
                        msgs.add(new Message(ase.getName() + " = \"true\", "
                                + "but there is no FeatureQuantLayer in FeatureList " + "\"" + ftLst.getId() + "\"", Level.ERROR));
                    }
                    if (!b.booleanValue() && ftLst.getFeatureQuantLayer() != null) {
                        msgs.add(new Message(ase.getName() + " = \"false\", "
                                + "but there is a FeatureQuantLayer in FeatureList " + "\"" + ftLst.getId() + "\"", Level.ERROR));
                    }
                    if (isMS2QLExist(ftLst)) {
                        msgs.add(new Message("There MUST not be any MS2QuantLayers for " + ase.getAnalysisType().getName(), Level.INFO));
                        msgs.add(new Message("There is at least one MS2QuantLayer exist in FeatureList " + "\"" + ftLst.getId() + "\"", Level.ERROR));
                    }
                }
            }
        }
    }

    private void checkMS2(AnalysisSummaryElement ase, Boolean b) {
        if (ase == AnalysisSummaryElement.MS2ProtG) {
            // proteingroup level quantitation = "true"
            if (b.booleanValue()) {
                if (this.protGrpLst == null) {
                    msgs.add(new Message(ase.getName() + " = \"true\", "
                            + "there is no ProteinGroupList in the file", Level.ERROR));
                } else {
                    if (!isProtGQLExist(this.protGrpLst)) {
                        msgs.add(new Message(ase.getName() + " = \"true\", "
                                + "but there is no QuantLayer in ProteinGroupList", Level.ERROR));
                    }
                }
            } else { // proteingroup level quantitation = "false"
                if (this.protGrpLst != null && isProtGQLExist(this.protGrpLst)) {
                    msgs.add(new Message(ase.getName() + " = \"false\", "
                            + "but there is at least one QuantLayer in ProteinGroupList", Level.ERROR));
                }
            }
        }

        if (ase == AnalysisSummaryElement.MS2Prot) {
            // protein level quantitation = "true"
            if (b.booleanValue()) {
                if (this.protLst == null) {
                    msgs.add(new Message(ase.getName() + " = \"true\", "
                            + "there is no ProteinList in the file", Level.ERROR));
                } else {
                    if (!isProtQLExist(this.protLst)) {
                        msgs.add(new Message(ase.getName() + " = \"true\", "
                                + "but there is no QuantLayer in ProteinList", Level.ERROR));
                    }
                }
            } else { // protein level quantitation = "false"
                if (this.protLst != null && isProtQLExist(this.protLst)) {
                    msgs.add(new Message(ase.getName() + " = \"false\", "
                            + "but there is at least one QuantLayer in ProteinList", Level.ERROR));
                }
            }
        }

        if (ase == AnalysisSummaryElement.MS2Pep) {
            if (this.pepCnsLsts == null && b.booleanValue()) {
                msgs.add(new Message(ase.getName() + " = \"true\", "
                        + "but there is no PeptideConsensusList", Level.ERROR));
            }
            if (this.pepCnsLsts != null) {
                for (PeptideConsensusListType pepCnsLst : this.pepCnsLsts) {
                    if (isPepQLExist(pepCnsLst) != b.booleanValue()) {
                        if (b.booleanValue()) {
                            msgs.add(new Message("If the cvParam values in AnalysisSummary states that a particular type of QuantLayer is present, "
                                    + "then at least one quant layer MUST be present", Level.INFO));
                            msgs.add(new Message(ase.getName() + " = \"true\", "
                                    + "but there is no QuantLayer in PeptideConsensuList", Level.ERROR));
                        } else {
                            msgs.add(new Message("If the cvParam values in AnalysisSummary states that a particular type of QuantLayer is present, "
                                    + "then at least one quant layer MUST be present", Level.INFO));
                            msgs.add(new Message(ase.getName() + " = \"false\", "
                                    + "but there is at least one QuantLayer in PeptideConsensusList", Level.ERROR));
                        }
                    }
                }
            }
        }

        if (ase == AnalysisSummaryElement.MS2Ft) {
            if (this.ftLsts == null && b.booleanValue()) {
                msgs.add(new Message(ase.getName() + " = \"true\", "
                        + "but there is no FeatureList", Level.ERROR));
            }
            if (this.ftLsts != null) {
                for (FeatureListType ftLst : this.ftLsts) {
                    if (b.booleanValue() && ftLst.getFeatureQuantLayer() == null) {
                        msgs.add(new Message(ase.getName() + " = \"true\", "
                                + "but there is no FeatureQuantLayer", Level.ERROR));
                    }
                    if (!b.booleanValue() && ftLst.getFeatureQuantLayer() != null) {
                        msgs.add(new Message(ase.getName() + " = \"false\", "
                                + "but there is a FeatureQuantLayer", Level.ERROR));
                    }
                    if (isMS2QLExist(ftLst)) {
                        msgs.add(new Message("There MUST not be any MS2QuantLayers for " + ase.getAnalysisType().getName(), Level.INFO));
                        msgs.add(new Message("There is at least one MS2QuantLayer exist", Level.ERROR));
                    }
                }
            }
        }
    }

    private boolean isPepQLExist(PeptideConsensusListType peptideConsensusList) {
        boolean b = false;
        if (peptideConsensusList != null) {
            if (peptideConsensusList.getAssayQuantLayer() != null
                    || peptideConsensusList.getStudyVariableQuantLayer() != null
                    || peptideConsensusList.getRatioQuantLayer() != null
                    || peptideConsensusList.getGlobalQuantLayer() != null) {
                b = true;
            }
        }
        return b;
    }

    private boolean isProtGQLExist(ProteinGroupListType proteinGroupList) {
        boolean b = false;
        if (proteinGroupList != null) {
            if (proteinGroupList.getAssayQuantLayer() != null
                    || proteinGroupList.getStudyVariableQuantLayer() != null
                    || proteinGroupList.getRatioQuantLayer() != null
                    || proteinGroupList.getGlobalQuantLayer() != null) {
                b = true;
            }
        }
        return b;
    }

    private boolean isProtQLExist(ProteinListType proteinList) {
        boolean b = false;
        if (proteinList != null) {
            if (proteinList.getAssayQuantLayer() != null
                    || proteinList.getStudyVariableQuantLayer() != null
                    || proteinList.getRatioQuantLayer() != null
                    || proteinList.getGlobalQuantLayer() != null) {
                b = true;
            }
        }
        return b;
    }

    private boolean isFtQLExist(FeatureListType featureList) {
        boolean b = false;
        if (featureList != null) {
            if (featureList.getMS2AssayQuantLayer() != null
                    || featureList.getMS2StudyVariableQuantLayer() != null
                    || featureList.getMS2RatioQuantLayer() != null
                    || featureList.getFeatureQuantLayer() != null) {
                b = true;
            }
        }
        return b;
    }

    private boolean isMS2QLExist(FeatureListType featureList) {
        boolean b = false;
        if (featureList != null) {
            if (featureList.getMS2AssayQuantLayer() != null
                    || featureList.getMS2RatioQuantLayer() != null
                    || featureList.getMS2StudyVariableQuantLayer() != null) {
                b = true;
            }
        }
        return b;
    }
}
