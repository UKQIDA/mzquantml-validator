/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.cvmapping;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.validator.*;
import psidev.psi.tools.validator.rules.cvmapping.CvRule;
import uk.ac.liv.jmzqml.MzQuantMLElement;
import uk.ac.liv.jmzqml.model.MzQuantMLObject;
import uk.ac.liv.jmzqml.xml.io.MzQuantMLUnmarshaller;

/**
 *
 * @author Da Qi @institute University of Liverpool @time 06-Sep-2012 11:53:19
 */
public class CvValidator extends Validator {

    private static final Logger logger = Logger.getLogger(CvValidator.class);
    private MessageLevel msgL = MessageLevel.WARN;
    private HashMap<String, List<ValidatorMessage>> msgs = null;
    private MzQuantMLUnmarshaller unmarshaller = null;
    private long uniqId = 0;

    public CvValidator(InputStream ontoConfig,
            InputStream cvRuleConfig,
            InputStream objectRuleConfig) throws ValidatorException, OntologyLoaderException {
        super(ontoConfig, cvRuleConfig, objectRuleConfig);
        validatorInit();
    }

    public CvValidator(InputStream ontoConfig,
            InputStream cvRuleConfig) throws ValidatorException, OntologyLoaderException {
        super(ontoConfig, cvRuleConfig);
        validatorInit();
    }

    public CvValidator(InputStream ontoConfig) throws OntologyLoaderException {
        super(ontoConfig);
    }

    private void validatorInit() throws ValidatorException {

        // Initialized the messages
        msgs = new HashMap<String, List<ValidatorMessage>>();
    }

    public void setMessageReportLevel(MessageLevel level) {
        this.msgL = level;
    }

    public MessageLevel getMessageReportLevel() {
        return this.msgL;
    }

    /**
     * Performs the actual validation, including schema validation (if not
     * turned off), validation against the CV-mapping rules and validation
     * against the registered ObjectRules.
     *
     * @param xmlFile the PRIDE XML file to validate.
     * @return a Collection of ValidatorMessages documenting the validation
     * result.
     */
    public Collection<ValidatorMessage> startValidation(File xmlFile) {
        if (logger.isInfoEnabled()) {
            logger.info("\nStarting new Cv validation, input file: " + xmlFile.getAbsolutePath());
        }

        super.resetCvRuleStatus();

        try {
            this.unmarshaller = new MzQuantMLUnmarshaller(xmlFile);

            addMessages(this.checkCvMappingRules(), this.msgL);
            // See if the mapping makes sense.
            if (!this.msgs.isEmpty()) {
                System.err.println("\n\nThere were errors procossing the Cv mapping configuration file:\n");
                for (ValidatorMessage lMessage : getMessageCollection()) {
                    System.err.println("\t - " + lMessage);
                }
            }
            System.out.println("number of valid rules: " + this.getCvRuleManager().getCvRules().size());

            // Cv mapping rules.
            applyCvMappingRules();

        } catch (ValidatorException ve) {
            logger.error("Exceptions during validation!", ve);
            ve.printStackTrace();
        }

        //check for terms that were not anticipated with the rules in the Cv mapping file
        for (String xpath : ValidatorCvContext.getInstance().getNotRecognisedXpath()) {

            Set<String> list = ValidatorCvContext.getInstance().getNotRecognisedTerms(xpath);
            if (list != null && !list.isEmpty()) {
                System.out.println("unrecognised terms for xpath '" + xpath + "' : " + list);
            }
        }

        return this.getMessageCollection();
    }

    private void addMessages(Collection<ValidatorMessage> aNewMessages, MessageLevel aLevel) {
        for (ValidatorMessage aNewMessage : aNewMessages) {
            if (aNewMessage.getLevel().isHigher(aLevel) || aNewMessage.getLevel().isSame(aLevel)) {
                if (aNewMessage.getRule() != null) {
                    addValidatorMessage(aNewMessage.getRule().getId(), aNewMessage, this.msgL);
                } else {
                    addValidatorMessage("unknown", aNewMessage, this.msgL);
                }
            }
        }
    }

    private void addValidatorMessage(String ruleId, ValidatorMessage validatorMessage, MessageLevel msgLevel) {
        if (validatorMessage.getLevel().isHigher(msgLevel) || validatorMessage.getLevel().isSame(msgLevel)) {
            if (this.msgs.containsKey(ruleId)) {
                this.msgs.get(ruleId).add(validatorMessage);
            } else {
                List<ValidatorMessage> list = new ArrayList<ValidatorMessage>();
                list.add(validatorMessage);
                this.msgs.put(ruleId, list);
            }
        }
    }

