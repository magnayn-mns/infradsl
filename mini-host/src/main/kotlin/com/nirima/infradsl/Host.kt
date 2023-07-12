package com.nirima.infradsl

import com.pulumi.Context
import com.pulumi.Pulumi
import com.pulumi.docker.Container
import com.pulumi.docker.ContainerArgs
import com.pulumi.docker.RemoteImage
import com.pulumi.docker.RemoteImageArgs
import nm.SimpleScript
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.PrintStream
import java.nio.file.Paths
import java.util.function.Consumer
import kotlin.reflect.KClass
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

data class ProvidedProperty(val name: String, val type: KClass<*>, val value: Any?) {
    constructor(name: String, type: Class<*>, value: Any?) : this(name, type.kotlin, value)
}

fun evalFile(scriptFile: File , props: List<ProvidedProperty>, context:Context): ResultWithDiagnostics<EvaluationResult> {

    val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<SimpleScript>(){
        jvm {
            // configure dependencies for compilation, they should contain at least the script base class and
            // its dependencies
            // variant 1: try to extract current classpath and take only a path to the specified "script.jar"
           /* dependenciesFromCurrentContext(
                "script-definition" /* script library jar name (exact or without a version) */
            ) */
            // variant 2: try to extract current classpath and use it for the compilation without filtering
                dependenciesFromCurrentContext(wholeClasspath = true)
            // variant 3: try to extract a classpath from a particular classloader (or Thread.contextClassLoader by default)
            // filtering as in the variat 1 is supported too
//            dependenciesFromClassloader(classLoader = SimpleScript::class.java.classLoader, wholeClasspath = true)
            // variant 4: explicit classpath
//            updateClasspath(listOf(File("/path/to/jar")))

        }
        providedProperties(*(props.map { it.name to KotlinType(it.type) }.toTypedArray()))

    }

    val evaluationConfig = ScriptEvaluationConfiguration {
        providedProperties(*(props.map { it.name to it.value }.toTypedArray()))
        constructorArgs(context)
    }


    return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), compilationConfiguration, evaluationConfig)
}

fun main_not(vararg args: String) {
    dumpProps()

    Pulumi.run { ctx: Context ->

        ctx.log().info("zippy")
        demoEnv(ctx)
    }
}

private fun dumpProps() {
    val f = File("/tmp/host.props.txt")
    val fos = FileOutputStream(f)
    val writer = PrintStream(fos)
    System.getenv().entries.forEach(Consumer<Map.Entry<String?, String?>> { (key, value): Map.Entry<String?, String?> ->
        writer.print(key)
        writer.print("=")
        writer.println(value)
    })
    writer.flush()
    writer.close()
}

fun main(vararg args: String) {

    var debug = File("/tmp/log.txt")

    var file:String? = null;

    if (args.size != 1) {
        file = System.getenv("DSL_SCRIPT");
    } else {
        file = args[0];
    }

    if( file == null ) {
        println("usage: <app> <script file>")
        return;
    }

    val scriptFile = File(file)
    println("Executing script $scriptFile")
    debug.appendText("Executing script $scriptFile")

    val currentRelativePath = Paths.get("")
    val s = currentRelativePath.toAbsolutePath().toString()
    println("Current absolute path is: $s")

    val f = File(".") // current directory

    val files = f.listFiles()
    for (file in files) {
        if (file.isDirectory) {
            print("directory:")
        } else {
            print("     file:")
        }
        println(file.canonicalPath)
    }

    debug.appendText("Starting run..")

    var monitor = System.getenv("PULUMI_MONITOR")
    debug.appendText("PULUMI_MONITOR: " + monitor)

    Pulumi.run { ctx: Context ->
        debug.appendText("In Run\n")

        ctx.log().info("zippy")
        demoEnv(ctx)


            val properties = listOf(
                ProvidedProperty("ctx", Context::class, ctx)
            )
            val res = evalFile(scriptFile, properties, ctx)

            res.reports.forEach {
                if (it.severity > ScriptDiagnostic.Severity.DEBUG) {
                    println(" : ${it.message}" + if (it.exception == null) "" else ": ${it.exception}")
                    debug.appendText(" : ${it.message}" + if (it.exception == null) "" else ": ${it.exception}\n")
                    ctx.log().warn(" : ${it.message}" + if (it.exception == null) "" else ": ${it.exception}\n")
                }


            }
        debug.appendText("Completed Run\n")
        ctx.log().info("Completed Run")
    }

    debug.appendText("Exit")


}

fun demoEnv(ctx: Context) {
    val ubuntuRemoteImage = RemoteImage(
        "ubuntuRemoteImage", RemoteImageArgs.builder()
            .name("ubuntu:precise")
            .build()
    )

    val ubuntuContainer = Container(
        "ubuntuContainer", ContainerArgs.builder()
            .image(ubuntuRemoteImage.imageId())
            .build()
    )
}


