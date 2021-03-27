import io.github.gradlenexus.publishplugin.NexusPublishExtension
import net.kyori.blossom.BlossomExtension
import net.minecraftforge.gradle.user.UserExtension

buildscript {
    repositories {
        mavenCentral()
        maven {
            name = "forge"
            url = uri("http://files.minecraftforge.net/maven")
        }
        maven {
            name = "sonatype"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        maven {
            name = "github"
            url = uri("https://github.com/juanmuscaria/maven/raw/master")
        }
        maven {
            name = "gradle plugins"
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:1.2-1.0.0-SNAPSHOT")
        classpath("gradle.plugin.net.kyori:blossom:1.1.0")
        classpath("io.github.gradle-nexus:publish-plugin:1.0.0")
    }
}

//apply(plugin = )
apply(plugin = "forge")
apply(plugin = "net.kyori.blossom")
apply(plugin = "signing")
apply(plugin = "maven-publish")
apply(plugin = "io.github.gradle-nexus.publish-plugin")

var versionString = "1.0.0"
if (!project.hasProperty("release")) versionString += "-SNAPSHOT"
version = versionString
group = "com.juanmuscaria" // http://maven.apache.org/guides/mini/guide-naming-conventions.html
val modId = "EventAssistant"
this.setProperty("archivesBaseName", "event-assistant")
val minecraft = extensions.findByType<UserExtension>()
val sourceSets = extensions.findByType<SourceSetContainer>()

//Equivalent of minecraft {}
configure<UserExtension> {
    version = "1.7.10-10.13.4.1614-1.7.10"
    runDir = "eclipse"
}

configure<BlossomExtension> {
    replaceToken(mapOf("@{modId}" to modId, "@{version}" to project.version))
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

configure<NexusPublishExtension> {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
configure<PublishingExtension> {
    publications {
        create("mavenJava", MavenPublication::class) {
            from(components.getAt("java"))
        }
    }
    repositories {
        maven {
            name = "local"
            // change URLs to point to your repos, e.g. http://my.org/repo
            val releasesRepoUrl = uri("$buildDir/repos/releases")
            val snapshotsRepoUrl = uri("$buildDir/repos/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}

repositories {
    mavenCentral()
    maven {
        name = "github"
        url = uri("https://github.com/juanmuscaria/maven/raw/master")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

dependencies {
    "compile"("org.bukkit", "craftbukkit", "1.7.10")
    "compileOnly"("org.jetbrains:annotations:16.0.2")
    "compileOnly"("org.projectlombok:lombok:1.18.16")
    "annotationProcessor"("org.projectlombok:lombok:1.18.16")

    "testImplementation"(platform("org.junit:junit-bom:5.7.1"))
    "testImplementation"("org.junit.jupiter:junit-jupiter")
    "testCompileOnly"( "org.projectlombok:lombok:1.18.16")
    "testAnnotationProcessor"( "org.projectlombok:lombok:1.18.16")
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets!!["main"].allJava)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
    from(tasks.named<Javadoc>("javadoc") )
    archiveClassifier.set("javadoc")
}

tasks.named<ProcessResources>("processResources") {
    // this will ensure that this task is redone when the versions change.
    inputs.property("version", project.version)
    inputs.property("mcversion", minecraft?.version)

    // replace stuff in mcmod.info, nothing else
    from(sourceSets!!["main"].resources.srcDirs) {
        include("mcmod.info")

        // replace version and mcversion
        //expand ("version":project.version, "mcversion":minecraft?.version)
        expand(mapOf<String, Any>("version" to project.version, "mcversion" to (minecraft?.version) as Any))
    }

    // copy everything else, thats not the mcmod.info
    from(sourceSets["main"].resources.srcDirs) {
        exclude("mcmod.info")
    }
}

project.plugins.withType<MavenPublishPlugin>().all {
    val publishing = project.extensions.getByType<PublishingExtension>()
    publishing.publications.withType<MavenPublication>().all {
        this.artifactId = "event-assistant"
        this.artifact(tasks["sourcesJar"])
        this.artifact(tasks["javadocJar"])
//        this.versionMapping {
//            usage("java-api") {
//                fromResolutionOf("runtimeClasspath")
//            }
//            usage("java-runtime") {
//                fromResolutionResult()
//            }
//        }
        this.pom {
            name.set("Event Assistant")
            description.set("A set of utilities to deal with both forge and bukkit without the headache of sides")
            url.set("https://github.com/juanmuscaria/Event-Assistant")
            licenses {
                license {
                    name.set("GNU General Public License v3.0")
                    url.set("https://www.gnu.org/licenses/gpl-3.0")
                }
            }
            developers {
                developer {
                    id.set("juanmuscaria")
                    name.set("juanmuscaria")
                    email.set("juanmuscaria@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:https://github.com/juanmuscaria/Event-Assistant")
                developerConnection.set("scm:git:ssh://github.com/juanmuscaria/Event-Assistant.git")
                url.set("https://github.com/juanmuscaria/Event-Assistant")
            }
        }
    }
}

configure<SigningExtension> {
    sign(project.extensions.getByType(PublishingExtension::class).publications.withType(MavenPublication::class)["mavenJava"])
}