pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
			content {
				includeGroupAndSubgroups("net.fabricmc")
				includeGroupAndSubgroups("fabric-loom")
			}
		}

		mavenCentral()
		gradlePluginPortal()
	}

	plugins {
		id("net.fabricmc.fabric-loom") version providers.gradleProperty("loom_version")
	}
}

System.setProperty("loom.excludeFabricReplacedDependencies", "false")

// Should match your modid
rootProject.name = "polymc"