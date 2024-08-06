plugins {
	alias(libs.plugins.fabric.loom)
	id("maven-publish")
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlinx.serialization)
}

version = libs.versions.mod.version.get()
group = project.extra["maven_group"]!!

base {
	//archivesName = project.archives_base_name
}

repositories {
	maven("https://api.modrinth.com/maven") {
		name = "modrinth"

	}
	maven("https://minecraft.curseforge.com/api/maven") {
		name = "curseforge"
	}
	maven("https://libraries.minecraft.net")
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft(libs.minecaft)
	mappings("net.fabricmc:yarn:${libs.versions.yarn.mappings.get()}:v2")
	modImplementation(libs.fabric.loader)

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation(libs.fabric.api)
	modImplementation(libs.fabric.kotlin)
	modImplementation(libs.admiral)
	modImplementation("com.mojang:brigadier:1.0.18")

	// Uncomment the following line to enable the deprecated Fabric API modules. 
	// These are included in the Fabric API production distribution and allow you to update your mod to the latest modules at a later more convenient time.

	// modImplementation "net.fabricmc.fabric-api:fabric-api-deprecated:${project.fabric_version}"

}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand("version" to project.version)
	}
}

kotlin {
	jvmToolchain(17)
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${project.base.archivesName.get()}"}
	}
}

/*
// configure the maven publication
publishing {
	publications {
		mavenJava(MavenPublication) {
			from components.java
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}
 */