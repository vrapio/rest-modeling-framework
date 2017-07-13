package io.vrap.rmf.raml.persistence.constructor;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.regex.Pattern;

import static io.vrap.rmf.raml.model.annotations.AnnotationsPackage.Literals.ANNOTATED_ELEMENT__ANNOTATIONS;
import static io.vrap.rmf.raml.model.annotations.AnnotationsPackage.Literals.STRING_INSTANCE;

public class AnnotationsConstructor extends KeyNodeMatchConstructor {
    private final static Pattern PATTERN = Pattern.compile("\\(.+\\)");

    public AnnotationsConstructor() {
        super(STRING_INSTANCE, ANNOTATED_ELEMENT__ANNOTATIONS, PATTERN);
    }

    @Override
    protected Object construct(final String key, final Scope scope) {
        final EObject annotation = EcoreUtil.create(eClass);
        final Scope annotationScope = scope.with(reference);
        annotationScope.setValue(annotation);

        return annotation;
    }
}
