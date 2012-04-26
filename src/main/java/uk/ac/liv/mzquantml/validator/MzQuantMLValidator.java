package uk.ac.liv.mzquantml.validator;

import info.psidev.psi.pi.mzquantml.io.MzQuantMLUnmarshaller;
import info.psidev.psi.pi.mzquantml._1_0.*;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import uk.ac.liv.mzquantml.validator.rules.*;

/**
 *
 * @author Da Qi
 * @time 10:28:09 14-Mar-2012
 * @institution University of Liverpool
 */
public class MzQuantMLValidator {

    /**
     * @param args the command line arguments
     */
    public static String main(String fileName) throws FileNotFoundException {
        // TODO code application logic here
        String results = "Starting validation process......\n";

        results = results + "Loading MzQuantML file......\n";


        MzQuantMLUnmarshaller unmarshaller = new MzQuantMLUnmarshaller(fileName);
        MzQuantMLType mzq = (MzQuantMLType) unmarshaller.unmarshall();

        mzq.getAnalysisSummary();
        mzq.getAssayList();
        mzq.getAuditCollection();
        mzq.getBibliographicReference();
        mzq.getCreationDate();
        mzq.getCvList();
        mzq.getDataProcessingList();
        mzq.getFeatureList();
        mzq.getInputFiles();
        mzq.getPeptideConsensusList();
        mzq.getProteinGroupList();
        mzq.getProteinList();
        mzq.getProvider();
        mzq.getRatioList();
        mzq.getSmallMoleculeList();
        mzq.getSoftwareList();
        mzq.getStudyVariableList();
        mzq.getVersion();
       
        
        /*
         *  Rule instance
         */
        
        UniqueObjectRefRule uniqueObjectRefRule = new UniqueObjectRefRule();
        NumeratorDenominatorRule numeratorDenominatorRule = new NumeratorDenominatorRule();
        
        /*
         * validate ProteinList
         */
        ProteinListType proteinList = mzq.getProteinList();
        if (proteinList != null) {
            System.out.println("\nValidating ProteinList......");
            results = results + "\nValidating ProteinList......\n";

            // get AssayQuantLayer
            List<QuantLayerType> prot_assay_QL_list = proteinList.getAssayQuantLayer();
            Iterator i_prot_assay_QL_list = prot_assay_QL_list.iterator();
            while (i_prot_assay_QL_list.hasNext()) {
                QuantLayerType QL = (QuantLayerType) (i_prot_assay_QL_list.next());

                //System.out.println(QL.getColumnIndex().toString());
                String output_checkRefNumConsistency = checkRefNumConsistency(QL);
                if (!output_checkRefNumConsistency.isEmpty()) {
                    String ql_id = QL.getId();
                    System.out.println("AssayQuantLayer in ProteinList is not validated in \"" + ql_id + "\"");
                    System.out.println(checkRefNumConsistency(QL));

                    results = results + "AssayQuantLayer in ProteinList is not validated in \"" + ql_id + "\"\n";
                    results = results + checkRefNumConsistency(QL) + "\n";
                }
            }

            // get GlobalQuantLayer
            proteinList.getGlobalQuantLayer();

            // get StudyVariableQuantLayer
            List<QuantLayerType> prot_sv_QL_list = proteinList.getStudyVariableQuantLayer();
            Iterator i_prot_sv_QL_list = prot_sv_QL_list.iterator();
            while (i_prot_sv_QL_list.hasNext()) {
                QuantLayerType QL = (QuantLayerType) (i_prot_sv_QL_list.next());

                //System.out.println(QL.getColumnIndex().toString());
                String output_checkRefNumConsistency = checkRefNumConsistency(QL);
                if (!output_checkRefNumConsistency.isEmpty()) {
                    String ql_id = QL.getId();
                    System.out.println("StudyVariableQuantLayer in ProteinList is not validated in \"" + ql_id + "\"");
                    System.out.println(checkRefNumConsistency(QL));

                    results = results + "StudyVariableQuantLayer in ProteinList is not validated in \"" + ql_id + "\"\n";
                    results = results + checkRefNumConsistency(QL) + "\n";
                }
            }
        }


        /*
         * validate PeptideConsensusList
         */
        List<PeptideConsensusListType> peptideConsensusLists = mzq.getPeptideConsensusList();
        if (peptideConsensusLists != null) {
            System.out.println("\nValidating PeptideConsensusLists......");
            results = results + "\nValidating PeptideConsensusLists......\n";

            Iterator i_peptideConsensusLists = peptideConsensusLists.iterator();
            while (i_peptideConsensusLists.hasNext()) {
                PeptideConsensusListType pep_con_list = (PeptideConsensusListType) i_peptideConsensusLists.next();
                String pep_con_list_id = pep_con_list.getId();

                List<PeptideConsensusType> pep_cons = pep_con_list.getPeptideConsensus();
                Iterator i_pep_cons = pep_cons.iterator();
                while (i_pep_cons.hasNext()) {
                    PeptideConsensusType peptideConsensus = (PeptideConsensusType) i_pep_cons.next();

                    String out_checkPeptideConsensusAssayRefFeatureRef =
                            checkPeptideConsensusAssayRefFeatureRef(peptideConsensus);
                    if (!out_checkPeptideConsensusAssayRefFeatureRef.isEmpty()) {
                        System.out.println(checkPeptideConsensusAssayRefFeatureRef(peptideConsensus));
                        results = results + checkPeptideConsensusAssayRefFeatureRef(peptideConsensus) + "\n";
                    }
                }

                // get AssaayQuantLayer
                List<QuantLayerType> pep_assay_QL_list = pep_con_list.getAssayQuantLayer();                
                Iterator i_pep_assay_QL_list = pep_assay_QL_list.iterator();
                while (i_pep_assay_QL_list.hasNext()) {
                    QuantLayerType QL = (QuantLayerType) (i_pep_assay_QL_list.next());
                    String ql_id = QL.getId();
                    uniqueObjectRefRule.check(QL);
                    results = results + "AssayQuantLayer in PeptideConsensusList \""
                            + pep_con_list_id + "\" has the following duplicated object_ref:\n";
                    results = results + uniqueObjectRefRule.getMessage().toString() + "\n";
                    
                    //System.out.println(QL.getColumnIndex().toString());
                    String output_checkRefNumConsistency = checkRefNumConsistency(QL);
                    if (!output_checkRefNumConsistency.isEmpty()) {
                        
                        System.out.println("AssayQuantLayer in PeptideConsensusList \""
                                + pep_con_list_id + "\" is not validated in \"" + ql_id + "\"");
                        System.out.println(checkRefNumConsistency(QL));

                        results = results + "AssayQuantLayer in PeptideConsensusList \""
                                + pep_con_list_id + "\" is not validated in \"" + ql_id + "\"\n";
                        results = results + checkRefNumConsistency(QL) + "\n";
                    }
                }

                // get GlobalQuantLayer
                pep_con_list.getGlobalQuantLayer();

                // get StudyVariableQuantLayer
                List<QuantLayerType> pep_sv_QL_list = pep_con_list.getStudyVariableQuantLayer();
                Iterator i_pep_sv_QL_list = pep_sv_QL_list.iterator();
                while (i_pep_sv_QL_list.hasNext()) {
                    QuantLayerType QL = (QuantLayerType) (i_pep_sv_QL_list.next());

                    //System.out.println(QL.getColumnIndex().toString());
                    String output_checkRefNumConsistency = checkRefNumConsistency(QL);
                    if (!output_checkRefNumConsistency.isEmpty()) {
                        String ql_id = QL.getId();
                        System.out.println("StudyVariableQuantLayer in PeptideConsensusList \""
                                + pep_con_list_id + "\" is not validated in \"" + ql_id + "\"");
                        System.out.println(checkRefNumConsistency(QL));

                        results = results + "StudyVariableQuantLayer in PeptideConsensusList \""
                                + pep_con_list_id + "\" is not validated in \"" + ql_id + "\"\n";
                        results = results + checkRefNumConsistency(QL) + "\n";
                    }
                }
            }
        }


        /*
         * validate FeatureList
         */
        List<FeatureListType> featureLists = mzq.getFeatureList();
        if (featureLists != null) {
            System.out.println("\nValidating FeatureLists......");
            results = results + "\nValidating FeatureLists......\n";

            Iterator i_featureLists = featureLists.iterator();
            while (i_featureLists.hasNext()) {
                FeatureListType feature_list = (FeatureListType) i_featureLists.next();
                String feature_list_id = feature_list.getId();

                // get AssaayQuantLayer
                List<QuantLayerType> feature_assay_QL_list = feature_list.getMS2AssayQuantLayer();
                Iterator i_feature_assay_QL_list = feature_assay_QL_list.iterator();
                while (i_feature_assay_QL_list.hasNext()) {
                    QuantLayerType QL = (QuantLayerType) (i_feature_assay_QL_list.next());

                    //System.out.println(QL.getColumnIndex().toString());
                    String output_checkRefNumConsistency = checkRefNumConsistency(QL);
                    if (!output_checkRefNumConsistency.isEmpty()) {
                        String ql_id = QL.getId();
                        System.out.println("MS2AssayQuantLayer in FeatureList \""
                                + feature_list_id + "\" is not validated in \"" + ql_id + "\"");
                        System.out.println(checkRefNumConsistency(QL));

                        results = results + "MS2AssayQuantLayer in FeatureList \""
                                + feature_list_id + "\" is not validated in \"" + ql_id + "\"\n";
                        results = results + checkRefNumConsistency(QL) + "\n";
                    }
                }

                // get StudyVariableQuantLayer
                List<QuantLayerType> feature_sv_QL_list = feature_list.getMS2StudyVariableQuantLayer();
                Iterator i_feature_sv_QL_list = feature_sv_QL_list.iterator();
                while (i_feature_sv_QL_list.hasNext()) {
                    QuantLayerType QL = (QuantLayerType) (i_feature_sv_QL_list.next());

                    //System.out.println(QL.getColumnIndex().toString());
                    String output_checkRefNumConsistency = checkRefNumConsistency(QL);
                    if (!output_checkRefNumConsistency.isEmpty()) {
                        String ql_id = QL.getId();
                        System.out.println("MS2StudyVariableQuantLayer in FeatureList \""
                                + feature_list_id + "\" is not validated in \"" + ql_id + "\"");
                        System.out.println(checkRefNumConsistency(QL));

                        results = results + "MS2StudyVariableQuantLayer in FeatureList \""
                                + feature_list_id + "\" is not validated in \"" + ql_id + "\"\n";
                        results = results + checkRefNumConsistency(QL) + "\n";
                    }
                }

                // get FeatureQuantLayer
                feature_list.getFeatureQuantLayer();
            }
        }
        
        /*
         * Validate RatioList
         */
        RatioListType ratioList = mzq.getRatioList();
        numeratorDenominatorRule.check(ratioList);
        if (!numeratorDenominatorRule.isValid()){
            results = results + "N/D rule is invalid \n";
        }
        
        
        System.out.println("MzQuantML validation process is finished.");
        results = results + "MzQuantML validation process is finished.\n";
        return results;
    }

    private static String checkRefNumConsistency(QuantLayerType ql) {
        String output = "";
        int refNum = ql.getColumnIndex().size();
        DataMatrixType dm = ql.getDataMatrix();
        List<RowType> row_list = dm.getRow();
        Iterator i_row_list = row_list.iterator();
        while (i_row_list.hasNext()) {
            RowType row = (RowType) i_row_list.next();
            int colNum = row.getValue().size();

            if (colNum != refNum) {
                output = output + "Row \"" + row.getObjectRef().toString()
                        + "\" has difference numbers of value from column indices.\n";
                //break;
            }
        }
        return output;
    }

    private static String checkPeptideConsensusAssayRefFeatureRef(PeptideConsensusType pep) {
        String output = "";
        int assayRef_num = pep.getAssayRefs().size();
        int featureRef_num = pep.getFeatureRefs().size();
        if (assayRef_num != featureRef_num) {
            output = "Peptide \"" + pep.getId() + "\" is not valid:\n";
            output = output + "The size of the Assay_refs list MUST always match the size of the Feature_refs list.";
        }
        return output;
    }
}
