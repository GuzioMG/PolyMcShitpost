import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.api.publish.maven.MavenPublication

plugins {
	id("net.fabricmc.fabric-loom")
	id("maven-publish")
	id("org.jetbrains.kotlin.jvm") version "2.4.10"
	id("org.ajoberstar.grgit") version "5.2.2"
}

base {
	archivesName.set(project.property("archives_base_name").toString())
}

val datagenDir = "${rootProject.layout.buildDirectory.get().asFile}/polymc-datagen/${project.property("minecraft_version")}"
version = "${project.property("mod_version")}${getVersionMetadata()}+${project.property("minecraft_version")}"
group = project.property("maven_group").toString()

repositories {
	mavenLocal() // Check local Maven cache first

	maven {
		url = uri("https://jitpack.io")
		content {
			includeGroupAndSubgroups("com.github")
		}
	}

	maven {
		url = uri("https://maven.theepicblock.nl")
		content {
			includeGroup("nl.theepicblock")
		}
	}

	maven {
		url = uri("https://maven.nucleoid.xyz")
		content {
			includeGroup("xyz.nucleoid")
			includeGroup("eu.pb4")
		}
	}

	maven {
		name = "Quilt"
		url = uri("https://maven.quiltmc.org/repository/release")
		content {
			includeGroupAndSubgroups("org.quiltmc")
		}
	}

	maven {
		name = "Modrinth"
		url = uri("https://api.modrinth.com/maven")
		content {
			includeGroupAndSubgroups("maven.modrinth")
		}
	}

	maven {
		name = "Ladysnake Mods"
		url = uri("https://maven.ladysnake.org/releases")
	}
}

sourceSets {
	create("common") {
		runtimeClasspath += sourceSets["main"].runtimeClasspath
		compileClasspath += sourceSets["main"].compileClasspath
	}

	named("main") {
		runtimeClasspath += sourceSets["common"].output
		compileClasspath += sourceSets["common"].output
	}

	create("testmod") {
		runtimeClasspath += sourceSets["main"].runtimeClasspath + sourceSets["main"].output
		compileClasspath += sourceSets["main"].compileClasspath + sourceSets["main"].output
	}

	create("datagen") {
		runtimeClasspath += sourceSets["main"].runtimeClasspath + sourceSets["common"].output
		compileClasspath += sourceSets["main"].compileClasspath + sourceSets["common"].output
	}
}

loom {
	mods {
		create("polymc-datagen") {
			sourceSet(sourceSets["datagen"])
		}

		create("polymc") {
			sourceSet(sourceSets["main"])
		}

		create("polymc-testmod") {
			sourceSet(sourceSets["testmod"])
		}
	}

	runs {
		create("testmodClient") {
			client()
			ideConfigGenerated(project.rootProject == project)
			name = "Test Mod Client"
			source(sourceSets["testmod"])
		}

		create("testmodServer") {
			server()
			ideConfigGenerated(project.rootProject == project)
			name = "Test Mod Server"
			source(sourceSets["testmod"])
		}

		create("gametest") {
			server()
			name("Game Test")
			vmArg("-Dfabric-api.gametest")
			vmArg("-Dfabric-api.gametest.report-file=${layout.buildDirectory.get().asFile}/junit.xml")
			runDir("build/gametest")
			source(sourceSets["testmod"])
		}

		create("datagen") {
			IO.println("Using datagen directory: $datagenDir") //UPDATE: The bit from below doesn't work, either. It's like Java is impervious to getting envars - imma just add a fallback instead, and print the intended directory here to simplify manual interventions.
			server()
			ideConfigGenerated(false)
			vmArg("-Doutput-dir=$datagenDir") //It seems like Fabric overrides custom settings from tasks.named("runDatagen"), so this must be passed in manually.
			source(sourceSets["datagen"])
		}
	}

	accessWidenerPath = file("src/main/resources/polymc.accesswidener")
}

/*fabricApi {
	configureDataGeneration {
		client = true
	}
}*/

