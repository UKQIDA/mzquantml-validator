
package uk.ac.liv.mzquantml.validator;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.log4j.Level;
import org.xml.sax.SAXException;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.liv.jmzqml.MzQuantMLElement;
import uk.ac.liv.jmzqml.model.mzqml.*;
import uk.ac.liv.jmzqml.xml.io.MzQuantMLUnmarshaller;
import uk.ac.liv.mzquantml.validator.cvmapping.MzQuantMLCvValidator;
import uk.ac.liv.mzquantml.validator.rules.general.*;
import uk.ac.liv.mzquantml.validator.utils.AnalysisSummaryElement;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType.AnalTp;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @time 10:28:09 14-Mar-2012
 * @institution University of Liverpool
 */
public class MzQuantMLValidator {

    private static AnalysisType at = new AnalysisType();
    private static EnumMap<AnalysisSummaryElement, Boolean> analysisSummaryMap;
    private static List<Message> msgs = new ArrayList<Message>();
    private String fileName;
    private boolean schemaValidating;
    private String schemaFn;
    private MzQuantMLUnmarshaller unmarshaller;

    public MzQuantMLValidator(String fileName, boolean schemaValidating,
                              String schemaFn) {
        this.fileName = fileName;
        this.schemaValidating = schemaValidating;
        this.schemaFn = schemaFn;
        this.unmarshaller = new MzQuantMLUnmarshaller(new File(this.fileName));
    }

