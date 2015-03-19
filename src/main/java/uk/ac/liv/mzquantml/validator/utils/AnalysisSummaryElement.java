
package uk.ac.liv.mzquantml.validator.utils;

import java.util.ArrayList;
import java.util.List;
import uk.ac.liv.jmzqml.model.mzqml.FeatureList;
import uk.ac.liv.jmzqml.model.mzqml.PeptideConsensusList;
import uk.ac.liv.jmzqml.model.mzqml.ProteinGroupList;
import uk.ac.liv.jmzqml.model.mzqml.ProteinList;

/**
 *
 * @author ddq
 */
public enum AnalysisSummaryElement {

    SCPep(AnalysisType.SpectralCounting, PeptideConsensusList.class, "MS:1002015", "spectral count peptide level quantitation"),
    SCProt(AnalysisType.SpectralCounting, ProteinList.class, "MS:1002016", "spectral count protein level quantitation"),
    SCProtG(AnalysisType.SpectralCounting, ProteinGroupList.class, "MS:1002017", "spectral count proteingroup level quantitation"),
    LCMSFt(AnalysisType.LabelFree, FeatureList.class, "MS:1002019", "label-free raw feature quantitation"),
    LCMSPep(AnalysisType.LabelFree, PeptideConsensusList.class, "MS:1002020", "label-free peptide level quantitation"),
    LCMSProt(AnalysisType.LabelFree, ProteinList.class, "MS:1002021", "label-free protein level quantitation"),
    LCMSProtG(AnalysisType.LabelFree, ProteinGroupList.class, "MS:1002022", "label-free proteingroup level quantitation"),
    MS1Ft(AnalysisType.MS1LabelBased, FeatureList.class, "MS:1002001", "MS1 label-based raw feature quantitation"),
    MS1Pep(AnalysisType.MS1LabelBased, PeptideConsensusList.class, "MS:1002002", "MS1 label-based peptide level quantitation"),
    MS1Prot(AnalysisType.MS1LabelBased, ProteinList.class, "MS:1002003", "MS1 label-based protein level quantitation"),
    MS1ProtG(AnalysisType.MS1LabelBased, ProteinGroupList.class, "MS:1002004", "MS1 label-based proteingroup level quantitation"),
    MS2Ft(AnalysisType.MS2TagBased, FeatureList.class, "MS:1002024", "MS2 tag-based feature level quantitation"),
    MS2Pep(AnalysisType.MS2TagBased, PeptideConsensusList.class, "MS:1002025", "MS2 tag-based peptide level quantitation"),
    MS2Prot(AnalysisType.MS2TagBased, ProteinList.class, "MS:1002026", "MS2 tag-based protein level quantitation"),
    MS2ProtG(AnalysisType.MS2TagBased, ProteinGroupList.class, "MS:1002027", "MS2 tag-based proteingroup level quantitation"),
    SRMPep(AnalysisType.SRM, PeptideConsensusList.class, "MS:1002282", "SRM peptide level quantitation"),
    SRMProt(AnalysisType.SRM, ProteinList.class, "MS:1002283", "SRM protein level quantitation"),
    SRMProtG(AnalysisType.SRM, ProteinGroupList.class, "MS:1002284", "SRM proteingroup level quantitation"),
    SRMFt(AnalysisType.SRM, FeatureList.class, "MS:1002281", "SRM feature level quantitation");
    private AnalysisType at;
    private Class cls;
    private String accession;
    private String name;

    private AnalysisSummaryElement(AnalysisType analysisType, Class clazz,
                                   String accession, String cvTerm) {
        this.at = analysisType;
        this.cls = clazz;
        this.accession = accession;
        this.name = cvTerm;
    }

    public AnalysisType getAnalysisType() {
        return this.at;
    }

    public Class getClazz() {
        return this.cls;
    }

    public String getAccession() {
        return this.accession;
    }

    public String getName() {
        return this.name;
    }

    public static AnalysisSummaryElement getASEbyName(String cvTermName) {
        for (AnalysisSummaryElement ase : AnalysisSummaryElement.values()) {
            if (ase.getName() != null && ase.getName().equals(cvTermName)) {
                return ase;
            }
        }
        return null;
    }

    public static AnalysisSummaryElement getASEbyAccession(String accession) {
        for (AnalysisSummaryElement ase : AnalysisSummaryElement.values()) {
            if (ase.getAccession() != null && ase.getAccession().equals(accession)) {
                return ase;
            }
        }
        return null;
    }

    public static List<AnalysisSummaryElement> valuesByType(AnalysisType at) {
        List<AnalysisSummaryElement> aseList = new ArrayList<>();
        for (AnalysisSummaryElement ase : AnalysisSummaryElement.values()) {
            if (ase.getAnalysisType() == at) {
                aseList.add(ase);
            }
        }
        return aseList;
    }

}
