To run the test suite locally, we can use the following command in our terminal or setup a run configuration in our IDE:
```bash mvn clean test -Dtest=TestRunner```
To run in headless mode, we can use:
```bash mvn clean test -Dtest=TestRunner -Dheadless=true```
We can also specify specific tags to run:
```bash mvn clean test -Dtest=TestRunner -Dcucumber.options="--tags @yourTag"```
To run the Karate tests, we can use:
```bash mvn clean test -Dtest=KarateTestRunner```


To configure the Jenkins pipeline, we need to set up it up with the following:
1. Navigate to our Jenkins dashboard and click New Item.
2. Enter a name (e.g., Selenium-Cucumber-UI-Suite) and select Pipeline, then click OK.
3. Scroll down to the Pipeline configuration section.
4. Change the Definition dropdown from Pipeline script to Pipeline script from SCM.
5. Select Git as our Source Control Management system.
6. Provide our Repository URL and set up the access credentials (SSH Key or GitHub Personal Access Token).
7. Specify our branch (e.g., */master or */main).
8. Ensure the Script Path says Jenkinsfile.
9. Click Apply and Save.
10. To trigger the pipeline, click Build Now. This will execute the Jenkinsfile, which contains the 
steps to run our test suite. We can monitor the progress and view the results in the Jenkins console output.

The database configuration is set up in the `src/test/resources/config.properties` file. 
We can specify our database connection details such as URL, username, and password. 
This allows our tests to interact with the database for any necessary setup or verification steps.
The current example is generic PostgreSQL configuration unrelated to any specific database, or this test suite,
but it can be modified to fit any database type by changing the connection URL and driver settings accordingly.
It is just a placeholder to demonstrate one way I would configure a database connection for this demo.


