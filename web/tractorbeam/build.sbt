name := "starship-web-tractorbeam"

unmanagedSourceDirectories in IntegrationTest <<= (baseDirectory in IntegrationTest)(base =>  Seq(base / "it"))

//Enabling Fork for test in web to provide Java Options
fork in Test := true

//Disable the mongodb evolutions plugin while running tests in web
javaOptions in Test += "-Dmongodb.evolution.enabled=false"

//
//Publish settings for play web distributable
//
//Do not publish web jar
publishArtifact in (Compile, packageBin) := false

//Do not publish web doc
publishArtifact in (Compile, packageDoc) := false

//Do not publish web src
publishArtifact in (Compile, packageSrc) := false

//Do not append _2.11 to the artifact zip file.
crossPaths := false
