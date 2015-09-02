# mzQuantML validator #

---

## General ##

The validator can check whether an mzQuantML file (.mzq) is schema valid, semantically valid and contains valid CV terms. Schema validation checks the correctness of file syntax against the XSD and object references. Semantic validation checks whether an mzQuantML file follows the semantic rules in the rule files, which include the general file and four specific files for the four supported techniques. The validator also checks the CV term used in an mzQuantML file against the CV mapping rule file. Each misuse of CV terms will generate an “INFO”, “WARN”, or “ERROR” message depending on the level of CV requirement s in the rule file.


---

## Download ##

The current version of validator is 1.0 (build 1.0.2) and can be downloaded from [here](https://mzquantml-validator.googlecode.com/svn/trunk/src/release/mzQuantML-validator-v1.0.zip)

## Test files ##

We produced all possible bad files from syntactical errors to semantic rule violations. They are in http://code.google.com/p/mzquantml-validator/source/browse/#svn/trunk/src/test/resources. The header of each file contains information about expected specific error. You can test them yourself using the latest validator and see how validator reports these errors.