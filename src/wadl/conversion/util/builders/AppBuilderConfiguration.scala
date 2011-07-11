package wadl.conversion.util.builders

object AppConfiguration extends ApplicationBuilderComponent with ResourcesBuilderComponent with ResourceBuilderComponent with ParameterBuilderComponent with MethodBuilderComponent {
	
	override val resourceBuilder = new ResourceBuilder()
	
	override val parameterBuilder = new ParameterBuilder()
	
	override val methodBuilder = new MethodBuilder()
	
	override val resourcesBuilder = new ResourcesBuilder()
	
	override val appBuilder = new ApplicationBuilder()
}