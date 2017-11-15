package io.vrap.rmf.raml.model.util;

import com.damnhandy.uri.template.UriTemplate;
import com.google.common.net.MediaType;
import io.vrap.rmf.raml.model.resources.Resource;
import io.vrap.rmf.raml.model.responses.Body;
import io.vrap.rmf.raml.model.responses.BodyContainer;
import io.vrap.rmf.raml.model.types.ObjectType;
import io.vrap.rmf.raml.model.types.ObjectTypeFacet;
import io.vrap.rmf.raml.model.types.Property;
import io.vrap.rmf.raml.model.types.TypedElement;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides helper methods used in our xcore files.
 *
 * We need this because we wrap some java value classes in EMF data types and
 * EMF datatypes can"t expose any methods defined on the wrapped java type.
 */
public class ModelHelper {
    private ModelHelper() {
    }

    public static boolean testPattern(final TypedElement typedElement, final String value) {
        return typedElement.getPattern().test(value);
    }

    public static UriTemplate fullUri(final Resource resource) {
        final UriTemplate relativeUri = resource.getRelativeUri();

        final UriTemplate fullUriTemplate;
        if (relativeUri == null) {
            fullUriTemplate = null;
        } else {
            final Stack<String> uris = new Stack<>();
            uris.push(relativeUri.getTemplate());

            for (Resource parent = resource.getParent(); parent != null; parent = parent.getParent()) {
                uris.push(parent.getRelativeUri().getTemplate());
            }

            final StringBuffer stringBuffer = new StringBuffer();
            while (!uris.empty()) {
                stringBuffer.append(uris.pop());
            }

            final String fullUri = stringBuffer.toString();
            fullUriTemplate = UriTemplate.fromTemplate(fullUri);
        }
        return fullUriTemplate;
    }

    public static List<Resource> allContainedResources(final Resource resource) {
        final List<Resource> allContainedResources = new ArrayList<>(resource.getResources());

        allContainedResources.addAll(resource.getResources().stream()
                .flatMap(r -> allContainedResources(r).stream())
                .collect(Collectors.toList()));

        return allContainedResources;
    }

    public static String resourcePath(final Resource resource) {
        final UriTemplate fullUri = resource.getFullUri();
        return fullUri != null ? fullUri.getTemplate() : "";
    }

    public static String resourcePathName(final Resource resource) {
        final String[] fragments = resourcePath(resource).split("/");

        final LinkedList<String> nonExpressionFragments = Stream.of(fragments)
                .filter(fragment -> !fragment.contains("{"))
                .collect(Collectors.toCollection(LinkedList::new));
        return nonExpressionFragments.isEmpty() ? "" : nonExpressionFragments.getLast();
    }

    public static Body getBody(final BodyContainer container, final String contentType) {
        final MediaType parsedContentType = MediaType.parse(contentType);
        return container.getBodies().stream()
                .filter(body -> body.getContentTypes().stream().filter(mediaType -> parsedContentType.is(mediaType)).findFirst().isPresent())
                .findFirst()
                .orElse(null);
    }


    public static Map<String, Property> getAllPropertiesAsMap(final ObjectTypeFacet objectTypeFacet) {
        if (objectTypeFacet instanceof ObjectType) {
            final ObjectType objectType = (ObjectType) objectTypeFacet;
            return getAllPropertiesAsMapInternal(objectType);
        } else {
            return getPropertiesAsMapInternal(objectTypeFacet);
        }
    }

    /**
     * Returns all properties (with inherited) of the given object type.
     * <p>
     * If an object type specializes the type of an inherited property,
     * the specialize property will be returned by this method.
     */
    public static EList<Property> getAllProperties(final ObjectType objectType) {
        final Collection<Property> values = getAllPropertiesAsMapInternal(objectType).values();
        return ECollections.toEList(values);
    }

    private static Map<String, Property> getAllPropertiesAsMapInternal(final ObjectType objectType) {
        final Map<String, Property> allPropertiesAsMap = new LinkedHashMap<>();
        if (objectType.getType() != null) {
            final ObjectType parent = (ObjectType) objectType.getType();
            allPropertiesAsMap.putAll(getAllPropertiesAsMap(parent));
        }
        allPropertiesAsMap.putAll(getPropertiesAsMapInternal(objectType));
        return allPropertiesAsMap;
    }

    private static Map<String, Property> getPropertiesAsMapInternal(final ObjectTypeFacet objectType) {
        final Map<String, Property> allPropertiesAsMap = new LinkedHashMap<>();
        if (objectType != null) {
            for (final Property property : objectType.getProperties()) {
                allPropertiesAsMap.put(property.getName(), property);
            }
        }
        return allPropertiesAsMap;
    }
}
