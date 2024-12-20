<div align="center">
	<img src="src/main/resources/META-INF/pluginIcon.svg" width="200" height="200">
    <h1>Simple Compose Preview Generator</h1>
    <a href="https://plugins.jetbrains.com/plugin/25536-simple-compose-preview-generator"><img src="https://img.shields.io/jetbrains/plugin/v/25536-simple-compose-preview-generator.svg"/></a>
    <a href="https://plugins.jetbrains.com/plugin/25536-simple-compose-preview-generator"><img src="https://img.shields.io/jetbrains/plugin/d/25536-simple-compose-preview-generator"/></a>
	<br/>
	<br/>
</div>

A plugin for generation of Jetpack Compose Preview functions with arguments and theme quickly and without the
Internet.  
After generation all the arguments can be edited with IDE smart autocompletion. Arguments of some types are provided
with [default simplifying elements](#types-with-default-simplifying-elements) (e.g. `String` argument is generated with
`""` value).

Can be run for:

- A single function using `Alt`+`Enter` (Windows/Linux) / `⌥⏎` (macOS) -> Generate Compose Preview
- All the functions in file without preview using `Alt<`+`Insert` (Windows/Linux) / `⌘N` (macOS) -> Generate All
  Remaining Compose Previews

![](media/Demo_Single.gif)
![](media/Demo_Multiple.gif)

### Configuration

- General:
    - Preview function name suffix
- Code Style:
    - First annotation (`@Preview`, `@Composable`)
    - Preview function body type (Expression =, Block {}")
    - Preview function location (End of file, After function)
    - Trailing comma
    - Force single blank line before Preview
- Arguments Generation:
    - Generate function arguments with default values
    - Skip ViewModel
    - Generate Modifier
    - Assign null values for nullable arguments
    - Add theme
    - Use empty collection builders (Array, "Iterable, Collection, List", Set, Map, Sequence,)

### Types with default simplifying elements

- Nullable types (disabled by default)
- Enums
- Lambdas
- `Char`
- `String`, `CharSequence`
- `Unit`, `Nothing`
- `Modifier`
- Arrays: `Array`, `IntArray`, `UIntArray`, `FloatArray` and etc.
- Iterables: `Iterable`, `Collection`, `List`, `MutableList`, `AbstractList`, `ArrayList`
- Sets: `Set`, `MutableSet`, `HashSet`, `LinkedHashSet`, `SortedSet`, `TreeSet`
- Maps: `Map`, `MutableMap`, `HashMap`, `LinkedHashMap`, `SortedMap`
- `Sequence`
- Kotlin Immutable Collections: `ImmutableCollection`, `ImmutableList`, `ImmutableSet`, `ImmutableMap` and same for
  `Persistent` ones

### Link

https://plugins.jetbrains.com/plugin/25536-simple-compose-preview-generator
