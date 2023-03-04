update-version:
	mvn -N versions:update-child-modules
	@echo "Update success."
package:
	mvn clean package -D maven.javadoc.skip=false
install:
	mvn clean install -DskipTests
deploy:
	mvn clean deploy -P release -Darguments=xwc1125
dependency-check:
	mvn dependency-check:aggregate -Dmaven.test.skip=true
