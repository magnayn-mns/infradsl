package nm

import java.io.File
import java.nio.file.Paths
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import com.pulumi.Context
import com.pulumi.Pulumi
import kotlin.reflect.KClass
import kotlin.script.experimental.api.*

data class ProvidedProperty(val name: String, val type: KClass<*>, val value: Any?) {
    constructor(name: String, type: Class<*>, value: Any?) : this(name, type.kotlin, value)
}

fun evalFile(scriptFile: File , props: List<ProvidedProperty>): ResultWithDiagnostics<EvaluationResult> {

    val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<SimpleScript>(){
        jvm {
            // configure dependencies for compilation, they should contain at least the script base class and
            // its dependencies
            // variant 1: try to extract current classpath and take only a path to the specified "script.jar"
            dependenciesFromCurrentContext(
                "script-definition" /* script library jar name (exact or without a version) */
            )
            // variant 2: try to extract current classpath and use it for the compilation without filtering
        //    dependenciesFromCurrentContext(wholeClasspath = true)
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
    }


    return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), compilationConfiguration, evaluationConfig)
}

fun main(vararg args: String) {

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

        Pulumi.run { ctx: Context ->

            val properties = listOf(
                ProvidedProperty("ctx", Context::class, ctx)
            )
            val res = evalFile(scriptFile, properties)

            res.reports.forEach {
                if (it.severity > ScriptDiagnostic.Severity.DEBUG) {
                    println(" : ${it.message}" + if (it.exception == null) "" else ": ${it.exception}")
                }
            }
        }



}


