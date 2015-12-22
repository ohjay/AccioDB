SPARK_DIR=~/spark-1.5.0
PROMPT='Please enter the path to your Spark directory: '

default:
    @echo "Error: make must be called with some target entry!"
    @echo "For example, \"make launch\""
	
# ===============================
# === DOES NOT INCLUDE PROMPT ===
# ===============================

compile: # Compiles AccioDB and doesn't do anything else
    @sbt package

launch: # Launches AccioDB (doesn't re-compile!)
    @$(SPARK_DIR)/bin/spark-submit --class "AccioUI" --master local[4] target/scala-2.10/acciodb_2.10-1.0.0.jar

cl: # Compiles AND launches AccioDB
    @sbt package
    @$(SPARK_DIR)/bin/spark-submit --class "AccioUI" --master local[4] target/scala-2.10/acciodb_2.10-1.0.0.jar
	
# ===========================
# === DOES INCLUDE PROMPT ===
# ===========================

qlaunch: # Launches AccioDB after prompting the user for the Spark directory
    @read -p $(PROMPT) path; \
    if [ -d $$path ]; \
    then \
        $${path}/bin/spark-submit --class "AccioUI" --master local[4] target/scala-2.10/acciodb_2.10-1.0.0.jar; \
    else \
        echo ""; \
        echo "Error: not a directory!"; \
        echo "Launch failed."; \
    fi \

qcl: compile qlaunch
