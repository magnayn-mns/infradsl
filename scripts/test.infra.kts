
println("Hello, World!")
bibble()
println(bobble("Fish"))
bibble()

println("== " + fish)

scope {
    println("INNER")
}

zippy("chops") {
    println("in bungle")
    println(it)
}
/*infra {
    println("wHOLA")
}*/