dependencies {
	// To change the versions, see the gradle.properties file.
	minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
	implementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

	implementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_version")}")
	implementation("net.fabricmc:fabric-language-kotlin:${providers.gradleProperty("fabric_kotlin_version").get()}")

	implementation(include("nl.theepicblock:resource-locator-api:${project.property("resource_locator_api_version")}")){}
	implementation(include("xyz.nucleoid:packet-tweaker:${project.property("packet_tweaker_version")}")){}
	implementation(include("eu.pb4:polymer-common:${project.property("polymer_version")}")){}
	implementation(include("eu.pb4:polymer-reg-sync-manipulator:${project.property("polymer_version")}")){}

	// Compat
	compileOnly(
		"maven.modrinth:lithium:${project.property("lithium_version")}"
	)

	/*compileOnly(
		"com.github.iPortalTeam.ImmersivePortalsMod:imm_ptl_core:${project.property("immersive_portals_version")}"
	) {
		exclude(
			group = "net.fabricmc",
			module = "fabric-loader"
		)
		isTransitive = false
	}

	compileOnly(
		"com.github.iPortalTeam.ImmersivePortalsMod:q_misc_util:${project.property("immersive_portals_version")}"
	) {
		exclude(
			group = "net.fabricmc",
			module = "fabric-loader"
		)
		isTransitive = false
	}*/

	compileOnly(
		"org.ladysnake.cardinal-components-api:cardinal-components-base:${project.property("cardinal_component_version")}"
	) {
		exclude(
			group = "net.fabricmc",
			module = "fabric-loader"
		)
		isTransitive = false
	}

	// compileOnly("org.quiltmc.qsl.core:registry:${project.property("qsl_version")}") {
	//     isTransitive = false
	// }
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand("version" to project.version)
	}
}

// Grgit exposes a project extension named "grgit".
// The Grgit plugin has known Kotlin-script compatibility limitations,
// so retrieve it through the extension container rather than using
// the Groovy-only `grgit` property.
fun getVersionMetadata(): String {
	val grgit = extensions.findByName("grgit") ?: return ""

	val head = grgit.javaClass.getMethod("head").invoke(grgit)
	val headTag = /*grgit.javaClass
		.getMethod("tag")
		.invoke(grgit)
		.let { tag ->
			tag.javaClass.getMethod("list").invoke(tag)
		}
		.let { tags ->
			(tags as Iterable<*>).find { tag ->
				tag?.javaClass?.getMethod("commit")?.invoke(tag) == head
			}
		}*/null

	// This is a release.
	if (headTag != null) {
		return ""
	}

	val id = null//head.javaClass.getMethod("abbreviatedId").invoke(head).toString()

	val status = grgit.javaClass.getMethod("status").invoke(grgit)
	val clean = false//status.javaClass.getMethod("clean").invoke(status) as Boolean

	return if (clean) {
		"-rev.$id"
	} else {
		"-rev.${id}-dirty"
	}
}

// Ensure that the encoding is UTF-8, no matter what the system default is.
tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
	options.release = 25
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_25
	}
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
	from("LICENSE") {
		rename {
			"${it}_${project.property("archives_base_name")}"
		}
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	repositories {
		maven {
			name = "teb"
			credentials(PasswordCredentials::class)
			url = uri("https://maven.theepicblock.nl")
		}
	}
}


//Ported custom tasks

tasks.register<Jar>("testmodJar") {
	archiveClassifier.set("testmod-dev")
	from(sourceSets["testmod"].output)
}

tasks.register<RemapJarTask>("remapTestmodJar") {
	dependsOn("testmodJar")

	archiveClassifier.set("testmod")
	input.set(
		tasks.named<Jar>("testmodJar").flatMap { it.archiveFile }
	)
	addNestedDependencies = false
}

// This includes the resources in build/polymc-datagen/<version>/ into the jar.
sourceSets["main"].resources.srcDir(datagenDir)

tasks.named("runDatagen") {
	doFirst {
		logger.info("This task was NOT overriden by Fabric.")
	}
	// Loom's environment DSL is supplied by the run task.
	// This remains equivalent to the original Groovy configuration.
	extensions.extraProperties["output-dir"] = datagenDir
}

tasks.named("build") {
	doLast {
		if (!file(datagenDir).exists()) {
			logger.error("!! Datagen hasn't been run. Vanilla ids aren't included in the build")
		}
	}
}

tasks.register("getClientSha1") {
	doLast {
		val loomExt = net.fabricmc.loom.LoomGradleExtension.get(project)
		val mcProvider = loomExt.minecraftProvider
		println(mcProvider.versionInfo.download("client").sha1())
	}
}