<#
.SYNOPSIS
  Run the To-Do List Spring Boot app with the 'sqlite' profile.

.DESCRIPTION
  This script starts the application configured to use the SQLite profile.
  It prefers the Maven Wrapper (mvnw.cmd) if present. Options:
    -Build      : build a jar then run the jar
    -Background : start mvn spring-boot:run (or the jar) in background
    -Port <int> : override server port (default 8080)

.EXAMPLE
  # Run using mvnw in foreground (default)
  .\run-sqlite.ps1

  # Build jar and run in foreground on port 8081
  .\run-sqlite.ps1 -Build -Port 8081

  # Run in background using mvnw
  .\run-sqlite.ps1 -Background
#>
param(
    [switch]$Build,
    [switch]$Background,
    [int]$Port = 8080
)

$ErrorActionPreference = 'Stop'

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $scriptDir

function Run-Mvnw {
    if (Test-Path ".\mvnw.cmd") {
        Write-Host "Using mvnw.cmd to run with profile 'sqlite' on port $Port..."
        & .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=sqlite -Dserver.port=$Port
    } else {
        Write-Host "Using system mvn to run with profile 'sqlite' on port $Port..."
        & mvn spring-boot:run -Dspring-boot.run.profiles=sqlite -Dserver.port=$Port
    }
}

function Build-And-RunJar {
    if (Test-Path ".\mvnw.cmd") {
        Write-Host "Building jar with mvnw.cmd (skip tests)..."
        & .\mvnw.cmd -DskipTests package
    } else {
        Write-Host "Building jar with mvn (skip tests)..."
        & mvn -DskipTests package
    }

    $jar = Get-ChildItem -Path ".\target" -Filter "*.jar" | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if (-not $jar) {
        Write-Error "No jar found in target/. Build failed or no artifact produced."
        exit 1
    }
    $jarPath = $jar.FullName
    $args = @("--spring.profiles.active=sqlite","--server.port=$Port")

    if ($Background) {
        Write-Host "Starting jar in background: $jarPath"
        Start-Process -FilePath "java" -ArgumentList ("-jar", "`"$jarPath`"") + $args
        Write-Host "Started $jarPath in background"
    } else {
        Write-Host "Running jar: $jarPath"
        & java -jar $jarPath @args
    }
}

if ($Build) {
    Build-And-RunJar
} else {
    if ($Background) {
        if (Test-Path ".\mvnw.cmd") {
            Write-Host "Starting mvnw spring-boot:run in background (profile=sqlite)..."
            Start-Process -FilePath ".\mvnw.cmd" -ArgumentList @('spring-boot:run','-Dspring-boot.run.profiles=sqlite','-Dserver.port=$Port')
            Write-Host "Started mvnw in background"
        } else {
            Write-Host "Starting mvn spring-boot:run in background (profile=sqlite)..."
            Start-Process -FilePath "mvn" -ArgumentList @('spring-boot:run','-Dspring-boot.run.profiles=sqlite','-Dserver.port=$Port')
            Write-Host "Started mvn in background"
        }
    } else {
        Run-Mvnw
    }
}