    /**
     * @param args the command line arguments
     */
    public List<Message> validate(String fileName, boolean schemaValidating,
                                  String schemaFn)
            throws FileNotFoundException {

        msgs.clear();
//        msgs.add(new Message("Starting validation process......", Level.INFO));
//        msgs.add(new Message("Loading MzQuantML file......", Level.INFO));


//        MzQuantMLUnmarshaller unmarshaller;
//        if (schemaValidating) {
//            unmarshaller = new MzQuantMLUnmarshaller(fileName, schemaValidating, new File(schemaFn));
//        } else {
//            File schema = new File(getClass().getClassLoader().getResource("mzQuantML_1_0_0-rc2.xsd").getFile());
//            unmarshaller = new MzQuantMLUnmarshaller(fileName, true, schema);
//        }

        Unmarshaller unmarsh;
        MzQuantML mzq = new MzQuantML();

        /**
         * ****************************************************************
         * Schema validation happens before validating cv mapping and schema
         * rule If MzQuantML file is not schema valid, it is unable to continue
         * the validation process
         * ******************************************************************
         */
        if (schemaValidating) {
            try {
                JAXBContext context = JAXBContext.newInstance(new Class[]{MzQuantML.class
                        });
                unmarsh = context.createUnmarshaller();
                SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
                File schemaFile = new File(schemaFn);
                Schema schema = sf.newSchema(schemaFile);
                unmarsh.setSchema(schema);

                ValidationEventHandler veh = new ValidationEventHandler() {

                    @Override
                    public boolean handleEvent(ValidationEvent event) {
                        //ignore warnings
                        if (event.getSeverity() != ValidationEvent.WARNING) {
                            ValidationEventLocator vel = event.getLocator();
//                            System.out.println("Line:Col[" + vel.getLineNumber()
//                                    + ":" + vel.getColumnNumber()
//                                    + "]:" + event.getMessage());
                            msgs.add(new Message("Line:Col[" + vel.getLineNumber()
                                    + ":" + vel.getColumnNumber()
                                    + "]:" + event.getMessage()));
                        }
                        return true;
                    }

                };
                unmarsh.setEventHandler(veh);
                mzq = (MzQuantML) unmarsh.unmarshal(new FileReader(fileName));
            }
            catch (JAXBException ex) {
                Logger.getLogger(MzQuantMLValidator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            catch (SAXException ex) {
                ex.printStackTrace();
            }
        }
        else {

            try {
                JAXBContext context = JAXBContext.newInstance(new Class[]{MzQuantML.class
                        });
                unmarsh = context.createUnmarshaller();
                SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
                File schemaFile = new File(getClass().getClassLoader().getResource("mzQuantML_1_0_0.xsd").getFile());
                //File schemaFile = new File("mzQuantML_1_0_0-rc2.xsd");
                Schema schema = sf.newSchema(schemaFile);
                unmarsh.setSchema(schema);

                ValidationEventHandler veh = new ValidationEventHandler() {

                    @Override
                    public boolean handleEvent(ValidationEvent event) {
                        //ignore warnings
                        if (event.getSeverity() != ValidationEvent.WARNING) {
                            ValidationEventLocator vel = event.getLocator();
//                            System.out.println("Line:Col[" + vel.getLineNumber()
//                                    + ":" + vel.getColumnNumber()
//                                    + "]:" + event.getMessage());
                            msgs.add(new Message("Line:Col[" + vel.getLineNumber()
                                    + ":" + vel.getColumnNumber()
                                    + "]:" + event.getMessage()));
                        }
                        return true;
                    }

                };
                unmarsh.setEventHandler(veh);
                mzq = (MzQuantML) unmarsh.unmarshal(new FileReader(fileName));
            }
            catch (JAXBException ex) {
                Logger.getLogger(MzQuantMLValidator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            catch (SAXException ex) {
                ex.printStackTrace();
            }
        }

//        if (unmarshaller.getExceptionalMessages().isEmpty()) {

        /**
         * ********************************************************************
         * If the MzQuantML file is schema invalid, the codes will break before
         * this. If the MzQuantML file is schema valid, then the following codes
         * will be executed.
         * *********************************************************************
         */

        /*
         * get all mzQuantML elements
         */
        // jmzquantml 1.0.0-rc3-1.0.5
//        AnalysisSummary analysisSummary = mzq.getAnalysisSummary();
//        AssayList assayList = mzq.getAssayList();
//        AuditCollection auditCollection = mzq.getAuditCollection();
//        List<BibliographicReference> bibliographicReferences = mzq.getBibliographicReference();
//        Calendar creationDate = mzq.getCreationDate();
//        CvList cvList = mzq.getCvList();
//        DataProcessingList dataProcessingList = mzq.getDataProcessingList();
//        List<FeatureList> featureLists = mzq.getFeatureList();
//        InputFiles inputFiles = mzq.getInputFiles();
//        List<PeptideConsensusList> peptideConsensusLists = mzq.getPeptideConsensusList();
//        ProteinGroupList proteinGroupList = mzq.getProteinGroupList();
//        ProteinList proteinList = mzq.getProteinList();
//        Provider provider = mzq.getProvider();
//        RatioList ratioList = mzq.getRatioList();
//        SmallMoleculeList smallMoleculeList = mzq.getSmallMoleculeList();
//        SoftwareList softwareList = mzq.getSoftwareList();
//        StudyVariableList studyVariableList = mzq.getStudyVariableList();
//        String version = mzq.getVersion();
        /**
         * ********************************
         * Start checking the schema rule These rules are manually written
         * located in MzQuantML/schema folder.
         *
         * They are: Schema_rules_general.txt Schema_rules_LCMS_label_free.txt
         * Schema_rules_MS1_label_based.txt Schema_rules_MS2_tag_based.txt
         * Schema_rules_spectral_counting.txt *******************************
         */

        /*
         * check all mzQuantML elements one by one
         */
        AnalysisSummary analysisSummary = unmarshaller.unmarshal(MzQuantMLElement.AnalysisSummary);
        if (analysisSummary != null) {
            at = new AnalysisType(analysisSummary);
            if (at.getAnalysisType() != AnalTp.InvalidAnalysisType) {
                /**
                 * check AnalsisSummaryRule first check() return a boolean value
                 * return ture when there is no cv term missing return false
                 * when at least one cv term is missing
                 */
                AnalysisSummaryRule asr = new AnalysisSummaryRule(analysisSummary);
                if (asr.check()) {
                    checkAnalysisSummary(analysisSummary);
                }

                msgs.addAll(asr.getMsgs());
            }
            /**
             * If the mzq file doesn't contain valid cv term for
             * AnalysisSummary, validator will not continue to validate other cv
             * terms or user terms in it.
             */
            else {
                msgs.add(new Message("Missing cv term accession for specific technique in AnalysisSummary. "
                        + "Hence validator can not identify the technique used in this file. "
                        + "A valid mzq file MUST contain one of the following cv terms:\n"
                        + "\"" + AnalTp.LabelFree.toString() + "\"\n"
                        + "\"" + AnalTp.MS1LabelBased.toString() + "\"\n"
                        + "\"" + AnalTp.MS2TagBased.toString() + "\"\n"
                        + "\"" + AnalTp.SpectralCounting.toString() + "\"\n", Level.ERROR));
            }
        }
        else {
            // message there is no AnalysisSummary which is not acceptable
        }

        AssayList assayList = unmarshaller.unmarshal(MzQuantMLElement.AssayList);
        if (assayList != null) {
            checkAssayList(assayList);
        }
        else {
            // message there is no AssayList which is not acceptable
        }


        AuditCollection auditCollection = unmarshaller.unmarshal(MzQuantMLElement.AuditCollection);
        if (auditCollection != null) {
            checkAuditCollection(auditCollection);
        }
        else {
            // message
        }

        Iterator<BibliographicReference> bibliographicReferences = unmarshaller.unmarshalCollectionFromXpath(MzQuantMLElement.BibliographicReference);
        if (bibliographicReferences != null) {
            checkBibliographicReference(bibliographicReferences);
        }
        else {
            // message
        }


//        if (creationDate != null) {
//            checkCreationDate(creationDate);
//        }
//        else {
//            // message
//        }

        CvList cvList = unmarshaller.unmarshal(MzQuantMLElement.CvList);
        if (cvList != null) {
            checkCvList(cvList);
        }
        else {
            // message
        }

        DataProcessingList dataProcessingList = unmarshaller.unmarshal(MzQuantMLElement.DataProcessingList);
        if (dataProcessingList != null) {
            checkDataProcessingList(dataProcessingList);
        }
        else {
            // message
        }

        Iterator<FeatureList> featureLists = unmarshaller.unmarshalCollectionFromXpath(MzQuantMLElement.FeatureList);
        if (featureLists != null) {
            checkFeatureLists(featureLists);
        }
        else {
            // messages
        }

        InputFiles inputFiles = unmarshaller.unmarshal(MzQuantMLElement.InputFiles);
        if (inputFiles != null) {
            checkInputFiles(inputFiles);
        }
        else {
            // message there is no inputFiles which is not acceptable
        }

        Iterator<PeptideConsensusList> peptideConsensusLists = unmarshaller.unmarshalCollectionFromXpath(MzQuantMLElement.PeptideConsensusList);
        if (peptideConsensusLists != null) {
            checkPeptideConsensusLists(peptideConsensusLists);
        }
        else {
            // some message
        }


        ProteinGroupList proteinGroupList = unmarshaller.unmarshal(MzQuantMLElement.ProteinGroupList);
        if (proteinGroupList != null) {
            checkProteinGroupList(proteinGroupList);
        }
        else {
            // message
        }

        ProteinList proteinList = unmarshaller.unmarshal(MzQuantMLElement.ProteinList);
        if (proteinList != null) {
            checkProteinList(proteinList);
        }
        else {
            // message
        }

        Provider provider = unmarshaller.unmarshal(MzQuantMLElement.Provider);
        if (provider != null) {
            checkProvider(provider);
        }
        else {
            // message
        }

        RatioList ratioList = unmarshaller.unmarshal(MzQuantMLElement.RatioList);
        if (ratioList != null) {
            checkRatioList(ratioList);
        }
        else {
            // message
        }

        SmallMoleculeList smallMoleculeList = unmarshaller.unmarshal(MzQuantMLElement.SmallMoleculeList);
        if (smallMoleculeList != null) {
            checkSmallMoleculeList(smallMoleculeList);
        }
        else {
            // message
        }

        SoftwareList softwareList = unmarshaller.unmarshal(MzQuantMLElement.SoftwareList);
        if (softwareList != null) {
            checkSoftwareList(softwareList);
        }
        else {
            // message
        }

        StudyVariableList studyVariableList = unmarshaller.unmarshal(MzQuantMLElement.StudyVariableList);
        if (studyVariableList != null) {
            checkStudyVariableList(studyVariableList);
        }
        else {
            // message
        }

        String version = unmarshaller.getMzQuantMLVersion();
        if (version != null) {
            checkVersion(version);
        }
        else {
            // message
        }

        /*
         * ListsRule start here
         */
        if (inputFiles != null) {
            ListsRule listsRule = new ListsRule(at, inputFiles, proteinGroupList, proteinList,
                                                peptideConsensusLists, featureLists);
            listsRule.check();
            msgs.addAll(listsRule.getMsgs());
        }

        /*
         * QuantLayerRule start here
         */

        if (at.getAnalysisType() != AnalTp.InvalidAnalysisType) {
            /**
             * If the mzq file doesn't contain valid cv term for
             * AnalysisSummary, validator will not continue to validate
             * QuantLayerRule.
             */
            if (analysisSummary != null) {
                getAnalysisSummaryMap(analysisSummary);
            }
            QuantLayerRule quantLayerRule = new QuantLayerRule(analysisSummaryMap, proteinGroupList, proteinList, peptideConsensusLists, featureLists);
            quantLayerRule.check();
            msgs.addAll(quantLayerRule.getMsgs());
        }

        /**
         * **************************************************
         *
         * start validate Cv Mapping rule
         *
         * **************************************************
         */
        InputStream ontology = getOntologiesFileInputStream();
        InputStream mappingRule = getGeneralMappingRuleInputStream();
        MzQuantMLCvValidator cvValidator;
        try {
            cvValidator = new MzQuantMLCvValidator(ontology, mappingRule);
            final Collection<ValidatorMessage> validationResult = cvValidator.startValidation(fileName);

            for (ValidatorMessage vMsg : validationResult) {
                // convert MessageLevel to log4j Level
                Level lev = Level.toLevel(vMsg.getLevel().toString());
                msgs.add(new Message(vMsg.getRule().toString() + vMsg.getMessage(), lev));
            }
        }
        catch (ValidatorException ex) {
            Logger.getLogger(MzQuantMLValidator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (OntologyLoaderException ex) {
            Logger.getLogger(MzQuantMLValidator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
//        } else {
//            msgs.add(new Message(unmarshaller.getExceptionalMessages()));
//            msgs.add(new Message("Semantic validation processing will not perform as this file is not schema valid!", Level.ERROR));
//        }


        /*
         * final output
         */
        System.out.println("MzQuantML validation process is finished");
        msgs.add(new Message("MzQuantML validation process is finished", Level.INFO));

        return msgs;
    }


    /*
     * ***************************************
     *
     * All the check*** functions start here. Organized alphabetically
     *
     * ***************************************
     */
    private void checkAnalysisSummary(AnalysisSummary analysisSummary) {
//        analysisSummaryMap = new EnumMap<AnalysisSummaryElement, Boolean>(AnalysisSummaryElement.class);

        List<AbstractParam> paramGroups = analysisSummary.getParamGroup();
        for (AbstractParam param : paramGroups) {
            if (param instanceof CvParam) {
                    CvParam cp = (CvParam) param;
                    String name = cp.getName();
                    String value = cp.getValue();
                    String accession = cp.getAccession();

                    String targetClassId = "AnalysisSummary";
                    //Object cvRef = cp.getCvRef();
                    String cvRef = cp.getCvRef();
                    
                    msgs.addAll(checkObjectRef(targetClassId, cvRef,
                                               uk.ac.liv.jmzqml.model.mzqml.Cv.class, this.unmarshaller));

                    String unitAccession = cp.getUnitAccession();
                    String unitCvRef = cp.getUnitCvRef();
                    String unitName = cp.getUnitName();           
            }
        }
        String targetClassId = "AnalysisSummary";
        checkParamGroups(targetClassId, paramGroups);
    }

    private void checkAssayList(AssayList assayList) {
        List<Assay> assays = assayList.getAssay();
        String id = assayList.getId();

        checkAssays(assays);

        //AssayLabelRule start here
        AssayLabelRule alr = new AssayLabelRule(at, assayList);
        alr.check();
        msgs.addAll(alr.getMsgs());
    }

    private void checkAssays(List<Assay> assays) {
        if (assays != null) {
            for (Assay assay : assays) {
                String targetClassId = assay.getId();

                //TODO: this is temporary turned off. turn the checking on later
                
//                List<Object> identificationFileRefs = assay.getIdentificationFileRefs();
//                if (identificationFileRefs != null) {
//                    for (Object ref : identificationFileRefs) {
//                        msgs.addAll(checkObjectRef(targetClassId, ref,
//                                                   uk.ac.liv.jmzqml.model.mzqml.IdentificationFile.class, this.unmarshaller));
//                    }
//                }

                List<AbstractParam> paramGroups = assay.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                String rawFilesGroupRef = assay.getRawFilesGroupRef();

                if (rawFilesGroupRef != null) {
                    msgs.addAll(checkObjectRef(targetClassId, rawFilesGroupRef,
                                               uk.ac.liv.jmzqml.model.mzqml.RawFilesGroup.class, this.unmarshaller));
                }

            }
        }
    }

    private void checkAuditCollection(AuditCollection auditCollection) {
//        List<AbstractContact> personOrOrganizations = auditCollection.getPersonOrOrganization();
        List<Person> persons = auditCollection.getPerson();
        List<Organization> orgs = auditCollection.getOrganization();
        //TODO: split to separated methods?
//        checkPersonOrOrganizations(persons);
//        checkPersonOrOrganizations(orgs);
    }

    private void checkBibliographicReference(
            Iterator<BibliographicReference> bibliographicReferences) {
        if (bibliographicReferences != null) {
            //for (BibliographicReference bibRef : bibliographicReferences) {
            while (bibliographicReferences.hasNext()) {
                BibliographicReference bibRef = bibliographicReferences.next();
                bibRef.getAuthors();
                bibRef.getDoi();
                bibRef.getEditor();
                bibRef.getIssue();
                bibRef.getPages();
                bibRef.getPublication();
                bibRef.getPublisher();
                bibRef.getTitle();
                bibRef.getVolume();
                bibRef.getYear();
            }
        }
    }

    private void checkColumnDefinition(String targetClassId,
                                             ColumnDefinition columnDefinition) {
        List<Column> columns = columnDefinition.getColumn();
        if (columns != null) {
            for (Column column : columns) {
                CvParamRef cvParamRef = column.getDataType();
                CvParam cvParam = cvParamRef.getCvParam();
                checkCvParam(targetClassId, cvParam);
            }
        }
    }

    private void checkContactRole(String tarClsId, ContactRole contactRole) {
        String ref = contactRole.getContactRef();
        msgs.addAll(checkObjectRef(tarClsId, ref, uk.ac.liv.jmzqml.model.mzqml.AbstractContact.class, this.unmarshaller));

        Role role = contactRole.getRole();
        CvParam cvParam = role.getCvParam();
        checkCvParam(tarClsId, cvParam);
    }

//    static public void checkCreationDate(Calendar creationDate1) {
//    }
    private void checkCvList(CvList cvList) {
        List<Cv> cvs = cvList.getCv();

        checkCvs(cvs);
    }

    private void checkCvParam(String tarClsId, CvParam cvParam) {
        String cvRef = cvParam.getCvRef();
        if (cvRef != null) {
            msgs.addAll(checkObjectRef(tarClsId, cvRef, uk.ac.liv.jmzqml.model.mzqml.Cv.class, this.unmarshaller));
        }

        String accession = cvParam.getAccession();
    }

    private void checkCvs(List<Cv> cvs) {
        if (cvs != null) {
            for (Cv cv : cvs) {
                cv.getFullName();
                cv.getId();
                cv.getUri();
                cv.getVersion();
            }
        }
    }

    private void checkDataMatrix(DataMatrix dataMatrix) {
        List<Row> rows = dataMatrix.getRow();
        if (rows != null) {
            for (Row row : rows) {
                checkRow(row);
            }
        }
    }

    private void checkDataProcessingList(
            DataProcessingList dataProcessingList) {
        List<DataProcessing> dataProcessings = dataProcessingList.getDataProcessing();

        checkDataProcessings(dataProcessings);
    }

    private void checkDataProcessings(List<DataProcessing> dataProcessings) {
        if (dataProcessings != null) {
            for (DataProcessing dataProcessing : dataProcessings) {
                String targetClassId = dataProcessing.getId();

                List<IdOnly> inputObjectRefs = dataProcessing.getInputObjects();
                List<IdOnly> outputObjectRefs = dataProcessing.getOutputObjects();

                InOutputObjectRefsRule inputRule = new InOutputObjectRefsRule(inputObjectRefs, targetClassId);
                inputRule.check();
                msgs.addAll(inputRule.getMsgs());

                InOutputObjectRefsRule outputRule = new InOutputObjectRefsRule(outputObjectRefs, targetClassId);
                outputRule.check();
                msgs.addAll(outputRule.getMsgs());

                String softwareRef = dataProcessing.getSoftwareRef();
                msgs.addAll(checkObjectRef(targetClassId, softwareRef, uk.ac.liv.jmzqml.model.mzqml.Software.class, this.unmarshaller));

                dataProcessing.getOrder();

                List<ProcessingMethod> processingMethods = dataProcessing.getProcessingMethod();
                checkProcessingMethods(targetClassId, processingMethods);

            }
        }
    }

    private void checkDBIdentificationRef(String tarClsId,
                                                DBIdentificationRef dBidRef) {
        String ref = dBidRef.getSearchDatabaseRef();
        checkObjectRef(tarClsId, ref, uk.ac.liv.jmzqml.model.mzqml.SearchDatabase.class, this.unmarshaller);
    }

    private void checkEvidenceRef(String tarClsId, EvidenceRef evidenceRef) {
        List<String> assayRefs = evidenceRef.getAssayRefs();
        if (assayRefs != null) {
            for (String assayRef : assayRefs) {
                String targetClassId = tarClsId;
                msgs.addAll(checkObjectRef(targetClassId, assayRef, uk.ac.liv.jmzqml.model.mzqml.Assay.class, this.unmarshaller));
            }
        }

        String ftRef = evidenceRef.getFeatureRef();
        String targetClassId = tarClsId;
        msgs.addAll(checkObjectRef(targetClassId, ftRef, uk.ac.liv.jmzqml.model.mzqml.Feature.class, this.unmarshaller));

        String idFileRef = evidenceRef.getIdentificationFileRef();
        msgs.addAll(checkObjectRef(targetClassId, idFileRef, uk.ac.liv.jmzqml.model.mzqml.IdentificationFile.class, this.unmarshaller));

        evidenceRef.getIdRefs();
    }

    /*
     * validate FeatureList
     */
    private void checkFeatureLists(Iterator<FeatureList> featureLists) {
        if (featureLists != null) {
            //for (FeatureList featureList : featureLists) {
            while (featureLists.hasNext()) {
                FeatureList featureList = featureLists.next();

                List<Feature> features = featureList.getFeature();
                checkFeatures(features);

                List<GlobalQuantLayer> featureQuantLayers = featureList.getFeatureQuantLayer();
                checkGlobalQuantLayers(featureQuantLayers);

                String id = featureList.getId();

                List<QuantLayer> ms2AssayQuantLayers = featureList.getMS2AssayQuantLayer();
                checkQuantLayers(ms2AssayQuantLayers);

                RatioQuantLayer ms2RatioQuantLayer = featureList.getMS2RatioQuantLayer();
                checkRatioQuantLayer(ms2RatioQuantLayer);

                List<QuantLayer> ms2StudyVariableQuantLayers = featureList.getMS2StudyVariableQuantLayer();
                checkQuantLayers(ms2StudyVariableQuantLayers);

                String targetClassId = id;

                List<AbstractParam> paramGroups = featureList.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                String rawFileGroupRef = featureList.getRawFilesGroupRef();
                msgs.addAll(checkObjectRef(targetClassId, rawFileGroupRef, uk.ac.liv.jmzqml.model.mzqml.RawFilesGroup.class, this.unmarshaller));
            }
        }
    }

    private void checkFeatures(List<Feature> features) {
        if (features != null) {
            for (Feature feature : features) {
                feature.getChromatogramRefs();
                feature.getSpectrumRefs();

                String targetClassId = feature.getId();
                List<AbstractParam> paramGroups = feature.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);
            }
        }
    }

    private void checkFileFormat(String tarClsId, FileFormat fileFormat) {
        if (fileFormat != null) {
            CvParam cvParam = fileFormat.getCvParam();
            checkCvParam(tarClsId, cvParam);
        }
    }

    private void checkGlobalQuantLayers(
            List<GlobalQuantLayer> globalQuantLayers) {
        if (globalQuantLayers != null) {
            for (GlobalQuantLayer globalQuantLayer : globalQuantLayers) {
                String id = globalQuantLayer.getId();

                ColumnDefinition columnDefinition = globalQuantLayer.getColumnDefinition();
                checkColumnDefinition(id, columnDefinition);

                DataMatrix dataMatrix = globalQuantLayer.getDataMatrix();
                checkDataMatrix(dataMatrix);

                ColIndRowValNumMatchRule colIndRowValNumMatchRule = new ColIndRowValNumMatchRule();
                colIndRowValNumMatchRule.check(globalQuantLayer);
                msgs.addAll(colIndRowValNumMatchRule.getMessage());
            }
        }
    }

    private void checkIdentificationFile(
            IdentificationFile identificationFile) {
        List<AbstractParam> paramGroups = identificationFile.getParamGroup();
        String targetClassId = identificationFile.getId();
        checkParamGroups(targetClassId, paramGroups);

        String searchDatabaseRef = identificationFile.getSearchDatabaseRef();
        checkObjectRef(targetClassId, searchDatabaseRef, uk.ac.liv.jmzqml.model.mzqml.SearchDatabase.class, this.unmarshaller);
    }

    private void checkIdentificationRefs(String tarClsId,
                                               List<IdentificationRef> identificationRefs) {
        if (identificationRefs != null) {
            for (IdentificationRef idRef : identificationRefs) {
                String idFileRef = idRef.getIdentificationFileRef(); 
                msgs.addAll(checkObjectRef(tarClsId, idFileRef, uk.ac.liv.jmzqml.model.mzqml.IdentificationFile.class, this.unmarshaller));
            }
        }
    }

    private void checkInputFiles(InputFiles inputFiles) {
        /*
         * RawFileRule start here
         */
        RawFileRule rfl = new RawFileRule(at, inputFiles);
        rfl.check();
        msgs.addAll(rfl.getMsgs());

        IdentificationFiles identificationFiles = inputFiles.getIdentificationFiles();
        if (identificationFiles != null) {
            List<IdentificationFile> identifications = identificationFiles.getIdentificationFile();
            if (identifications != null) {
                for (IdentificationFile identificationFile : identifications) {
                    checkIdentificationFile(identificationFile);
                }
            }
        }

        MethodFiles methodFiles = inputFiles.getMethodFiles();
        if (methodFiles != null) {
            List<MethodFile> methods = methodFiles.getMethodFile();
            if (methods != null) {
                for (MethodFile methodFile : methods) {
                    FileFormat format = methodFile.getFileFormat();
                    String targetClassId = methodFile.getId();
                    checkFileFormat(targetClassId, format);
                }
            }
        }

        List<RawFilesGroup> rawFilesGroups = inputFiles.getRawFilesGroup();
        checkRawFilesGroups(rawFilesGroups);

        List<SearchDatabase> searchDatabases = inputFiles.getSearchDatabase();
        checkSearchDatabases(searchDatabases);

        List<SourceFile> sourceFiles = inputFiles.getSourceFile();
        checkSourceFiles(sourceFiles);
    }

    private void checkModification(String tarClsId,
                                         Modification modification) {
        List<CvParam> cvParams = modification.getCvParam();
        if (cvParams != null) {
            for (CvParam cvParam : cvParams) {
                checkCvParam(tarClsId, cvParam);
            }
        }

//      There is no FeatureRefs under Modification in new schema  ///

//        List<Object> ftRefs = modification.getFeatureRefs();
//        if (!ftRefs.isEmpty()) {
//            for (Object ref : ftRefs) {
//                msgs.addAll(checkObjectRef(tarClsId, ref, uk.ac.liv.jmzqml.model.mzqml.Feature.class));
//            }
//        }

    }

    private <T> ArrayList<Message> checkObjectRef(String tarClsId,
                                                        String ref,
                                                        Class<T> cls,
                                                        MzQuantMLUnmarshaller um) {
        ObjectRefTypeMatchRule objectRefMatchRule = new ObjectRefTypeMatchRule(tarClsId, ref, cls, um);
        objectRefMatchRule.check();
        return objectRefMatchRule.getMessage();
    }

    private void checkOrganization(Organization organization) {
        ParentOrganization parentOrg = organization.getParentOrganization();
        String targetClassId = organization.getId();
        String parentOrgRef = parentOrg.getOrganizationRef();
        msgs.addAll(checkObjectRef(targetClassId, parentOrgRef, uk.ac.liv.jmzqml.model.mzqml.ParentOrganization.class, this.unmarshaller));
    }

    private void checkParamGroups(String tarClsId,
                                        List<AbstractParam> paramGroups) {
        for (AbstractParam param : paramGroups) {
            if (param.getClass().isInstance(uk.ac.liv.jmzqml.model.mzqml.CvParam.class)) {
                CvParam cv = (CvParam) param;
                cv.getAccession();
                String cvRef = cv.getCvRef();
                msgs.addAll(checkObjectRef(tarClsId, cvRef, uk.ac.liv.jmzqml.model.mzqml.Cv.class, this.unmarshaller));
            }
            else if (param.getClass().isInstance(uk.ac.liv.jmzqml.model.mzqml.UserParam.class)) {
                UserParam userParam = (UserParam) param;
                userParam.getType();
            }

        }
    }

    private void checkPeptideConsensusList(
            PeptideConsensusList peptideConsensusList) {
        if (peptideConsensusList != null) {
            List<QuantLayer> assayQuantLayers = peptideConsensusList.getAssayQuantLayer();
            List<GlobalQuantLayer> globalQuantLayers = peptideConsensusList.getGlobalQuantLayer();
            String id = peptideConsensusList.getId();
            List<AbstractParam> paramGroups = peptideConsensusList.getParamGroup();
            List<PeptideConsensus> peptideConsensuses = peptideConsensusList.getPeptideConsensus();
            RatioQuantLayer ratioQuantLayer = peptideConsensusList.getRatioQuantLayer();
            List<QuantLayer> studyVariableQuantLayers = peptideConsensusList.getStudyVariableQuantLayer();

            checkQuantLayers(assayQuantLayers);
            checkGlobalQuantLayers(globalQuantLayers);
            String targetClassId = id;
            checkParamGroups(targetClassId, paramGroups);
            checkPeptideConsensuses(peptideConsensuses);
            checkRatioQuantLayer(ratioQuantLayer);
            checkQuantLayers(studyVariableQuantLayers);
        }
    }

    private void checkPeptideConsensus(PeptideConsensus peptideConsensus) {
        if (peptideConsensus != null) {
            List<String> charges = peptideConsensus.getCharge();

            String id = peptideConsensus.getId();
            String targetClassId = id;
            List<Modification> modifications = peptideConsensus.getModification();
            if (modifications != null) {
                for (Modification modification : modifications) {
                    checkModification(targetClassId, modification);
                }
            }

            List<AbstractParam> paramGroups = peptideConsensus.getParamGroup();
            checkParamGroups(targetClassId, paramGroups);

            String sequence = peptideConsensus.getPeptideSequence();

            String searchDBRef = peptideConsensus.getSearchDatabaseRef();
            msgs.addAll(checkObjectRef(targetClassId, searchDBRef, uk.ac.liv.jmzqml.model.mzqml.SearchDatabase.class, this.unmarshaller));

            List<EvidenceRef> evidenceRefs = peptideConsensus.getEvidenceRef();
            if (evidenceRefs != null) {
                for (EvidenceRef evidenceRef : evidenceRefs) {
                    checkEvidenceRef(targetClassId, evidenceRef);

                    //check EvidenceRefRule
                    EvidenceRefRule eRefRule = new EvidenceRefRule(peptideConsensus, evidenceRef);
                    eRefRule.check();
                    msgs.addAll(eRefRule.getMsgs());
                }
            }
        }
    }

    private void checkPeptideConsensuses(
            List<PeptideConsensus> peptideConsensuses) {
        if (peptideConsensuses != null) {
            for (PeptideConsensus peptideConsensus : peptideConsensuses) {
                checkPeptideConsensus(peptideConsensus);
            }
        }
    }

    /*
     * validate PeptideConsensusList
     */
    private void checkPeptideConsensusLists(
            Iterator<PeptideConsensusList> peptideConsensusLists) {
        if (peptideConsensusLists != null) {
            /*
             * FinalResultRule start here
             */
            FinalResultRule frr = new FinalResultRule(peptideConsensusLists);
            frr.check();
            msgs.addAll(frr.getMsgs());

            //for (PeptideConsensusList peptideConsensusList : peptideConsensusLists) {
            while (peptideConsensusLists.hasNext()) {
                PeptideConsensusList peptideConsensusList = peptideConsensusLists.next();
                checkPeptideConsensusList(peptideConsensusList);
            }
        }

    }

    private void checkPerson(Person person) {
        List<Affiliation> affiliations = person.getAffiliation();
        String id = person.getId();
        String targetClassId = id;
        if (affiliations != null) {
            for (Affiliation affiliation : affiliations) {
                String orgRef = affiliation.getOrganizationRef();
                msgs.addAll(checkObjectRef(targetClassId, orgRef, uk.ac.liv.jmzqml.model.mzqml.Organization.class, this.unmarshaller));
            }
        }
    }

    private void checkPersonOrOrganizations(
            List<AbstractContact> personOrOrganizations) {
        if (personOrOrganizations != null) {
            for (AbstractContact personOrOrganization : personOrOrganizations) {
                String targetClassId = personOrOrganization.getId();
                List<AbstractParam> paramGroups = personOrOrganization.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                if (personOrOrganization.getClass().isInstance(uk.ac.liv.jmzqml.model.mzqml.Person.class)) {
                    Person person = (Person) personOrOrganization;
                    checkPerson(person);
                }

                if (personOrOrganization.getClass().isInstance(uk.ac.liv.jmzqml.model.mzqml.Organization.class)) {
                    Organization org = (Organization) personOrOrganization;
                    checkOrganization(org);
                }

            }
        }
    }

    private void checkProcessingMethods(String tarClsId,
                                              List<ProcessingMethod> processingMethods) {
        if (processingMethods != null) {
            for (ProcessingMethod processingMethod : processingMethods) {
                processingMethod.getOrder();

                List<AbstractParam> paramGroups = processingMethod.getParamGroup();
                checkParamGroups(tarClsId, paramGroups);
            }
        }
    }

    private void checkProtein(Protein protein) {
        String id = protein.getId();
        String targetClassId = id;
        List<IdentificationRef> identificationRefs = protein.getIdentificationRef();
        checkIdentificationRefs(targetClassId, identificationRefs);

        List<AbstractParam> paramGroups = protein.getParamGroup();
        checkParamGroups(targetClassId, paramGroups);

        List<String> pepRefs = protein.getPeptideConsensusRefs();
        if (pepRefs != null) {
            for (String ref : pepRefs) {
                msgs.addAll(checkObjectRef(targetClassId, ref, uk.ac.liv.jmzqml.model.mzqml.PeptideConsensus.class, this.unmarshaller));
            }
        }

        String searchDBRef = protein.getSearchDatabaseRef();
        msgs.addAll(checkObjectRef(targetClassId, searchDBRef, uk.ac.liv.jmzqml.model.mzqml.SearchDatabase.class, this.unmarshaller));
    }

    private void checkProteinGroup(ProteinGroup proteinGroup) {
        String id = proteinGroup.getId();
        String targetClassId = id;

        List<IdentificationRef> identificationRefs = proteinGroup.getIdentificationRef();
        checkIdentificationRefs(targetClassId, identificationRefs);

        List<AbstractParam> paramGroups = proteinGroup.getParamGroup();
        checkParamGroups(targetClassId, paramGroups);

        //TODO: re-coded in consistent manner
//        List<ProteinRef> proteinRefs = proteinGroup.getProteinRef();
//        if (proteinRefs != null) {
//            for (ProteinRef proteinRef : proteinRefs) {
//                Object protRef = proteinRef.getProteinRef();
//                msgs.addAll(checkObjectRef(targetClassId, protRef, uk.ac.liv.jmzqml.model.mzqml.Protein.class, this.unmarshaller));
//            }
//        }

        String searchDBRef = proteinGroup.getSearchDatabaseRef();
        msgs.addAll(checkObjectRef(targetClassId, searchDBRef, uk.ac.liv.jmzqml.model.mzqml.SearchDatabase.class, this.unmarshaller));
    }

    private void checkProteinGroupList(ProteinGroupList proteinGroupList) {
        if (proteinGroupList != null) {
            List<QuantLayer> assayQuantLayers = proteinGroupList.getAssayQuantLayer();
            List<GlobalQuantLayer> globalQuantLayers = proteinGroupList.getGlobalQuantLayer();
            String id = proteinGroupList.getId();
            List<AbstractParam> paramGroups = proteinGroupList.getParamGroup();
            List<ProteinGroup> proteinGroups = proteinGroupList.getProteinGroup();
            RatioQuantLayer ratioQuantLayer = proteinGroupList.getRatioQuantLayer();
            List<QuantLayer> studyVariableQuantLayers = proteinGroupList.getStudyVariableQuantLayer();

            checkQuantLayers(assayQuantLayers);
            checkGlobalQuantLayers(globalQuantLayers);
            String targetClassId = id;
            checkParamGroups(targetClassId, paramGroups);
            checkProteinGroups(proteinGroups);
            checkRatioQuantLayer(ratioQuantLayer);
            checkQuantLayers(studyVariableQuantLayers);
        }
    }

    private void checkProteinGroups(List<ProteinGroup> proteinGroups) {
        if (proteinGroups != null) {
            for (ProteinGroup proteinGroup : proteinGroups) {
                checkProteinGroup(proteinGroup);
            }
        }
    }

    /*
     * validate ProteinList
     */
    private void checkProteinList(ProteinList proteinList) {
        if (proteinList != null) {
            List<QuantLayer> assayQuantLayers = proteinList.getAssayQuantLayer();
            List<GlobalQuantLayer> globalQuantLayers = proteinList.getGlobalQuantLayer();
            String id = proteinList.getId();
            List<AbstractParam> paramGroups = proteinList.getParamGroup();
            List<Protein> proteins = proteinList.getProtein();
            RatioQuantLayer ratioQuantLayer = proteinList.getRatioQuantLayer();
            List<QuantLayer> studyVariableQuantLayers = proteinList.getStudyVariableQuantLayer();

            checkQuantLayers(assayQuantLayers);
            checkGlobalQuantLayers(globalQuantLayers);
            String targetClassId = id;
            checkParamGroups(targetClassId, paramGroups);
            checkProteins(proteins);
            checkRatioQuantLayer(ratioQuantLayer);
            checkQuantLayers(studyVariableQuantLayers);
        }
    }

    private void checkProteins(List<Protein> proteins) {
        if (proteins != null) {
            for (Protein protein : proteins) {
                checkProtein(protein);
            }
        }
    }

    private void checkProvider(Provider provider) {
        String analysisSoftwareRef = provider.getSoftwareRef();
        String targetClassId = provider.getId();
        msgs.addAll(checkObjectRef(targetClassId, analysisSoftwareRef, uk.ac.liv.jmzqml.model.mzqml.Software.class, this.unmarshaller));

        ContactRole contactRole = provider.getContactRole();
        checkContactRole(targetClassId, contactRole);

        String name = provider.getName();
    }

    private void checkQuantLayers(List<QuantLayer> quantLayers) {
        if (quantLayers != null) {
            for (QuantLayer quantLayer : quantLayers) {

                checkQuantLayer(quantLayer);
            }
        }
    }

    private void checkQuantLayer(QuantLayer quantLayer) {

        if (quantLayer != null) {
            //TODO: need to figure out what  does columnIndex refer to 
            List<String> columnIndex = quantLayer.getColumnIndex();

            String id = quantLayer.getId();
            String targetClassId = id;

            DataMatrix dataMatrix = quantLayer.getDataMatrix();
            checkDataMatrix(dataMatrix);

            CvParamRef data = quantLayer.getDataType();
            CvParam cvParam = data.getCvParam();
            checkCvParam(targetClassId, cvParam);

            ColIndRowValNumMatchRule colIndRowValNumMatchRule = new ColIndRowValNumMatchRule();
            colIndRowValNumMatchRule.check(quantLayer);
            msgs.addAll(colIndRowValNumMatchRule.getMessage());
        }
    }

    /*
     * Validate RatioList
     */
    private void checkRatio(Ratio ratio) {
        String id = ratio.getId();
        String targetClassId = id;
        ParamList ratioCalParamList = ratio.getRatioCalculation();
        if (ratioCalParamList != null) {
            List<AbstractParam> paramGroups = ratioCalParamList.getParamGroup();
            checkParamGroups(targetClassId, paramGroups);
        }
    }

    private void checkRatioList(RatioList ratioList) {
        if (ratioList != null) {
            List<Ratio> ratios = ratioList.getRatio();
            checkRatios(ratios);

            NumeratorDenominatorRule numeratorDenominatorRule = new NumeratorDenominatorRule(ratioList);
            numeratorDenominatorRule.check();
            msgs.addAll(numeratorDenominatorRule.getMsgs());
        }
    }

    private void checkRatioQuantLayer(RatioQuantLayer ratioQuantLayer) {

        if (ratioQuantLayer != null) {
            //TODO: need to figure out what  does columnIndex refer to 
            List<String> columnIndex = ratioQuantLayer.getColumnIndex();

            String id = ratioQuantLayer.getId();
            String targetClassId = id;

            DataMatrix dataMatrix = ratioQuantLayer.getDataMatrix();
            checkDataMatrix(dataMatrix);

            ColIndRowValNumMatchRule colIndRowValNumMatchRule = new ColIndRowValNumMatchRule();
            colIndRowValNumMatchRule.check(ratioQuantLayer);
            msgs.addAll(colIndRowValNumMatchRule.getMessage());
        }
    }

    private void checkRatios(List<Ratio> ratios) {
        if (ratios != null) {
            for (Ratio ratio : ratios) {
                checkRatio(ratio);
            }
        }
    }

    private void checkRawFiles(List<RawFile> rawFiles) {
        if (rawFiles != null) {
            for (RawFile rawFile : rawFiles) {
                String id = rawFile.getId();
                String targetClassId = id;

                String methodFileRef = rawFile.getMethodFileRef();
                msgs.addAll(checkObjectRef(targetClassId, methodFileRef, uk.ac.liv.jmzqml.model.mzqml.MethodFile.class, this.unmarshaller));

                List<AbstractParam> paramGroups = rawFile.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                FileFormat format = rawFile.getFileFormat();
                checkFileFormat(targetClassId, format);
            }
        }
    }

    private void checkRawFilesGroups(List<RawFilesGroup> rawFilesGroups) {
        if (rawFilesGroups != null) {
            for (RawFilesGroup rawFilesGroup : rawFilesGroups) {
                String targetClassId = rawFilesGroup.getId();
                List<AbstractParam> paramGroups = rawFilesGroup.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                List<RawFile> rawFiles = rawFilesGroup.getRawFile();
                checkRawFiles(rawFiles);
            }
        }
    }

    private void checkRow(Row row) {
        row.getObjectRef();
    }

    private void checkSearchDatabases(List<SearchDatabase> searchDatabases) {
        if (searchDatabases != null) {
            for (SearchDatabase searchDatabase : searchDatabases) {
                List<CvParam> cvParams = searchDatabase.getCvParam();
                String targetClassId = searchDatabase.getId();
                if (cvParams != null) {
                    for (CvParam cvParam : cvParams) {
                        checkCvParam(targetClassId, cvParam);
                    }
                }

                //TODO: to check the following commented code
//                Param databaseName = searchDatabase.getDatabaseName();
//                if (databaseName.getCvParam() != null) {
//                    checkCvParam(targetClassId, databaseName.getCvParam());
//                }

                FileFormat format = searchDatabase.getFileFormat();
                checkFileFormat(targetClassId, format);

            }
        }
    }

    private void checkSmallMolecule(SmallMolecule smallMolecule) {
        String id = smallMolecule.getId();
        String targetClassId = id;

        List<DBIdentificationRef> dBidentificationRefs = smallMolecule.getDBIdentificationRef();
        if (dBidentificationRefs != null) {
            for (DBIdentificationRef dBidRef : dBidentificationRefs) {
                checkDBIdentificationRef(targetClassId, dBidRef);
            }
        }

        List<String> ftRefs = smallMolecule.getFeatureRefs();
        if (ftRefs != null) {
            for (String ref : ftRefs) {
                msgs.addAll(checkObjectRef(targetClassId, ref, uk.ac.liv.jmzqml.model.mzqml.Feature.class, this.unmarshaller));
            }
        }

        List<AbstractParam> paramGroups = smallMolecule.getParamGroup();
        checkParamGroups(targetClassId, paramGroups);

        List<SmallMolModification> smallMolModifications = smallMolecule.getModification();
        checkSmallMolModifications(targetClassId, smallMolModifications);

    }

    private void checkSmallMolModifications(String tarClsId,
                                                  List<SmallMolModification> smallMolModifications) {
        if (smallMolModifications != null) {
            for (SmallMolModification smallMolMod : smallMolModifications) {
                List<CvParam> cvParams = smallMolMod.getCvParam();
                if (cvParams != null) {
                    for (CvParam cvParam : cvParams) {
                        checkCvParam(tarClsId, cvParam);
                    }
                }
            }
        }
    }

    private void checkSmallMolecules(List<SmallMolecule> smallMolecules) {
        if (smallMolecules != null) {
            for (SmallMolecule smallMol : smallMolecules) {
                checkSmallMolecule(smallMol);
            }
        }
    }

    private void checkSmallMoleculeList(
            SmallMoleculeList smallMoleculeList) {
        List<QuantLayer> assayQuantLayers = smallMoleculeList.getAssayQuantLayer();
        List<GlobalQuantLayer> globalQuantLayers = smallMoleculeList.getGlobalQuantLayer();
        String id = smallMoleculeList.getId();
        List<AbstractParam> paramGroups = smallMoleculeList.getParamGroup();
        RatioQuantLayer ratioQuantLayer = smallMoleculeList.getRatioQuantLayer();
        List<SmallMolecule> smallMolecules = smallMoleculeList.getSmallMolecule();
        List<QuantLayer> studyVariableQuantLayers = smallMoleculeList.getStudyVariableQuantLayer();

        checkQuantLayers(assayQuantLayers);
        checkGlobalQuantLayers(globalQuantLayers);
        String targetClassId = id;
        checkParamGroups(targetClassId, paramGroups);
        checkSmallMolecules(smallMolecules);
        checkRatioQuantLayer(ratioQuantLayer);
        checkQuantLayers(studyVariableQuantLayers);
    }

    private void checkSoftwares(List<Software> softwares) {
        if (softwares != null) {
            for (Software software : softwares) {
                String id = software.getId();
                String targetClassId = id;

                List<AbstractParam> paramGroups = software.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);
            }
        }
    }

    private void checkSoftwareList(SoftwareList softwareList) {
        if (softwareList != null) {
            List<Software> softwares = softwareList.getSoftware();
            checkSoftwares(softwares);
        }
    }

    private void checkSourceFiles(List<SourceFile> sourceFiles) {
        if (sourceFiles != null) {
            for (SourceFile sourceFile : sourceFiles) {
                String targetClassId = sourceFile.getId();
                FileFormat format = sourceFile.getFileFormat();
                checkFileFormat(targetClassId, format);
            }
        }
    }

    private void checkStudyVariableList(
            StudyVariableList studyVariableList) {
        if (studyVariableList != null) {
            List<StudyVariable> studyVariables = studyVariableList.getStudyVariable();
            checkStudyVariables(studyVariables);
        }
    }

    private void checkStudyVariables(List<StudyVariable> studyVariables) {
        if (studyVariables != null) {
            for (StudyVariable studyVariable : studyVariables) {
                String id = studyVariable.getId();
                String targetClassId = id;

                List<String> assayRefs = studyVariable.getAssayRefs();
                if (assayRefs != null) {
                    for (String assayRef : assayRefs) {
                        msgs.addAll(checkObjectRef(targetClassId, assayRef, uk.ac.liv.jmzqml.model.mzqml.Assay.class, this.unmarshaller));
                    }
                }

                List<AbstractParam> paramGroups = studyVariable.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);
            }
        }
    }

    private void checkVersion(String version) {
    }

    /*
     * protected methods
     */
    protected InputStream getOntologiesFileInputStream()
            throws FileNotFoundException {

        File file = new File(getClass().getClassLoader().getResource("ontologies.xml").getFile());
        if (!file.exists()) {
            ClassLoader cl = this.getClass().getClassLoader();
            return cl.getResourceAsStream("ontologies.xml");
        }
        return new FileInputStream(file);
    }

    protected InputStream getGeneralMappingRuleInputStream()
            throws FileNotFoundException {
        String mappingRuleFile = getClass().getClassLoader().
                getResource("mzQuantML-mapping_1.0.0-rc3-general.xml").getFile();
        File file = new File(mappingRuleFile);
        if (!file.exists()) {
            ClassLoader cl = this.getClass().getClassLoader();
            return cl.getResourceAsStream("mzQuantML-mapping_1.0.0-rc3-general.xml");
        }
        return new FileInputStream(file);
    }

    /*
     * private methods
     */
    private void getAnalysisSummaryMap(AnalysisSummary analysisSummary) {
        analysisSummaryMap = new EnumMap<AnalysisSummaryElement, Boolean>(AnalysisSummaryElement.class);

        List<AbstractParam> paramGroups = analysisSummary.getParamGroup();
        for (AbstractParam param : paramGroups) {
            if (param instanceof CvParam) {
                CvParam cv = (CvParam) param;
                String name = cv.getName();
                String value = cv.getValue();
                AnalysisSummaryElement key = AnalysisSummaryElement.getType(name);
                if (key != null) {
                    analysisSummaryMap.put(key, Boolean.valueOf(value));
                }
            }
        }

    }

}
