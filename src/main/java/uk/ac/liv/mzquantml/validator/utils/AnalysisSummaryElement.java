/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.utils;

import info.psidev.psi.pi.mzquantml._1_0.FeatureListType;
import info.psidev.psi.pi.mzquantml._1_0.PeptideConsensusListType;
import info.psidev.psi.pi.mzquantml._1_0.ProteinGroupListType;
import info.psidev.psi.pi.mzquantml._1_0.ProteinListType;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType.AnalTp;

/**
 *
 * @author ddq
 */
public enum AnalysisSummaryElement {

    LCMSPep(AnalTp.LabelFree, PeptideConsensusListType.class, "label-free peptide level quantitation"),
    LCMSProt(AnalTp.LabelFree, ProteinListType.class, "label-free protein level quantitation"),
    LCMSProtG(AnalTp.LabelFree, ProteinGroupListType.class, "label-free proteingroup level quantitation"),
    LCMSFt(AnalTp.LabelFree, FeatureListType.class, "label-free raw feature quantitation"),
    SCPep(AnalTp.SpectralCounting, PeptideConsensusListType.class, "spectral count peptide level quantitation"),
    SCProt(AnalTp.SpectralCounting, ProteinListType.class, "spectral count protein level quantitation"),
    SCProtG(AnalTp.SpectralCounting, ProteinGroupListType.class, "spectral count proteingroup level quantitation"),
    MS1Pep(AnalTp.MS1LabelBased, PeptideConsensusListType.class, "MS1 label-based peptide level quantitation"),
    MS1Prot(AnalTp.MS1LabelBased, ProteinListType.class, "MS1 label-based protein level quantitation"),
    MS1ProtG(AnalTp.MS1LabelBased, ProteinGroupListType.class, "MS1 label-based proteingroup level quantitation"),
    MS1Ft(AnalTp.MS1LabelBased, FeatureListType.class, "MS1 label-based raw feature quantitation"),
    MS2Pep(AnalTp.MS2TagBased, PeptideConsensusListType.class, "MS2 tag-based peptide level quantitation"),
    MS2Prot(AnalTp.MS2TagBased, ProteinListType.class, "MS2 tag-based protein level quantitation"),
    MS2ProtG(AnalTp.MS2TagBased, ProteinGroupListType.class, "MS2 tag-based proteingroup level quantitation"),
    MS2Ft(AnalTp.MS2TagBased, FeatureListType.class, "MS2 tag-based feature level quantitation");
    private AnalysisType.AnalTp at;
    private Class cls;
    private String name;

    private AnalysisSummaryElement(AnalysisType.AnalTp analysisType, Class clazz, String cvTerm) {
        this.at = analysisType;
        this.cls = clazz;
        this.name = cvTerm;
    }

    public AnalysisType.AnalTp getAnalysisType() {
        return this.at;
    }

    public Class getClazz() {
        return this.cls;
    }

    public String getName() {
        return this.name;
    }

    public static AnalysisSummaryElement getType(String cvTerm) {
        for (AnalysisSummaryElement type : AnalysisSummaryElement.values()) {
            if (type.getName() != null && type.getName().equals(cvTerm)) {
                return type;
            }
        }
        return null;
    }
}
