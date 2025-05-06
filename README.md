# 336project
Group 15 336 Project

# Members
- Tejas Kandri
- Shivam Patel
- Arsal Shaikh
- Aanand Aggarwal

# Dependencies & Versions

This project uses 
1. Tomcat V11.0.4 or just Tomcat V11 within Eclipse
2. Java 17 (Change in project facets settings)
3. Dynamic Web Module 6.1 (Change in project facets settings)
4. mysql-connector-j-9.3.9.jar (download this online and drag and drop in lib folder) (IMPORTANT! Make sure to add this to the build path) (https://dev.mysql.com/downloads/connector/j/)
5. jakarta.servlet-api-5.0.0-javadoc.jar (download this online and drag and drop in lib folder) (IMPORTANT! Make sure to add this to the build path) (https://repo1.maven.org/maven2/jakarta/servlet/jakarta.servlet-api/5.0.0/)

The above are the project specifications that are required to run this properly.

# Structure

1. This project is made with Eclipse's Dynamic Web Module Option which does the heavy lifting for us
2. .jsp files can be appended within the webapp directory as needed (no need to put them in META-INF or WEB-INF)
3. servlet files are .java files that will handle the logic and should be placed in java folder within the main directory

# Important to know logic

1. JSP is the frontend and will send a request to some servlet via the annotation
2. Servlet files will create a connection through mysql jar file and will send in a query
3. The response is also handled in the Servlet file