    private void applyCvMappingRules() throws ValidatorException {
        logger.debug("Validating against the Cv mapping Rules...");

        checkElementCvMapping(MzQuantMLElement.AssayList);

        checkElementCvMapping(MzQuantMLElement.AuditCollection);

        checkElementCvMapping(MzQuantMLElement.DataProcessingList);

        checkElementCvMapping(MzQuantMLElement.SoftwareList);

        checkElementCvMapping(MzQuantMLElement.FeatureList);

        checkElementCvMapping(MzQuantMLElement.SmallMoleculeList);

        checkElementCvMapping(MzQuantMLElement.PeptideConsensusList);

        checkElementCvMapping(MzQuantMLElement.Provider);

        checkElementCvMapping(MzQuantMLElement.InputFiles);

        checkElementCvMapping(MzQuantMLElement.StudyVariableList);

        checkElementCvMapping(MzQuantMLElement.AnalysisSummary);

        checkElementCvMapping(MzQuantMLElement.RatioList);

        checkElementCvMapping(MzQuantMLElement.ProteinGroupList);

        checkElementCvMapping(MzQuantMLElement.ProteinList);
    }

    public Collection<ValidatorMessage> validate(InputStream aInputStream) throws ValidatorException {

        File tempFile;
        try {
            tempFile = storeAsTemporaryFile(aInputStream);
            return this.startValidation(tempFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidatorException("Unable to process input stream", e);
        }
    }

    /**
     * Store the content of the given input stream into a temporary file and
     * return its descriptor.
     *
     * @param is the input stream to store.
     * @return a File descriptor describing a temporary file storing the content
     * of the given input stream.
     * @throws IOException if an IO error occur.
     */
    private File storeAsTemporaryFile(InputStream is) throws IOException {

        if (is == null) {
            throw new IllegalArgumentException("You must give a non null InputStream");
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        // Create a temp file and write URL content in it.
        File tempDirectory = new File(System.getProperty("java.io.tmpdir", "tmp"));
        if (!tempDirectory.exists()) {
            if (!tempDirectory.mkdirs()) {
                throw new IOException("Cannot create temp directory: "
                        + tempDirectory.getAbsolutePath());
            }
        }

        long id = getUniqueId();

        File tempFile = File.createTempFile("validator." + id, ".xml", tempDirectory);

        log.info("The file is temporary store as: " + tempFile.getAbsolutePath());

        BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));

        String line;
        while ((line = in.readLine()) != null) {
            out.write(line);
        }

        in.close();

        out.flush();
        out.close();

        return tempFile;
    }

    /**
     * Return a unique ID.
     *
     * @return a unique id.
     */
    synchronized private long getUniqueId() {
        return ++uniqId;
    }

    private void checkElementCvMapping(MzQuantMLElement element) throws ValidatorException {

        Iterator<MzQuantMLObject> mzqIter;
        mzqIter = this.unmarshaller.unmarshalCollectionFromXpath(element);
        Collection toValidate = new ArrayList();
        while (mzqIter.hasNext()) {
            final Object next = mzqIter.next();
            toValidate.add(next);
        }

        final Collection<ValidatorMessage> cvMappingResult = this.checkCvMapping(toValidate, element.getXpath());
        addMessages(cvMappingResult, this.msgL);
    }

    @Override
    public Collection<ValidatorMessage> checkCvMapping(Collection<?> collection, String xPath) throws ValidatorException {
        Collection messages = new ArrayList();

        if (this.getCvRuleManager() != null) {
            for (CvRule rule : this.getCvRuleManager().getCvRules()) {
                for (Object o : collection) {
                    if (rule.canCheck(xPath)) {
                        final Collection<ValidatorMessage> resultCheck = rule.check(o, xPath);
                        messages.addAll(resultCheck);
                    }
                }
            }
        } else {
            logger.error("The CvRuleManager has not been set up yet.");
        }
        return messages;
    }

    private Collection<ValidatorMessage> getMessageCollection() {
        Collection<ValidatorMessage> ret = new HashSet<ValidatorMessage>();
        for (String key : this.msgs.keySet()) {
            final List<ValidatorMessage> list = this.msgs.get(key);
            if (list != null) {
                ret.addAll(list);
            }
        }
        return ret;
    }
}
