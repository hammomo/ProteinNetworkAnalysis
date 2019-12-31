# ProteinNetworkAnalysis

Get into the root folder of the project

1. Create local jre env after building the project
2. Run the jar package
```shell script
> jlink --module-path $PATH_TO_FX_MODS:mods/production --add-modules ProteinNetworkAnalysis --output jre
> jre/bin/java -jar target/ProteinNetworkAnalysis.jar 
```
