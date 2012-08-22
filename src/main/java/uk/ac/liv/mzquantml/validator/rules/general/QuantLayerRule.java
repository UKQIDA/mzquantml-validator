/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.jmzqml.model.mzqml.FeatureList;
import uk.ac.liv.jmzqml.model.mzqml.PeptideConsensusList;
import uk.ac.liv.jmzqml.model.mzqml.ProteinGroupList;
import uk.ac.liv.jmzqml.model.mzqml.ProteinList;
import uk.ac.liv.mzquantml.validator.utils.AnalysisSummaryElement;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType.AnalTp;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @time 13:57:11 13-May-2012
 * @institution University of Liverpool
 */
public class QuantLayerRule {

    EnumMap<AnalysisSummaryElement, Boolean> anlSumMap;
    List<PeptideConsensusList> pepCnsLsts;
    ProteinGroupList protGrpLst;
    ProteinList protLst;
    List<FeatureList> ftLsts;
    ArrayList<Message> msgs = new ArrayList<Message>();

    /*
     * constructor
     */
    public QuantLayerRule() {
        this.msgs = null;
        this.anlSumMap = null;
    }

    public QuantLayerRule(EnumMap<AnalysisSummaryElement, Boolean> summaryMap,
                          ProteinGroupList proteinGroupList,
                          ProteinList proteinList,
                          List<PeptideConsensusList> peptideConsensusLists,
                          List<FeatureList> featureLists) {
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
                            + "there is no ProteinGroupList in the file\n", Level.ERROR));
                } else {
                    if (!isProtGQLExist(this.protGrpLst)) {
                        msgs.add(new Message(ase.getName() + " = \"true\", "
                                + "but there is no QuantLayer in ProteinGroupList "
                                + "\"" + this.protGrpLst.getId() + "\"\n", Level.ERROR));
                    }
                }
            } else { // proteingroup level quantitation = "false"
                if (this.protGrpLst != null && isProtGQLExist(this.protGrpLst)) {
                    msgs.add(new Message(ase.getName() + " = \"false\", "
                            + "but there is at least one QuantLayer in ProteinGroupList "
                            + "\"" + this.protGrpLst.getId() + "\"\n", Level.ERROR));
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
                            + "there is no ProteinList in the file\n", Level.ERROR));
                } else {
                    if (!isProtQLExist(this.protLst)) {
                        msgs.add(new Message(ase.getName() + " = \"true\", "
                                + "but there is no QuantLayer in ProteinList "
                                + "\"" + this.protLst.getId() + "\"\n", Level.ERROR));
                    }
                }
            } else { // protein level quantitation = "false"
                if (this.protLst != null && isProtQLExist(this.protLst)) {
                    msgs.add(new Message(ase.getName() + " = \"false\", "
                            + "but there is at least one QuantLayer in ProteinList "
                            + "\"" + this.protLst.getId() + "\"\n", Level.ERROR));
                }
            }
        }

        if (ase == AnalysisSummaryElement.LCMSPep
                || ase == AnalysisSummaryElement.MS1Pep
                || ase == AnalysisSummaryElement.SCPep) {
            if (this.pepCnsLsts == null && b.booleanValue()) {
                msgs.add(new Message(ase.getName() + " = \"true\", "
                        + "but there is no PeptideConsensusList\n", Level.ERROR));
            }
            if (this.pepCnsLsts != null) {
                for (PeptideConsensusList pepCnsLst : this.pepCnsLsts) {
                    if (isPepQLExist(pepCnsLst) != b.booleanValue()) {
                        if (b.booleanValue()) {
                            msgs.add(new Message("If the cvParam values in AnalysisSummary states that a particular type of QuantLayer is present, "
                                    + "then at least one quant layer MUST be present", Level.INFO));
                            msgs.add(new Message(ase.getName() + " = \"true\", "
                                    + "but there is no QuantLayer in PeptideConsensuList "
                                    + "\"" + pepCnsLst.getId() + "\"\n", Level.ERROR));
                        } else {
                            msgs.add(new Message("If the cvParam values in AnalysisSummary states that a particular type of QuantLayer is present, "
                                    + "then at least one quant layer MUST be present", Level.INFO));
                            msgs.add(new Message(ase.getName() + " = \"false\", "
                                    + "but there is at least one QuantLayer in PeptideConsensusList "
                                    + "\"" + pepCnsLst.getId() + "\"\n", Level.ERROR));
                        }
                    }
                }
            }
        }

        if (ase == AnalysisSummaryElement.LCMSFt || ase == AnalysisSummaryElement.MS1Ft) {
            if (this.ftLsts == null && b.booleanValue()) {
                msgs.add(new Message(ase.getName() + " = \"true\", "
                        + "but there is no FeatureList\n", Level.ERROR));
            }
            if (this.ftLsts != null) {
                for (FeatureList ftLst : this.ftLsts) {
                    if (b.booleanValue() && ftLst.getFeatureQuantLayer().isEmpty()) {
                        msgs.add(new Message(ase.getName() + " = \"true\", "
                                + "but there is no FeatureQuantLayer in FeatureList "
                                + "\"" + ftLst.getId() + "\"\n", Level.ERROR));
                    }
                    if (!b.booleanValue() && !ftLst.getFeatureQuantLayer().isEmpty()) {
                        msgs.add(new Message(ase.getName() + " = \"false\", "
                                + "but there is a FeatureQuantLayer in FeatureList "
                                + "\"" + ftLst.getId() + "\"\n", Level.ERROR));
                    }
                    if (isMS2QLExist(ftLst)) {
                        msgs.add(new Message("There MUST not be any MS2QuantLayers for " + ase.getAnalysisType().getName(), Level.INFO));
                        msgs.add(new Message("There is at least one MS2QuantLayer exist in FeatureList "
                                + "\"" + ftLst.getId() + "\"\n", Level.ERROR));
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
                            + "there is no ProteinGroupList in the file\n", Level.ERROR));
                } else {
                    if (!isProtGQLExist(this.protGrpLst)) {
                        msgs.add(new Message(ase.getName() + " = \"true\", "
                                + "but there is no QuantLayer in ProteinGroupList\n", Level.ERROR));
                    }
                }
            } else { // proteingroup level quantitation = "false"
                if (this.protGrpLst != null && isProtGQLExist(this.protGrpLst)) {
                    msgs.add(new Message(ase.getName() + " = \"false\", "
                            + "but there is at least one QuantLayer in ProteinGroupList\n", Level.ERROR));
                }
            }
        }

        if (ase == AnalysisSummaryElement.MS2Prot) {
            // protein level quantitation = "true"
            if (b.booleanValue()) {
                if (this.protLst == null) {
                    msgs.add(new Message(ase.getName() + " = \"true\", "
                            + "there is no ProteinList in the file\n", Level.ERROR));
                } else {
                    if (!isProtQLExist(this.protLst)) {
                        msgs.add(new Message(ase.getName() + " = \"true\", "
                                + "but there is no QuantLayer in ProteinList\n", Level.ERROR));
                    }
                }
            } else { // protein level quantitation = "false"
                if (this.protLst != null && isProtQLExist(this.protLst)) {
                    msgs.add(new Message(ase.getName() + " = \"false\", "
                            + "but there is at least one QuantLayer in ProteinList\n", Level.ERROR));
                }
            }
        }

        if (ase == AnalysisSummaryElement.MS2Pep) {
            if (this.pepCnsLsts == null && b.booleanValue()) {
                msgs.add(new Message(ase.getName() + " = \"true\", "
                        + "but there is no PeptideConsensusList\n", Level.ERROR));
            }
            if (this.pepCnsLsts != null) {
                for (PeptideConsensusList pepCnsLst : this.pepCnsLsts) {
                    if (isPepQLExist(pepCnsLst) != b.booleanValue()) {
                        if (b.booleanValue()) {
                            msgs.add(new Message("If the cvParam values in AnalysisSummary states that a particular type of QuantLayer is present, "
                                    + "then at least one quant layer MUST be present", Level.INFO));
                            msgs.add(new Message(ase.getName() + " = \"true\", "
                                    + "but there is no QuantLayer in PeptideConsensuList\n", Level.ERROR));
                        } else {
                            msgs.add(new Message("If the cvParam values in AnalysisSummary states that a particular type of QuantLayer is present, "
                                    + "then at least one quant layer MUST be present", Level.INFO));
                            msgs.add(new Message(ase.getName() + " = \"false\", "
                                    + "but there is at least one QuantLayer in PeptideConsensusList\n", Level.ERROR));
                        }
                    }
                }
            }
        }

        if (ase == AnalysisSummaryElement.MS2Ft) {
            if (this.ftLsts == null && b.booleanValue()) {
                msgs.add(new Message(ase.getName() + " = \"true\", "
                        + "but there is no FeatureList\n", Level.ERROR));
            }
            if (this.ftLsts != null) {
                for (FeatureList ftLst : this.ftLsts) {
                    if (b.booleanValue() && ftLst.getFeatureQuantLayer() == null) {
                        msgs.add(new Message(ase.getName() + " = \"true\", "
                                + "but there is no FeatureQuantLayer\n", Level.ERROR));
                    }
                    if (!b.booleanValue() && ftLst.getFeatureQuantLayer() != null) {
                        msgs.add(new Message(ase.getName() + " = \"false\", "
                                + "but there is a FeatureQuantLayer\n", Level.ERROR));
                    }
                    if (isMS2QLExist(ftLst)) {
                        msgs.add(new Message("There MUST not be any MS2QuantLayers for " + ase.getAnalysisType().getName(), Level.INFO));
                        msgs.add(new Message("There is at least one MS2QuantLayer exist\n", Level.ERROR));
                    }
                }
            }
        }
    }

    private boolean isPepQLExist(PeptideConsensusList peptideConsensusList) {
        boolean b = false;
        if (peptideConsensusList != null) {
            if (!peptideConsensusList.getAssayQuantLayer().isEmpty()
                    || !peptideConsensusList.getStudyVariableQuantLayer().isEmpty()
                    || !peptideConsensusList.getRatioQuantLayer().isEmpty()
                    || !peptideConsensusList.getGlobalQuantLayer().isEmpty()) {
                b = true;
            }
        }
        return b;
    }

    private boolean isProtGQLExist(ProteinGroupList proteinGroupList) {
        boolean b = false;
        if (proteinGroupList != null) {
            if (!proteinGroupList.getAssayQuantLayer().isEmpty()
                    || !proteinGroupList.getStudyVariableQuantLayer().isEmpty()
                    || !proteinGroupList.getRatioQuantLayer().isEmpty()
                    || !proteinGroupList.getGlobalQuantLayer().isEmpty()) {
                b = true;
            }
        }
        return b;
    }

    private boolean isProtQLExist(ProteinList proteinList) {
        boolean b = false;
        if (proteinList != null) {
            if (!proteinList.getAssayQuantLayer().isEmpty()
                    || !proteinList.getStudyVariableQuantLayer().isEmpty()
                    || !proteinList.getRatioQuantLayer().isEmpty()
                    || !proteinList.getGlobalQuantLayer().isEmpty()) {
                b = true;
            }
        }
        return b;
    }

    private boolean isFtQLExist(FeatureList featureList) {
        boolean b = false;
        if (featureList != null) {
            if (!featureList.getMS2AssayQuantLayer().isEmpty()
                    || !featureList.getMS2StudyVariableQuantLayer().isEmpty()
                    || !featureList.getMS2RatioQuantLayer().isEmpty()
                    || !featureList.getFeatureQuantLayer().isEmpty()) {
                b = true;
            }
        }
        return b;
    }

    private boolean isMS2QLExist(FeatureList featureList) {
        boolean b = false;
        if (featureList != null) {
            if (!featureList.getMS2AssayQuantLayer().isEmpty()
                    || !featureList.getMS2RatioQuantLayer().isEmpty()
                    || !featureList.getMS2StudyVariableQuantLayer().isEmpty()) {
                b = true;
            }
        }
        return b;
    }
}
