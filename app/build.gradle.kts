import com.android.build.api.variant.ComponentIdentity

repositories {
    google()
    mavenCentral()
}

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.gradle.jacoco")
}

fun ComponentIdentity.getVersion(): String {
    check(flavorName!!.isEmpty())
    return when (buildType) {
        "debug", "examine" -> listOf(
            android.defaultConfig.versionName!!,
            name,
            android.defaultConfig.versionCode!!.toString(),
        ).joinToString(separator = "-")
        "release" -> listOf(
            android.defaultConfig.versionName!!,
            android.defaultConfig.versionCode!!.toString(),
        ).joinToString(separator = "-")
        else -> error("Build type \"${buildType}\" is not supported!")
    }
}

android {
    namespace = "sample.compose.unittest"
    compileSdk = Version.Android.compileSdk

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                // https://stackoverflow.com/a/71834475/4398606
                it.configure<JacocoTaskExtension> {
                    isIncludeNoLocationClasses = true
                    excludes = listOf("jdk.internal.*")
                }
            }
        }
    }

    defaultConfig {
        applicationId = namespace
        minSdk = Version.Android.minSdk
        targetSdk = Version.Android.targetSdk
        versionCode = 1
        versionName = "0.0.$versionCode"
        manifestPlaceholders["appName"] = "@string/app_name"
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".$name"
            versionNameSuffix = "-$name"
            isMinifyEnabled = false
            isShrinkResources = false
            manifestPlaceholders["buildType"] = name
            enableUnitTestCoverage = true
        }
        getByName("release") {
            applicationIdSuffix = ""
            versionNameSuffix = ""
            isMinifyEnabled = true
            isShrinkResources = true
            manifestPlaceholders["buildType"] = name
            enableUnitTestCoverage = false
        }
        create("examine") {
            val parent = getByName("release")
            initWith(parent)
            sourceSets.getByName(name) {
                res.srcDir("src/${parent.name}/res")
                kotlin.srcDir("src/${parent.name}/kotlin")
            }
            enableUnitTestCoverage = true
            testBuildType = name
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions.kotlinCompilerExtensionVersion = Version.Android.compose
}

jacoco.toolVersion = Version.jacoco

fun checkCoverage(variant: ComponentIdentity) {
    val taskUnitTest = camelCase("test", variant.name, "UnitTest")
    val executionData = layout.buildDirectory.get()
        .dir("outputs/unit_test_code_coverage/${variant.name}UnitTest")
        .file("$taskUnitTest.exec")
    tasks.getByName<Test>(taskUnitTest) {
        doLast {
            check(executionData.asFile.exists())
            val report = layout.buildDirectory.get()
                .dir("reports/tests/$name")
                .file("index.html")
                .asFile
            if (report.exists()) {
                println("Unit test report: ${report.absolutePath}")
            }
        }
    }
    val taskCoverageReport = task<JacocoReport>(camelCase("assemble", variant.name, "CoverageReport")) {
        dependsOn(taskUnitTest)
        reports {
            csv.required = false
            html.required = true
            xml.required = false
        }
        sourceDirectories.setFrom(file("src/main/kotlin"))
        val root = layout.buildDirectory.get()
            .dir("tmp/kotlin-classes")
            .dir(variant.name)
        val dirs = fileTree(root) {
            val rootPackage = android.namespace!!.replace('.', '/')
            include("**/$rootPackage/App.class")
            val path = "**/$rootPackage/module/**"
            setOf("Screen", "ViewModel").forEach { name ->
                include(
                    "$path/*$name.class",
                    "$path/*${name}Kt.class",
                )
            }
        }
        classDirectories.setFrom(dirs)
        executionData(executionData)
        doLast {
            val report = layout.buildDirectory.get()
                .dir("reports/jacoco/$name/html")
                .file("index.html")
                .asFile
            if (report.exists()) {
                println("Coverage report: ${report.absolutePath}")
            }
        }
    }
    task<JacocoCoverageVerification>(camelCase("check", variant.name, "Coverage")) {
        dependsOn(taskCoverageReport)
        violationRules {
            rule {
                limit {
                    minimum = BigDecimal(0.95)
                }
            }
        }
        classDirectories.setFrom(taskCoverageReport.classDirectories)
        executionData(taskCoverageReport.executionData)
    }
}

fun camelCase(first: String, vararg segments: String): String {
    return first + segments.joinToString(separator = "") { it.capitalize() }
}

androidComponents.onVariants { variant ->
    val output = variant.outputs.single()
    check(output is com.android.build.api.variant.impl.VariantOutputImpl)
    output.outputFileName = listOf(rootProject.name, variant.getVersion()).joinToString(separator = "-", postfix = ".apk")
    afterEvaluate {
        tasks.getByName<JavaCompile>(camelCase("compile", variant.name, "JavaWithJavac")) {
            targetCompatibility = Version.jvmTarget
        }
        tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>(camelCase("compile", variant.name, "Kotlin")) {
            kotlinOptions.jvmTarget = Version.jvmTarget
        }
        if (variant.buildType == android.testBuildType) {
            tasks.getByName<JavaCompile>(camelCase("compile", variant.name, "UnitTest", "JavaWithJavac")) {
                targetCompatibility = Version.jvmTarget
            }
            tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>(camelCase("compile", variant.name, "UnitTest", "Kotlin")) {
                kotlinOptions.jvmTarget = Version.jvmTarget
            }
            checkCoverage(variant)
        }
        val checkManifestTask = task(camelCase("checkManifest", variant.name)) {
            dependsOn(camelCase("compile", variant.name, "Sources"))
            doLast {
                val file = "intermediates/merged_manifest/${variant.name}/AndroidManifest.xml"
                val manifest = groovy.xml.XmlParser().parse(layout.buildDirectory.file(file).get().asFile)
                val actual = manifest.getAt(groovy.namespace.QName("uses-permission")).map {
                    check(it is groovy.util.Node)
                    val attributes = it.attributes().mapKeys { (k, _) -> k.toString() }
                    val name = attributes["{http://schemas.android.com/apk/res/android}name"]
                    check(name is String && name.isNotEmpty())
                    name
                }
                val applicationId by variant.applicationId
                val expected = setOf(
                    "$applicationId.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION",
                )
                check(actual.sorted() == expected.sorted()) {
                    "Actual is:\n$actual\nbut expected is:\n$expected"
                }
            }
        }
        tasks.getByName(camelCase("assemble", variant.name)) {
            dependsOn(checkManifestTask)
        }
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.foundation:foundation:${Version.Android.compose}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.compose.ui:ui-test-junit4:${Version.Android.compose}")
}
