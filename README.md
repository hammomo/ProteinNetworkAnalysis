# Network Analysis

1. Download [Java SE 13.0.1](https://www.oracle.com/technetwork/java/javase/downloads/index.html) as the principal platform to run the project.
![Java SE 13.0.1](./pics/jdk13.png)

2. Download [JavaFX 13.0.1](https://gluonhq.com/products/javafx/).
![JavaFX 13.0.1](./pics/javafx.png)

3. **Eclipse** could be used to open the archive file (*NetworkAnalysis.zip*), but before that, several steps need to be done firstly.
   * Open the preferences settings of Eclipse (hotkey: “CMD + ,” on macOS).

   * Locate “Java -> Build Path -> User Libraries” and click New… to add new user-defined libraries, name it as “javafx” as followed:

   ![setting 1](./pics/setting1.png)

   * After creating the user-defined library, click “Add External JARs…”.

   ![setting 2](./pics/setting2.png)

   * From the local path of the downloaded JavaFX sdk, import all the *.jar files.

   ![setting 3](./pics/setting3.png)

   * The imported packages are displayed like the following, and click “Apply and Close”:

   ![setting 4](./pics/setting4.png)

4. Follow the next few steps to import the archive (.zip) file as a project into Eclipse.
   * “File -> Import…”

   ![importing 1](./pics/importing1.png)

   * Choose “Existing Projects into Workspace”

   ![importing 2](./pics/importing2.png)

   * Select archive file and check if the project exists, leave other parameters as default and “Finish”.

   ![importing 3](./pics/importing3.png)

   * Right click “NetworkAnalysis” and select “Run Configurations…” to create a profile for “Java Application” (just double click) and name it as “NetworkAnalysis”.

   ![importing 4](./pics/importing4.png)
   ![importing 5](./pics/importing5.png)

   * Click “Search…” to select the Main class of the project and select “Main – home”.

   ![importing 6](./pics/importing6.png)
   ![importing 7](./pics/importing7.png)

   * Go to “Arguments” tab and make sure to uncheck “Use the -XstartOnFirstThread argument when launching with SWT”.

   ![importing 8](./pics/importing8.png)

5. Finally, run the project. The Input file “PPInetwork.txt” is under the root folder, and another "PPInetwork_copy.txt" is a modified version, deleting some connections, to view the differences.

***Hints:*** The following extension (*e(fx)clipse 3.6.0*) could be helpful to have a better view of .fxml and .css files.
![e(fx)clipse](./pics/efxclipse.png)
