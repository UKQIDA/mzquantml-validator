/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.utils;

import java.util.ArrayList;
import uk.ac.liv.jmzqml.model.mzqml.FeatureList;
import uk.ac.liv.jmzqml.model.mzqml.PeptideConsensusList;
import uk.ac.liv.jmzqml.model.mzqml.ProteinGroupList;
import uk.ac.liv.jmzqml.model.mzqml.ProteinList;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType.AnalTp;

/**
 *
 * @author ddq
 */
public enum AnalysisSummaryElement {

    SCPep(AnalTp.SpectralCounting, PeptideConsensusList.class, "MS:1002015", "spectral count peptide level quantitation"),
    SCProt(AnalTp.SpectralCounting, ProteinList.class, "MS:1002016", "spectral count protein level quantitation"),
    SCProtG(AnalTp.SpectralCounting, ProteinGroupList.class, "MS:1002017", "spectral count proteingroup level quantitation"),
    LCMSFt(AnalTp.LabelFree, FeatureList.class, "MS:1002019", "label-free raw feature quantitation"),
    LCMSPep(AnalTp.LabelFree, PeptideConsensusList.class, "MS:1002020", "label-free peptide level quantitation"),
    LCMSProt(AnalTp.LabelFree, ProteinList.class, "MS:1002021", "label-free protein level quantitation"),
    LCMSProtG(AnalTp.LabelFree, ProteinGroupList.class, "MS:1002022", "label-free proteingroup level quantitation"),
    MS1Ft(AnalTp.MS1LabelBased, FeatureList.class, "MS:1002001", "MS1 label-based raw feature quantitation"),
    MS1Pep(AnalTp.MS1LabelBased, PeptideConsensusList.class, "MS:1002002", "MS1 label-based peptide level quantitation"),
    MS1Prot(AnalTp.MS1LabelBased, ProteinList.class, "MS:1002003", "MS1 label-based protein level quantitation"),
    MS1ProtG(AnalTp.MS1LabelBased, ProteinGroupList.class, "MS:1002004", "MS1 label-based proteingroup level quantitation"),
    MS2Ft(AnalTp.MS2TagBased, FeatureList.class, "MS:1002024", "MS2 tag-based feature level quantitation"),
    MS2Pep(AnalTp.MS2TagBased, PeptideConsensusList.class, "MS:1002025", "MS2 tag-based peptide level quantitation"),
    MS2Prot(AnalTp.MS2TagBased, ProteinList.class, "MS:1002026", "MS2 tag-based protein level quantitation"),
    MS2ProtG(AnalTp.MS2TagBased, ProteinGroupList.class, "MS:1002027", "MS2 tag-based proteingroup level quantitation");
    private AnalysisType.AnalTp at;
    private Class cls;
    private String accession;
    private String name;

    private AnalysisSummaryElement(AnalysisType.AnalTp analysisType, Class clazz,
                                   String accession, String cvTerm) {
        this.at = analysisType;
        this.cls = clazz;
        this.accession = accession;
        this.name = cvTerm;
    }

    public AnalysisType.AnalTp getAnalysisType() {
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

    public static AnalysisSummaryElement getType(String cvTerm) {
        for (AnalysisSummaryElement type : AnalysisSummaryElement.values()) {
            if (type.getName() != null && type.getName().equals(cvTerm)) {
                return type;
            }
        }
        return null;
    }
    
    public static ArrayList<AnalysisSummaryElement> valuesByType(AnalysisType at){
        ArrayList<AnalysisSummaryElement> aseList = new ArrayList<AnalysisSummaryElement>();
        for (AnalysisSummaryElement ase: AnalysisSummaryElement.values()){
            if (ase.getAnalysisType()==at.getAnalysisType()){
                aseList.add(ase);
            }
        }
        return aseList;
    }
}
