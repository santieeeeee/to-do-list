<#
SQLite runner removed

This helper script previously provided a convenience wrapper to run the
application with the 'sqlite' profile. SQLite support was removed from the
project due to runtime compatibility issues with Hibernate. Use the default H2
configuration to run the application locally.

Examples:

  .\mvnw.cmd -DskipTests spring-boot:run

  or

  .\mvnw.cmd -DskipTests package
  java -jar target\to-do-list-0.0.1-SNAPSHOT.jar

#>
