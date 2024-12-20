@file:Suppress("NOTHING_TO_INLINE")

import org.jetbrains.intellij.platform.gradle.Constants.Configurations
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "com.dropdrage"
version = "0.36.6"

val targetIde = TargetIde.valueOf(
    (project.properties.getOrDefault("targetIde", TargetIde.AS.name).toString()),
)

val env: MutableMap<String, String> = System.getenv()
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
        bundledPlugins(getPlugins("baseBundledPlugins"))

        when (targetIde) {
            TargetIde.AS -> {
                local(getLocalProperty("AS_LOCAL_PATH"))
                bundledPlugins(getPlugins("asBundledPlugins"))
            }

            TargetIde.ICE -> {
                local(getLocalProperty("ICE_LOCAL_PATH"))
                plugins(getPlugins("icePlugins"))
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
        }
    }

    publishing {
        token.set(env.getOrDefault("PUBLISH_TOKEN", File(getLocalPropertyUnsafe("PUBLISH_TOKEN")!!).readText()))
        channels.set(listOf(env["PUBLISH_CHANNEL"] ?: "default"))
    }

    signing {
        certificateChainFile.set(File(getEnvOrLocalProperty("CERTIFICATE_CHAIN_FILE")))
        privateKeyFile.set(File(getEnvOrLocalProperty("PRIVATE_KEY_FILE")))
        password.set(File(getEnvOrLocalProperty("PRIVATE_KEY_PASSWORD_FILE")).readText(Charsets.UTF_8))
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

configurations {
    named(Configurations.INTELLIJ_PLATFORM_BUNDLED_MODULES) {
        exclude(Configurations.Dependencies.BUNDLED_MODULE_GROUP, "com.jetbrains.performancePlugin")
    }
}


private inline fun getPlugins(key: String): Provider<List<String>> = properties(key).map { it.split(',') }

private inline fun getEnvOrLocalProperty(key: String): String = env.getOrDefault(key, getLocalPropertyUnsafe(key))!!

private inline fun getLocalProperty(key: String): String = getLocalPropertyUnsafe(key) ?: error("File is null")

private fun getLocalPropertyUnsafe(key: String): String? {
    val localProperties = File("local.properties")
    return if (localProperties.isFile) {
        val properties = Properties()
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use(properties::load)
        properties.getProperty(key)
    } else null
}

enum class TargetIde {
    AS, ICE,
}
