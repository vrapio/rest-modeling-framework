package io.vrap.rmf.raml.model.util

import com.damnhandy.uri.template.UriTemplate
import io.vrap.rmf.raml.model.modules.Api
import io.vrap.rmf.raml.model.modules.ModulesFactory
import io.vrap.rmf.raml.model.resources.*
import io.vrap.rmf.raml.model.types.Annotation
import io.vrap.rmf.raml.model.types.AnyType
import io.vrap.rmf.raml.model.types.StringAnnotationType
import io.vrap.rmf.raml.model.types.TypesFactory
import spock.lang.Shared
import spock.lang.Specification

/**
 * Unit tests for {@link UriFragmentBuilder}.
 */
class UriFragmentBuilderTest extends Specification {
    @Shared
    UriFragmentBuilder uriFragmentBuilder = new UriFragmentBuilder()

    Api api

    def setup() {
        api = ModulesFactory.eINSTANCE.createApi()
    }

    def "types"() {
        when:
        AnyType anyType = TypesFactory.eINSTANCE.createAnyType()
        anyType.setName("Type")
        api.types.add(anyType)
        then:
        uriFragmentBuilder.getURIFragment(anyType) == '/types/Type'
    }

    def "annotation"() {
        when:
        AnyType anyType = TypesFactory.eINSTANCE.createAnyType()
        anyType.setName("Type")
        api.types.add(anyType)
        StringAnnotationType annotationType = TypesFactory.eINSTANCE.createStringAnnotationType()
        annotationType.setName("AnnotatedName")
        api.annotationTypes.add(annotationType)
        Annotation annotation = TypesFactory.eINSTANCE.createAnnotation()
        annotation.type = annotationType
        anyType.annotations.add(annotation)
        then:
        uriFragmentBuilder.getURIFragment(annotation) == '/types/Type/annotations/AnnotatedName'
    }

    def "resources"() {
        when:
        Resource resource = createResource("root", HttpMethod.GET)
        api.resources.add(resource)
        Resource subResource = createResource("users", HttpMethod.POST)
        resource.resources.add(subResource)
        then:
        uriFragmentBuilder.getURIFragment(resource) == '/resources/root'
        uriFragmentBuilder.getURIFragment(resource.methods[0]) == '/resources/root/methods/get'
        uriFragmentBuilder.getURIFragment(subResource) == '/resources/root/resources/users'
        uriFragmentBuilder.getURIFragment(subResource.methods[0]) == '/resources/root/resources/users/methods/post'
    }

    def "stringInstance"() {
        when:
        Resource resource = createResource("root", HttpMethod.GET)
        api.resources.add(resource)
        resource.description = TypesFactory.eINSTANCE.createStringInstance()
        then:
        uriFragmentBuilder.getURIFragment(resource.description) == '/resources/root/description'
    }

    Resource createResource(String relativeUri, HttpMethod httpMethod) {
        Resource resource = ResourcesFactory.eINSTANCE.createResource()

        UriTemplate uriTemplate = ResourcesFactory.eINSTANCE.createFromString(ResourcesPackage.Literals.URI_TEMPLATE, relativeUri);

        resource.setRelativeUri(uriTemplate)

        Method method = ResourcesFactory.eINSTANCE.createMethod()
        method.setMethod(httpMethod)
        resource.methods.add(method)

        return resource
    }
}
