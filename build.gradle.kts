import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.20"
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "com.dropdrage"
version = "0.34"

val targetIde = TargetIde.valueOf(
    (project.properties.getOrDefault("targetIde", TargetIde.AS.name).toString()),
)

val env: MutableMap<String, String> = System.getenv()
val dir: String = projectDir.parentFile.absolutePath
fun properties(key: String) = providers.gradleProperty(key)

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

kotlin {
    jvmToolchain(properties("javaVersion").get().toInt())
}

dependencies {
    intellijPlatform {
        bundledPlugins(
            "com.intellij.java",
            "org.jetbrains.kotlin",
        )

        when (targetIde) {
            TargetIde.AS -> {
                local(getLocalProperty("AS_LOCAL_PATH"))

                bundledPlugins(properties("asBundledPlugins").map { it.split(',') })
            }

            TargetIde.ICE -> {
                local(getLocalProperty("ICE_LOCAL_PATH"))
                plugins(properties("icePlugins").map { it.split(',') })
            }
        }

        instrumentationTools()
        zipSigner()
    }
}

intellijPlatform {
    buildSearchableOptions = targetIde == TargetIde.ICE

    pluginConfiguration {
        version = project.version.toString()

        ideaVersion {
            sinceBuild = properties("pluginSinceBuild")
            untilBuild = properties("pluginUntilBuild")
        }
    }
//    sandboxContainer = layout.projectDirectory.dir(properties("sandboxDir").get())

    publishing {
        token.set(env["PUBLISH_TOKEN"])
        channels.set(listOf(env["PUBLISH_CHANNEL"] ?: "default"))
    }

    signing {
//        certificateChainFile.set(File(env.getOrDefault("CERTIFICATE_CHAIN", "$dir/pluginCert/chain.crt")))
//        privateKeyFile.set(File(env.getOrDefault("PRIVATE_KEY", "$dir/pluginCert/private.pem")))
//        password.set(File(env.getOrDefault("PRIVATE_KEY_PASSWORD", "$dir/pluginCert/password.txt")).readText(Charsets.UTF_8))
    }

    pluginVerification {
        cliPath.set(file(getLocalProperty("PLUGIN_VERIFIER_CLI_PATH")))

        if (env["PLUGIN_VERIFY_CI"].toBoolean()) {
            ides {
                select {
                    types = listOf(IntelliJPlatformType.AndroidStudio)
                    channels = listOf(ProductRelease.Channel.RELEASE)
                    sinceBuild = properties("pluginSinceBuild")
                    untilBuild = properties("pluginUntilBuild")
                }
            }
            ides {
                select {
                    types = listOf(IntelliJPlatformType.IntellijIdeaCommunity)
                    channels = listOf(ProductRelease.Channel.RELEASE)
                    sinceBuild = properties("pluginSinceBuild")
                    untilBuild = properties("pluginUntilBuild")
                }
            }
        } else {
            ides {
                local(getLocalProperty("AS_LOCAL_PATH"))
            }
            ides {
                local(getLocalProperty("ICE_LOCAL_PATH"))
            }
        }
    }
}

//tasks {
//    buildSearchableOptions {
//        outputDirectory = File("./resources/searchableOptions.xml")
//    }
//}

//tasks {
//    // Set the JVM compatibility versions
//    withType<JavaCompile> {
//        sourceCompatibility = JavaVersion.VERSION_17.toString()
//        targetCompatibility = JavaVersion.VERSION_17.toString()
//    }
//    kotlin {
//        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_17)
////            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
//        }
//    }
////    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
////        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
////    }
//
//    runIde {
//        jvmArguments.add("-Xmx4G")
//        jvmArguments.add("-XX:+UseG1GC")
//    }
//
//    patchPluginXml {
//        sinceBuild.set("232")
//        untilBuild.set("242.*")
//    }
//
//    signPlugin {
//        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
//        privateKey.set(System.getenv("PRIVATE_KEY"))
//        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
//    }
//
//    publishPlugin {
//        token.set(System.getenv("PUBLISH_TOKEN"))
//    }
//}

private fun getLocalProperty(key: String): String {
    val properties = Properties()
    val localProperties = File("local.properties")
    if (localProperties.isFile) {
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use(properties::load)
    } else {
        error("File from not found")
    }

    return properties.getProperty(key)
}

enum class TargetIde {
    AS, ICE,
}
