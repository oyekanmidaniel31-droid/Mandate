// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.secrets) apply false
  alias(libs.plugins.google.services) apply false
}

abstract class BuildSignedDebugApkTask : DefaultTask() {
    @get:org.gradle.api.tasks.Input
    val projectRootDir: org.gradle.api.provider.Property<File> = project.objects.property(File::class.java).convention(project.rootDir)

    @TaskAction
    fun copyApk() {
        val rootDirFile = projectRootDir.get()
        val apkOutputDir = File(rootDirFile, "app/build/outputs/apk/debug")
        val targetDir = File(rootDirFile, "outputs")
        if (!targetDir.exists()) targetDir.mkdirs()

        val apkFile = apkOutputDir.listFiles()?.firstOrNull { it.extension == "apk" }
        if (apkFile != null && apkFile.exists()) {
            val destination = File(targetDir, "Mandate-Debug-Signed.apk")
            apkFile.copyTo(destination, overwrite = true)
            println("==================================================")
            println("SUCCESS: Signed Debug APK generated successfully!")
            println("Location: ${destination.absolutePath}")
            println("==================================================")
        } else {
            println("ERROR: Debug APK output file not found in $apkOutputDir.")
        }
    }
}

tasks.register<BuildSignedDebugApkTask>("buildSignedDebugApk") {
    group = "build"
    description = "Builds and copies the signed debug APK using debugConfig for easy sideloading."
    dependsOn(":app:assembleDebug")
    projectRootDir.set(rootDir)
}



