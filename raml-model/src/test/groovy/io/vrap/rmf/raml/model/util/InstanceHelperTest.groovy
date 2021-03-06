package io.vrap.rmf.raml.model.util

import io.vrap.rmf.raml.model.RamlModelResult
import io.vrap.rmf.raml.model.types.*
import spock.lang.Specification

/**
 * Unit tests for {@link InstanceHelper}.
 */
class InstanceHelperTest extends Specification {
    ObjectType objectType

    def setup() {
        objectType = TypesFactory.eINSTANCE.createObjectType()
        StringType stringType = TypesFactory.eINSTANCE.createStringType()
        Property property = TypesFactory.eINSTANCE.createProperty()
        objectType.properties.add(property)
        property.name = 'test'
        property.type = stringType
    }

    def "parse"() {
        when:
        String json = '{ test: "Me" }'
        then:
        RamlModelResult<Instance> result = InstanceHelper.parseAndValidate(json, objectType)
        result.validationResults.empty == true
        result.rootObject instanceof ObjectInstance
        ObjectInstance objectInstance = result.rootObject
        objectInstance.getValue('test').value == 'Me'
    }

    def "parseInvalid"() {
        when:
        String json = '{ test: 1 }'
        then:
        RamlModelResult<Instance> result = InstanceHelper.parseAndValidate(json, objectType)
        result.validationResults.empty == false
    }
}
