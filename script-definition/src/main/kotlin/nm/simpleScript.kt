package nm

import com.pulumi.Context
import com.pulumi.docker.RemoteImage
import com.pulumi.docker.RemoteImageArgs
import kotlin.script.experimental.annotations.KotlinScript

// The KotlinScript annotation marks a class that can serve as a reference to the script definition for
// `createJvmCompilationConfigurationFromTemplate` call as well as for the discovery mechanism
// The marked class also become the base class for defined script type (unless redefined in the configuration)
@KotlinScript(
    // file name extension by which this script type is recognized by mechanisms built into scripting compiler plugin
    // and IDE support, it is recommendend to use double extension with the last one being "kts", so some non-specific
    // scripting support could be used, e.g. in IDE, if the specific support is not installed.
    fileExtension = "infra.kts"
)
// the class is used as the script base class, therefore it should be open or abstract
abstract class SimpleScript(val context:Context) {
    fun bibble() {
        println("Hello there freaks")
    }

    fun bobble(x:String):String {
        return "Hello, $x"
    }

    val fish: Int // property type is optional since it can be inferred from the getter's return type
        get() = 100

    fun scope( x:() -> Unit) {
        println("+scope")
        x()
        println("-scope")
    }

    fun alpine( x:() -> Unit) {

        val alpine = RemoteImage(
            "alpine", RemoteImageArgs.builder()
                .name("alpine:latest")
                .build()
        )

        x()

        context.log().info("alpine")
    }

    fun zippy(href:String, init:(String)->Unit) {
        println("+zippy " + href)
        init(href)
        println("-zippy")
    }
}